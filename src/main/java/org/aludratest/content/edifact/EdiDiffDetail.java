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
package org.aludratest.content.edifact;

/** An atomic EDI difference.
 * @author Volker Bergmann */
public interface EdiDiffDetail {

    /** @return the expected element */
    Object getExpected();

    /** @return the actual element */
    Object getActual();

    /** @return a classifier for the element */
    String getObjectClassifier();

    /** @return the type of difference observed */
    EdiDiffDetailType getEdiDiffType();

    /** @return the locator of the expected element */
    String getLocatorOfExpected();

    /** @return the locator of the actual element */
    String getLocatorOfActual();

}
