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
package org.aludratest.scheduler;

import org.aludratest.scheduler.node.RunnerLeaf;

/**
 * Common interface for factory classes that provide
 * proxies to {@link RunnerLeaf} objects.
 * This can be used to track test execution and results.
 * @see org.aludratest.junit.JUnitWrapperFactory
 * @author Volker Bergmann
 */
public interface WrapperFactory {

    /** Callback method invoked by the AludraTest framework and retrieves a {@link RunnerLeaf} object to be wrapped. Implementors
     * of this method shall return a proxy object that also implements the {@link RunnerLeaf} interface and forwards calls to the
     * object provided by the invoker. */
    RunnerLeaf wrap(RunnerLeaf leaf);

}
