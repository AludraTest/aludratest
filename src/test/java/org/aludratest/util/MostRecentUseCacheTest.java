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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.aludratest.util.MostRecentUseCache.Factory;
import org.aludratest.util.MostRecentUseCache.HashCalculator;
import org.junit.Test;

public class MostRecentUseCacheTest {

    @Test
    public void testHashCode() {
        MostRecentUseCache<Helper1, String> cache = new MostRecentUseCache<Helper1, String>(helperFactory, 10);

        Helper1 key1 = new Helper1("Test1");
        Helper1 key2 = new Helper1("Test2");
        assertEquals("Test1", cache.get(key1));
        assertEquals("Test1", cache.get(key2)); // because same hash code and value exists
        assertEquals(1, cache.getSize());

        cache.clear();
        assertEquals("Test2", cache.get(key2));
        assertEquals("Test2", cache.get(key1));
        assertEquals(1, cache.getSize());

        // this also tests the reuse of objects, so no special test for reusing objects.
    }

    @Test
    public void testMaxSize() {
        CountingFactory factory = new CountingFactory();
        MostRecentUseCache<String, String> cache = new MostRecentUseCache<String, String>(factory, 3);
        cache.get("Test1");
        cache.get("Test2");
        cache.get("Test3");
        assertEquals(1, factory.createCounter.get("Test1").intValue());
        assertEquals(1, factory.createCounter.get("Test2").intValue());
        assertEquals(1, factory.createCounter.get("Test3").intValue());
        assertEquals(3, cache.getSize());
        cache.get("Test4");
        assertEquals(1, factory.createCounter.get("Test4").intValue());
        assertEquals(3, cache.getSize());
        cache.get("Test1");
        cache.get("Test3");
        cache.get("Test4");
        assertEquals(3, cache.getSize());
        assertEquals(2, factory.createCounter.get("Test1").intValue());
        assertEquals(1, factory.createCounter.get("Test2").intValue());
        assertEquals(1, factory.createCounter.get("Test3").intValue());
        assertEquals(1, factory.createCounter.get("Test4").intValue());
    }

    @Test
    public void testHashCalculator() {
        HashCalculator<Helper1> calculator = new HashCalculator<MostRecentUseCacheTest.Helper1>() {
            @Override
            public String hash(Helper1 object) {
                return object.value; // FULL "HASH"
            }
        };
        MostRecentUseCache<Helper1, String> cache = new MostRecentUseCache<Helper1, String>(helperFactory, 10, calculator);

        Helper1 key1 = new Helper1("Test1");
        Helper1 key2 = new Helper1("Test2");
        assertEquals("Test1", cache.get(key1));
        assertEquals("Test2", cache.get(key2));
        assertEquals(2, cache.getSize());

        cache.clear();
        assertEquals("Test2", cache.get(key2));
        assertEquals("Test1", cache.get(key1));
        assertEquals(2, cache.getSize());
    }

    @Test
    public void testThreadSafety() {
        CountingFactory factory = new CountingFactory();
        final MostRecentUseCache<String, String> cache = new MostRecentUseCache<String, String>(factory, 10);
        Runnable runGet = new Runnable() {
            @Override
            public void run() {
                try {
                    // give all threads the chance to start parallel
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    return;
                }
                cache.get("Test1");
                cache.get("Test2");
                cache.get("Test3");
                cache.get("Test4");
            }
        };

        ExecutorService svc = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            svc.execute(runGet);
        }
        svc.shutdown();
        try {
            svc.awaitTermination(2000, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            return;
        }

        assertEquals(1, factory.createCounter.get("Test1").intValue());
        assertEquals(1, factory.createCounter.get("Test2").intValue());
        assertEquals(1, factory.createCounter.get("Test3").intValue());
        assertEquals(1, factory.createCounter.get("Test4").intValue());
    }

    private static class Helper1 {
        private String value;

        public Helper1(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private static final Factory<Helper1, String> helperFactory = new MostRecentUseCache.Factory<Helper1, String>() {
        @Override
        public String create(Helper1 key) {
            return key.value;
        }
    };

    private static class CountingFactory implements Factory<String, String> {

        private Map<String, Integer> createCounter = new HashMap<String, Integer>();

        @Override
        public String create(String key) {
            Integer i = createCounter.get(key);
            i = (i == null ? 1 : (i.intValue() + 1));
            createCounter.put(key, i);

            return key;
        }
    }

}
