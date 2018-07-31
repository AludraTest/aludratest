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
package org.aludratest.service.gui.web.page;


/**
 * Abstract base class for so-called "Page Helpers". A Page Helper is a class
 * which can be used by Pages to perform recurring or complex tasks. <br>
 * This base class serves as a marker class to identify a concrete class as Page
 * Helper class via its class hierarchy. Page Helpers are allowed to reference
 * and work with Page classes, while most other classes are not. The AludraTest
 * PMD checks check for this rule, e.g. the <code>PageUsageRestriction</code>
 * rule.
 * 
 * @author falbrech
 * 
 */
public abstract class PageHelper { // NOSONAR

}
