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
package org.aludratest.data;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.InternalComponent;

/** Configuration component for special data markers. The markers are special keywords that allow a String with the same value to
 * be treated as <code>null</code>, as empty, or as an "ALL"-match.
 * 
 * @author falbrech */
@InternalComponent(singleton = true)
@ConfigProperties({
    @ConfigProperty(name = "NULL", type = String.class, description = "Marker to treat Strings as null when String value equals the marker", defaultValue = "<NULL>"),
    @ConfigProperty(name = "EMPTY", type = String.class, description = "Marker to treat Strings as empty when String value equals the marker", defaultValue = "<EMPTY>"),
        @ConfigProperty(name = "ALL", type = String.class, description = "Marker to treat Strings as ALL-match when String value equals the marker", defaultValue = "<ALL>")

})
public interface DataConfiguration {

    /** Returns the marker string to be used as keyword for treating values as <code>null</code>. By default, this is the string
     * <code>"NULL"</code>.
     * 
     * @return The marker string to be used as keyword for treating values as <code>null</code>. */
    public String getNullMarker();

    /** Returns the marker string to be used as keyword for treating values as empty. By default, this is the string
     * <code>"EMPTY"</code>.
     * 
     * @return The marker string to be used as keyword for treating values as empty. */
    public String getEmptyMarker();

    /** Returns the marker string to be used as keyword for treating values as ALL-match. By default, this is the string
     * <code>"ALL"</code>.
     * 
     * @return The marker string to be used as keyword for treating values as ALL-match. */
    public String getAllMarker();

}
