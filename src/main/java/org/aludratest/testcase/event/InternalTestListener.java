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
package org.aludratest.testcase.event;


/** Internal adapter to be used for AludraTestContextImpl to decouple step group and step creation logic from test context. <b>Do
 * not use nor implement outside AludraTest!</b>
 * 
 * @author falbrech */
public interface InternalTestListener {

    public void newTestStepGroup(String name);

    public void newTestStep(TestStepInfo testStep);

}
