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

import java.util.List;

import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;

/**
 * {@link Interaction} interface of the {@link DatabaseService}.
 * @author Volker Bergmann
 */
public interface DatabaseInteraction extends Interaction {

    /** Inserts a row into the database 
     *  @param object */
    void insert(Object object);

    /** Updates a row in the database.
     *  @param object */
    void update(Object object);

    /** Queries a String from the database.
     *  @param query 
     *  @return the query result */
    String queryString(@TechnicalLocator String query);

    /** Queries an integer from the database
     *  @param query 
     *  @return the query result */
    int queryInt(@TechnicalLocator String query);

    /** Queries a list of rows from the Database.
     *  @param clazz 
     *  @return the query result values */
    <T> List<T> query(@TechnicalLocator Class<T> clazz);

    /** Queries a list of rows from the Database.
     *  @param clazz 
     *  @param query 
     *  @return the query result values */
    <T> List<T> query(@TechnicalLocator Class<T> clazz, String query);

}
