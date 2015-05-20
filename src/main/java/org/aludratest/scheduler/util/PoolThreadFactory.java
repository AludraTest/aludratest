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
package org.aludratest.scheduler.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link ThreadFactory} implementation which names the created
 * {@link Thread}s by a given name plus an incremental thread number.
 * @author Marcel Malitz
 */
public class PoolThreadFactory implements ThreadFactory {

    /**
     * The number of pools respectivley the number of instances of this
     * class.
     */
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    /**
     * The number of threads created by this factory.
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Newly by this factory created group get added to this ThreadGroup.
     */
    private final ThreadGroup group;

    /**
     * Creates a new instance of {@link PoolThreadFactory} and increments
     * the internal pool counter.
     * 
     * @param poolName
     *            is used for the name of the internal ThreadGroup
     *            {@link #group}.
     */
    public PoolThreadFactory(String poolName) {
        group = new ThreadGroup(poolName + "-" + POOL_NUMBER.getAndIncrement());
    }

    /**
     * Creates a new thread which belongs to the ThreadGroup of this factory
     * instance and which gets a unique name with the information about the
     * group name, that it is a thread and its number in the ThreadGroup.
     */
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
    }

}
