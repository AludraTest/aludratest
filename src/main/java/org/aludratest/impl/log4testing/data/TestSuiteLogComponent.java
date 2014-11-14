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
package org.aludratest.impl.log4testing.data;

import org.aludratest.impl.log4testing.configuration.ConfigurationError;

/** 
 * Common parent class for all test objects that can be part of a test suite: 
 * {@link TestSuiteLog}s and {@link TestCaseLog}.
 * @author Volker Bergmann
 */
public abstract class TestSuiteLogComponent extends TestStepContainer {

    /** Parent suite of the component */
    protected TestSuiteLog parent;

    /** Constructor with component name */
    public TestSuiteLogComponent(String name) {
        super(name);
    }

    /** @return the parent suite of the component */
    public TestSuiteLog getParent() {
        return parent;
    }

    /** Sets the parent suite of the component */
    public void setParent(TestSuiteLog parent) {
        if (this.parent != null) {
            throw new ConfigurationError(this + " already used in " + this.parent + ". " + "Tried to add it additionally to " + parent);
        }
        this.parent = parent;
    }

    /** Tells if the component has finished execution. */
    public abstract boolean isFinished();

}
