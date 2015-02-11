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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides object pool functionality with a limited pool size.
 * @param <E> the type of the pooled objects
 * @author Volker Bergmann
 */
public class ObjectPool<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectPool.class);

    private boolean dupsAllowed;
    
    private BlockingQueue<E> pool;

    /** Constructor
     *  @param size the maximum number of objects allowed in the pool
     *  @param dupsAllowed specifies if several equal objects 
     *      (in the sense of the equals() method) may put into the pool */
    public ObjectPool(int size, boolean dupsAllowed) {
        this.dupsAllowed = dupsAllowed;
        this.pool = (size > 0 ? new ArrayBlockingQueue<E>(size) : null);
    }

    /**  @return the current number of elements in the pool. */
    public int size() {
        return (pool != null ? pool.size() : 0);
    }

    /** Adds an element to the pool.
     *  @param element the element to add */
    public synchronized void add(E element) {
        if (!dupsAllowed && this.pool.contains(element)) {
            throw new IllegalArgumentException("Duplicate element not accepted in pool: " + element);
        }
        pool.add(element);
    }

    /** Acquires an element from the pool.
     *  @return the acquired element
     *  @throws InterruptedException */
    public synchronized E acquire() throws InterruptedException {
        return pool.take();
    }

    /** Puts back an element into the pool.
     *  @param element The element to put back into the pool */
    public void release(E element) {
        if (!pool.contains(element)) {
            this.add(element);
        } else {
            LOGGER.warn("{} released twice: {}", element.getClass().getSimpleName(), element);
        }
    }

}
