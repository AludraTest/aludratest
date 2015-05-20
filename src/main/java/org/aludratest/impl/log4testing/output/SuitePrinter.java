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
package org.aludratest.impl.log4testing.output;

import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.impl.log4testing.data.TestSuiteLogComponent;

/** 
 * Prints a test suite hierarchy to the console recursively.
 * @author Volker Bergmann
 */
public class SuitePrinter {

    /** Private constructor of utility class preventing instantiation by other classes */
    private SuitePrinter() {
    }

    public static void print(TestSuiteLog suite) {
        print(suite, "");
    }

    private static void print(TestSuiteLogComponent component, String indent) {
        System.out.println(indent + component); //NOSONAR
        indent += "\t";
        if (component instanceof TestSuiteLog) {
            for (TestSuiteLogComponent child : ((TestSuiteLog) component).getComponents()) {
                print(child, indent);
            }
        }
    }

}
