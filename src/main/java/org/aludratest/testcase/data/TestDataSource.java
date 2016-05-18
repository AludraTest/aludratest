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
package org.aludratest.testcase.data;

import org.aludratest.dict.Data;

/** Interface used by TestCaseData class for deferred data evaluation, if supported by the TestDataProvider. Use appropriate
 * TestCaseData constructor, and the TestCaseData object will call this interface's {@link #getData()} method each time the
 * TestCaseData object is asked for its data.
 *
 * @author falbrech */
public interface TestDataSource {

    /** Returns the data provided by this source.
     *
     * @return The data provided by this source. May be <code>null</code> if no data is available. */
    public Data[] getData();

}
