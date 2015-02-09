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
package org.aludratest.util.poll;

/** Interface for operations to be invoked repeatedly until they succeed (polling). The task is performed by the {@link #run()}
 * method. If execution fails, this method has to return null, and the framework reschedules the task for repeated invocation. If
 * the operation is successful, {@link #run()} has to return a non-null value.
 * @author Volker Bergmann
 * @param <E> Type of the value to be returned by the task */
public interface PolledTask<E> {

    /** The operation to execute.
     *  @return a non-null operation result if successful, otherwise null. */
    E run();

    /** Called by the framework when a task did not succeed within the timeout period. The implementor may return an appropriate
     * result value E as fallback or throw an exception to indicate failure
     * @return an exception that describes why the task failed */
    E timedOut();

    /** Creates a String representation of the task and is used for logging. */
    @Override
    String toString();

}