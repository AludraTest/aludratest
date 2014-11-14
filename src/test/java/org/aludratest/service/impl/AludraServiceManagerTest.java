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
package org.aludratest.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.aludratest.AludraTest;
import org.aludratest.AludraTestTest;
import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.junit.Assert;
import org.junit.Test;
import org.test.testclasses.service.SingletonComponent;
import org.test.testclasses.service.SingletonComponentImpl;

public class AludraServiceManagerTest {

    @Test
    public void testSingletonRaceCondition() throws Exception {
        AludraTestTest.setInstance(null);
        new AludraTest();
        // register singletonComponent service
        System.setProperty("ALUDRATEST_CONFIG/aludraservice/" + SingletonComponent.class.getName(),
                SingletonComponentImpl.class.getName());

        // start 100 Threads, check if some get same object
        List<SingletonRaceTestCallable> callables = new ArrayList<SingletonRaceTestCallable>();
        for (int i = 0; i < 100; i++) {
            callables.add(new SingletonRaceTestCallable());
        }

        List<Future<SingletonComponent>> futures = Executors.newFixedThreadPool(100).invokeAll(callables);
        Set<SingletonComponent> objs = new HashSet<SingletonComponent>();

        for (Future<SingletonComponent> future : futures) {
            objs.add(future.get());
        }

        Assert.assertEquals(1, objs.size());
    }

    @Test
    public void testImplementationOverrides() throws Exception {
        String propertyName = "ALUDRATEST_CONFIG/aludraservice/" + AludraTestConfig.class.getName();
        System.getProperties().remove(propertyName);

        AludraTestTest.setInstance(null);
        new AludraTest();

        Assert.assertFalse(AludraTest.getInstance().getServiceManager().newImplementorInstance(AludraTestConfig.class) instanceof AludraTestingTestConfigImpl);

        System.setProperty(propertyName, AludraTestingTestConfigImpl.class.getName());

        // the only way to get a new, clean IoC container
        AludraTestTest.setInstance(null);
        new AludraTest();

        Assert.assertTrue(AludraTest.getInstance().getServiceManager().newImplementorInstance(AludraTestConfig.class) instanceof AludraTestingTestConfigImpl);

        System.getProperties().remove(propertyName);
    }

    private static class SingletonRaceTestCallable implements Callable<SingletonComponent> {

        @Override
        public SingletonComponent call() throws Exception {
            return AludraTest.getInstance().getServiceManager().newImplementorInstance(SingletonComponent.class);
        }
    }

}
