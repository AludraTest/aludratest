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

import java.util.List;

/** Provides an aggregated collection of differences between to XML files.
 * @author Volker Bergmann */
public interface AggregateEdiDiff {

    /** @return the expected object */
    public Object getExpected();

    /** @return the actual object */
    public Object getActual();

    /** @return the settings applied in the comparison */
    public EdiComparisonSettings getEdiComparisonSettings();

    /** @return the found differences which where not tolerated by the settings */
    public List<EdiDiffDetail> getEdiDetails();

    /** @return true if no differences where found, otherwise false. */
    public boolean isEmpty();

}
