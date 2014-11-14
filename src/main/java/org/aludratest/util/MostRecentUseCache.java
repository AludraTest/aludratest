/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** This class implements a MRU (Most recently used), factory-based cache. When querying for a given key of type <code>K</code> for
 * which no value of type <code>V</code> is already stored, the factory of the cache is used to create an object of type
 * <code>V</code> (using the key as factory input parameter). In the internal map, the value is stored as value for the <i>Hash
 * Value</i> of the used key. The hash value calulcation defaults to {@link Object#hashCode()}, but can be overridden when
 * providing an own {@link HashCalculator}. <br>
 * After retrieving the object (either an already cached or newly created one), the hash value is inserted as first (or moved to
 * first) in the internal MRU list. If the list has more elements than the maximum for the cache, the least recently used value
 * (the last element in the list) is removed from the cache. The next query for the key of the removed value will cause the value
 * to be newly created using the factory. <br>
 * <br>
 * This class is fully thread-safe.
 * 
 * @author falbrech
 * 
 * @param <K> Type of the key elements of this cache.
 * @param <V> Type of the values being stored in this cache. */
public final class MostRecentUseCache<K, V> {

    /** Interface for factories being able to create an object of type <code>O</code> when getting an input object of type
     * <code>I</code>.
     * 
     * @author falbrech
     * 
     * @param <I> Type of input objects, being passed to {@link #create(Object)} as parameter.
     * @param <O> Type of output objects, the return type of the {@link #create(Object)} method. */
    public static interface Factory<I, O> {

        /** Creates an object from a given input value.
         * 
         * @param key Input value.
         * 
         * @return Created object. */
        public O create(I key);
    }

    /** Interface for hash value calculators. A hash value is encoded as String to be able to calculate long hashes.
     * 
     * @author falbrech
     * 
     * @param <E> Type of objects this calculator can calculate hash values for. */
    public static interface HashCalculator<E> {

        /** Calculate the hash value for the given object.
         * 
         * @param object Object to calculate the hash value for.
         * 
         * @return Hash value for the given object. */
        public String hash(E object);
    }

    private Map<String, V> values = new HashMap<String, V>();

    private Factory<K, V> factory;

    private HashCalculator<K> hashCalculator;

    private int maxSize;

    private Set<String> lockedHashes = new HashSet<String>();

    private List<String> mostRecentUseList = new LinkedList<String>();

    /** Creates a new MRU Cache with the given factory and the given maximum capacity. The hashes of used keys are calculated using
     * {@link Object#hashCode()}.
     * 
     * @param factory Factory to use to create new values for given keys.
     * @param maxSize Maximum size for this cache. */
    public MostRecentUseCache(Factory<K, V> factory, int maxSize) {
        this(factory, maxSize, new HashCodeCalculator<K>());
    }

    /** Creates a new MRU Cache with the given factory, the given maximum capacity, and the given calculator for hash values for
     * used keys.
     * 
     * @param factory Factory to use to create new values for given keys.
     * @param maxSize Maximum size for this cache.
     * @param hashCalculator The calculator to use to calculate a hash value for a given key. */
    public MostRecentUseCache(Factory<K, V> factory, int maxSize, HashCalculator<K> hashCalculator) {
        this.factory = factory;
        this.maxSize = maxSize;
        this.hashCalculator = hashCalculator;
    }

    /** Retrieves the value for the given key object. If the key's hash value is not yet (or no more) registered in this cache, the
     * factory of the cache is used to create a new object. After creation, the key's hash value is inserted as first (or moved to
     * first) in the cache's MRU list. If the MRU list has exceeded maximum size, the value to the last hash value stored in the
     * list is removed from the cache.
     * 
     * @param key Key to retrieve the value for.
     * 
     * @return The value for the given key, either reused from cache, or newly created using the factory. */
    public V get(K key) {
        String hash = hash(key);
        acquireLock(hash);
        try {
            V value = internalGetValue(hash);
            if (value != null) {
                reused(hash);
                return value;
            }
            value = factory.create(key);
            internalSetValue(hash, value);
            addUsed(hash);
            return value;
        }
        finally {
            releaseLock(hash);
        }
    }

    /** Clears this cache. */
    public void clear() {
        synchronized (mostRecentUseList) {
            mostRecentUseList.clear();
        }
        synchronized (values) {
            values.clear();
        }
    }

    /** Returns the current size of this cache. Once a cache has reached its maximum size, this will always return the same
     * (maximum) size, unless {@link #clear()} is invoked to clear the cache.
     * 
     * @return The current size of this cache. */
    public int getSize() {
        synchronized (values) {
            return values.size();
        }
    }

    private String hash(K key) {
        return hashCalculator.hash(key);
    }

    private void acquireLock(String hash) {
        synchronized (lockedHashes) {
            while (lockedHashes.contains(hash)) {
                try {
                    lockedHashes.wait();
                }
                catch (InterruptedException e) {
                    return;
                }
            }
            lockedHashes.add(hash);
        }
    }

    private void releaseLock(String hash) {
        synchronized (lockedHashes) {
            if (lockedHashes.contains(hash)) {
                lockedHashes.remove(hash);
                lockedHashes.notifyAll();
            }
        }
    }

    private V internalGetValue(String hash) {
        synchronized (values) {
            return values.get(hash);
        }
    }

    private void internalSetValue(String hash, V value) {
        synchronized (values) {
            values.put(hash, value);
        }
    }

    private void internalRemoveValue(String hash) {
        synchronized (values) {
            values.remove(hash);
        }
    }

    private void reused(String hash) {
        synchronized (mostRecentUseList) {
            mostRecentUseList.remove(hash);
            mostRecentUseList.add(0, hash);
        }
    }

    private void addUsed(String hash) {
        synchronized (mostRecentUseList) {
            mostRecentUseList.add(0, hash);
            while (mostRecentUseList.size() > maxSize) {
                internalRemoveValue(mostRecentUseList.get(maxSize));
                mostRecentUseList.remove(maxSize);
            }
        }
    }

    private static class HashCodeCalculator<E> implements HashCalculator<E> {
        @Override
        public String hash(E object) {
            return object == null ? "" : Integer.toHexString(object.hashCode());
        }
    }

}
