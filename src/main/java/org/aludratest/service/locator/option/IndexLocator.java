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
package org.aludratest.service.locator.option;

/**
 * Locates an option by its index.
 * @author Volker Bergmann
 */
public class IndexLocator extends OptionLocator {

    private Integer index;

    /** Locates a DropDownBox option by its (zero-based) index.
     *  @param index zero-based index of the related DropDownBox option */
    public IndexLocator(int index) {
        super(String.valueOf(index));
        if (index < 0) {
            throw new IllegalArgumentException("Negative index: " + index);
        }
        this.index = index;
    }

    /** @return the zero-based {@link #index}. */
    public Integer getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj) || getClass() != obj.getClass())
            return false;
        IndexLocator that = (IndexLocator) obj;
        return (this.index == that.index);
    }

}
