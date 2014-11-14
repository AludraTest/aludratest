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
package org.aludratest.service.edifactfile;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.service.Verification;
import org.databene.edifatto.ComparisonSettings;
import org.databene.edifatto.compare.ComparisonModel;
import org.databene.edifatto.model.Interchange;
import org.w3c.dom.Element;

/** 
 * Verifies equality of EDIFACT or X12 documents.
 * @author Volker Bergmann
 */
public interface EdifactFileVerification extends Verification {

    /** Asserts that two EDIFACT or X12 interchanges are equal.
     *  @param elementType 
     *  @param elementName 
     *  @param expected the expected interchange data
     *  @param actual the actual interface data
     *  @param settings the {@link ComparisonSettings} to apply 
     *  @param model */
    void assertInterchangesMatch(
            @ElementType String elementType, 
            @ElementName String elementName, 
            Interchange expected, 
            Interchange actual, 
            @TechnicalArgument ComparisonSettings settings, 
            @TechnicalArgument ComparisonModel<Element> model);
}
