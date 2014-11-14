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
package org.aludratest.scheduler.impl;

import org.aludratest.scheduler.PoolSizeProvider;

/**
 * {@link PoolSizeProvider} implementation which reads 
 * the system property <code>hierasched.parallel.poolsize</code> 
 * to determine the parallel thread pool size to be used for 
 * test execution.
 * @author Volker Bergmann
 */
public class SysPropPoolSize implements PoolSizeProvider {

    /** The name of the JVM setting to use for configuration: 
     *  'hierasched.parallel.poolsize' */
    public static final String PROPERTY = "hierasched.parallel.poolsize";

    /** Evaluates the system property 'hierasched.parallel.poolsize'. */
    public Integer getPoolSize() {
        Integer poolSize;
        String cfg = System.getProperty(PROPERTY);
        if (cfg != null && cfg.length() > 0) {
            poolSize = Integer.parseInt(cfg);
        } else {
            poolSize = null;
        }
        return poolSize;
    }

}
