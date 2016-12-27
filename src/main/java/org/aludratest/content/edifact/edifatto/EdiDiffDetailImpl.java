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
package org.aludratest.content.edifact.edifatto;

import org.aludratest.content.edifact.EdiDiffDetail;
import org.aludratest.content.edifact.EdiDiffDetailType;
import org.databene.formats.compare.DiffDetail;

/** TODO javadoc
 * @author Volker Bergmann */
public class EdiDiffDetailImpl extends DiffDetail implements EdiDiffDetail {

    private EdiDiffDetailType ediType;

    /** Constructor.
     * @param detail a prototype */
    public EdiDiffDetailImpl(DiffDetail detail) {
        super(detail.getExpected(), detail.getActual(), detail.getObjectClassifier(), detail.getType(), detail
                .getLocatorOfExpected(), detail.getLocatorOfActual(), detail.getFormatter());
        this.ediType = EdifattoUtil.generic2ediDiffType(type);
    }

    @Override
    public EdiDiffDetailType getEdiDiffType() {
        return ediType;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + ((ediType == null) ? 0 : ediType.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EdiDiffDetailImpl that = (EdiDiffDetailImpl) obj;
        return (this.ediType == that.ediType);
    }

}
