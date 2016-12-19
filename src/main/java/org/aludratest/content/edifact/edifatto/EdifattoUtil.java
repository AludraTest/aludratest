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

import org.aludratest.content.edifact.AggregateEdiDiff;
import org.aludratest.content.edifact.EdiDiffDetailType;
import org.databene.formats.compare.AggregateDiff;
import org.databene.formats.compare.ComparisonSettings;
import org.databene.formats.compare.DiffDetailType;

/** Provides utilities for the Edifatto-based EDI content handler.
 * @author Volker Bergmann */
public final class EdifattoUtil {

    private EdifattoUtil() {
        // private constructor to prevent instantiation of this utility class
    }

    /** Private constructor for preventing instantiation of this utility class. */
    private EdifattoUtil() {
        // Private constructor for preventing instantiation of this utility class.
    }

    /** Maps an {@link EdiDiffDetailType} to a generic diff detail type.
     * @param type the EDI diff detail type to map
     * @return the corresponding generic diff detail type */
    public static DiffDetailType edi2genericDetailType(EdiDiffDetailType type) {
        return DiffDetailType.valueOf(type.name());
    }

    /** Maps a generic diff detail type to a {@link EdiDiffDetailType}.
     * @param type the EDI diff detail type to map
     * @return the corresponding generic diff detail type */
    public static EdiDiffDetailType generic2ediDiffType(DiffDetailType type) {
        return EdiDiffDetailType.valueOf(type.name());
    }

    /** Converts a generic {@link AggregateDiff} to an {@link AggregateEdiDiff}.
     * @param expected
     * @param actual
     * @param settings
     * @param genericDiff the {@link AggregateDiff} to convert
     * @return a corresponding {@link AggregateEdiDiff} */
    public static AggregateEdiDiff generic2ediDiff(Object expected, Object actual, ComparisonSettings settings,
            AggregateDiff genericDiff) {
        return new EdifattoAggregateEdiDiff(expected, actual, settings, genericDiff);
    }

}
