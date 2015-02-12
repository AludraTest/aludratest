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

import org.aludratest.util.ObjectPool;
import org.junit.Test;

/**
 * Tests the {@link ObjectPool}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class ObjectPoolTest {

    @Test
    public void testCapacityUsed() {
        ObjectPool<Integer> pool = new ObjectPool<Integer>(2, true);
        pool.add(1);
        pool.add(2);
    }

    @Test(expected = IllegalStateException.class)
    public void testCapacityExceeded() {
        ObjectPool<Integer> pool = new ObjectPool<Integer>(2, true);
        pool.add(1);
        pool.add(2);
        pool.add(3);
    }

    @Test
    public void testDupsAllowed() {
        ObjectPool<Integer> pool = new ObjectPool<Integer>(2, true);
        pool.add(1);
        pool.add(1);
        assertEquals(2, pool.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDupsForbidden() {
        ObjectPool<Integer> pool = new ObjectPool<Integer>(2, false);
        pool.add(1);
        pool.add(1);
    }

    @Test
    public void testAcquireRelease() throws Exception {
        ObjectPool<Integer> pool = new ObjectPool<Integer>(2, true);
        pool.add(1);
        pool.add(2);
        assertEquals(2, pool.size());
        Integer o1 = pool.acquire();
        assertEquals(1, pool.size());
        Integer o2 = pool.acquire();
        assertEquals(0, pool.size());
        pool.release(o1);
        assertEquals(1, pool.size());
        pool.release(o2);
        assertEquals(2, pool.size());
    }

}
