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

import java.util.ArrayList;
import java.util.List;

import org.aludratest.content.edifact.AggregateEdiDiff;
import org.aludratest.content.edifact.EdiComparisonSettings;
import org.aludratest.content.edifact.EdiDiffDetail;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.compare.ComparisonSettings;
import org.databene.formats.compare.DiffDetail;

/** Aggregate diff of two Edifact interchanges.
 * @author Volker Bergmann */
public class EdifattoAggregateEdiDiff extends AggregateDiff implements AggregateEdiDiff {

    /** Constructor
     * @param expected the expected interchange structure
     * @param actual the actual interchange structure
     * @param comparisonSettings the comparison settings
     * @param genericDiff the underlying specific aggregate diff to map to this abstract aggregate diff */
    public EdifattoAggregateEdiDiff(Object expected, Object actual, ComparisonSettings comparisonSettings,
            AggregateDiff genericDiff) {
        super(expected, actual, comparisonSettings);
        for (DiffDetail detail : genericDiff.getDetails()) {
            this.addDetail(new EdiDiffDetailImpl(detail)); // NOSONAR
        }
    }

    @Override
    public EdiComparisonSettings getEdiComparisonSettings() {
        return (EdiComparisonSettings) super.getComparisonSettings();
    }

    @Override
    public List<EdiDiffDetail> getEdiDetails() {
        List<DiffDetail> sourceDetails = super.getDetails();
        List<EdiDiffDetail> targetDetails = new ArrayList<EdiDiffDetail>(sourceDetails.size());
        for (DiffDetail s : sourceDetails) {
            targetDetails.add(new EdiDiffDetailImpl(s));
        }
        return targetDetails;
    }

}
