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
package org.aludratest.service.locator.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link GUIElementLocator} version which wraps a set of alternative GUIElementLocators. To retrieve a
 * <code>GUIElementLocator</code> compatible object, invoke <code>newMutableInstance()</code>. You can (and should) retrieve
 * multiple mutable instances from the same <code>ElementLocators</code> object, as every instance will be modified by the
 * AludraTest framework when the actual element to use is searched and stored in the object.
 * 
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public class ElementLocators implements Iterable<GUIElementLocator> {

    // attributes --------------------------------------------------------------

    private List<GUIElementLocator> options;

    // constructors ------------------------------------------------------------

    /** Creates an {@link ElementLocators} containing a list of {@link IdLocator}s.
     *  @param locators the alternative {@link IdLocator}s */
    public ElementLocators(GUIElementLocator... locators) {
        options = new ArrayList<GUIElementLocator>();
        for (GUIElementLocator locator : locators) {
            options.add(locator);
        }
    }


    // Iterable interface implementation ---------------------------------------

    @Override
    public Iterator<GUIElementLocator> iterator() {
        return options.iterator();
    }

    /**
     * Creates a new mutable instance of this locators object which can be used as GUIElementLocator and will be modified by the
     * AludraTest framework. Such instances may <b>not</b> be shared between Threads!
     * 
     * @return A new mutable instance of this locators object, which implements GUIElementLocator.
     */
    public GUIElementLocator newMutableInstance() {
        return new ElementLocatorsGUI(options);
    }

    // java.lang.Object overrides ----------------------------------------------

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + options.hashCode(); // NOSONAR
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        ElementLocators that = (ElementLocators) obj;
        return (this.options.equals(that.options));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName()).append("[");
        builder.append("options=[");
        for (int i = 0; i < options.size(); i++) {
            builder.append(i > 0 ? ", " : "");
            builder.append("#").append(i).append(": ").append(options.get(i));
        }
        builder.append("]");
        return builder.append("]").toString();
    }

    public static class ElementLocatorsGUI extends GUIElementLocator implements Iterable<GUIElementLocator> {

        private List<GUIElementLocator> options;
        private GUIElementLocator usedOption;

        private ElementLocatorsGUI(List<GUIElementLocator> options) {
            super("");
            this.options = new ArrayList<GUIElementLocator>(options);
        }

        @Override
        public Iterator<GUIElementLocator> iterator() {
            return options.iterator();
        }

        // properties --------------------------------------------------------------

        /**
         * Sets the pointer to the given locator
         * 
         * @param option
         *            the pointer to set
         */
        public final void setUsedOption(GUIElementLocator option) {
            this.usedOption = option;
        }

        /** @return the value of the pointer */
        public GUIElementLocator getUsedOption() {
            return usedOption;
        }

        // private helpers ---------------------------------------------------------

        private int indexOf(GUIElementLocator option) {
            for (int i = 0; i < options.size(); i++) {
                GUIElementLocator candidate = options.get(i);
                if (candidate == option) { // NOSONAR
                    return i;
                }
            }
            return -1;
        }

        // java.lang.Object overrides ----------------------------------------------
        @Override
        public int hashCode() {
            return super.hashCode() * 31 + options.hashCode(); // NOSONAR
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!super.equals(obj) || getClass() != obj.getClass()) {
                return false;
            }
            ElementLocatorsGUI that = (ElementLocatorsGUI) obj;
            return (this.options.equals(that.options) && (this.usedOption == null ? that.usedOption == null : this.usedOption
                    .equals(that.usedOption)));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(getClass().getSimpleName()).append("[");
            builder.append("options=[");
            for (int i = 0; i < options.size(); i++) {
                builder.append(i > 0 ? ", " : "");
                builder.append("#").append(i).append(": ").append(options.get(i));
            }
            builder.append("]");
            builder.append(", usedLocator=");
            if (usedOption != null) {
                builder.append(usedOption.toString());
            }
            else {
                builder.append("none");
            }
            return builder.append("]").toString();
        }

    }
}
