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
package org.aludratest.service.database;

/** 
 * Represents a database query
 * @author Volker Bergmann
 */
public class DatabaseQuery {

    /** The query id */
    public final String id;

    /** The query text */
    public final String query;

    /** The number of query parameters */
    public final int paramCount;

    /**
     * Constructor
     * @param id the {@link #id}
     * @param query the {@link #query} text
     * @param paramCount the {@link #paramCount}
     */
    public DatabaseQuery(String id, String query, int paramCount) {
        this.id = id;
        this.query = query;
        this.paramCount = paramCount;
    }

    @Override
    public String toString() {
        return id + ": " + query;
    }

}
