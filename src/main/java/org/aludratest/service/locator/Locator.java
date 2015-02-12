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
package org.aludratest.service.locator;

import org.aludratest.exception.TechnicalException;

/**
 * Identifies a component of the SUT, for example a button in a GUI.
 * @author Volker Bergmann
 */
public abstract class Locator {

    protected final String locator;

    /** Constructor.
     *  @param locator */
    protected Locator(String locator) {
        if (locator == null) {
            throw new TechnicalException("Locator string is NULL");
        }
        this.locator = locator;
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public String toString() {
        return locator;
    }

    @Override
    public int hashCode() {
        return this.locator.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Locator that = (Locator) obj;
        return (this.locator.equals(that.locator));
    }

}
