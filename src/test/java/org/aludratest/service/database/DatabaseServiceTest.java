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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.database.DatabaseService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** 
 * Tests the {@link DatabaseService}.
 * @author Volker Bergmann
 */
@Ignore
@SuppressWarnings("javadoc")
public class DatabaseServiceTest extends AbstractAludraServiceTest {

    private DatabaseService service;

    @Before
    public void setUp() {
        this.service = getService(DatabaseService.class, "tg38");
    }

    @After
    public void tearDown() {
        if (this.service != null)
            this.service.close();
    }

    @Test
    public void testQueryString() {
        String message = service.perform().queryString("select MESSAGE from ERROR_LOG");
        assertNotNull(message);
    }

    @Test
    public void testQueryInt() {
        int errCount = service.perform().queryInt("select count(*) from ERROR_LOG");
        assertTrue(errCount >= 0);
    }

    @Test
    public void testPlainDataBeanQuery() {
        List<Error_log> errorLogs = service.perform().query(Error_log.class);
        for (Error_log log : errorLogs) {
            System.out.println(log);
        }
    }

    @Test
    public void testConfiguredDataBeanQuery() {
        List<Error_log> errorLogs = Error_log.queryRecentOfUser(service, 1);
        for (Error_log log : errorLogs) {
            System.out.println(log);
        }
    }

    @Test
    public void testUpdate() {
        List<Error_log> errors = Error_log.queryRecent(service);
        for (Error_log error : errors) {
            error.error_message = error.error_message.trim();
            service.perform().update(error);
        }
    }

}
