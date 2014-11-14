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

/** Interface for classes which provide data.
 * @param <E> the type of the provided value
 * @author Volker Bergmann */
public interface Provider<E> {

    /** Returns the name of the provider
     * @return the name of the provider */
    String getName();

    /** Returns the value to provide.
     * @return the value to provide */
    E getValue();

}
