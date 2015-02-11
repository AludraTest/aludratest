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
package org.aludratest.service.gui.web.uimap;

/**
 * Abstract base class for so-called "UIMap Utilities". A UIMap Utility is a
 * class which can be used by UIMaps to perform recurring or complex tasks. <br>
 * All public methods of the utility class must be static, and the constructor
 * must not be visible (package-private or private). <br>
 * This base class serves as a marker class to identify a concrete class as
 * UIMap Utility class via its class hierarchy. UIMap Utilities are allowed to
 * reference and work with UIMap classes, while most other classes are not. The
 * AludraTest PMD checks check for this rule, e.g. the
 * <code>UIMapUsageRestriction</code> rule.
 * 
 * @author falbrech
 * 
 */
public abstract class UIMapUtility {

}
