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

import java.util.Date;
import java.util.List;

import org.aludratest.service.database.DatabaseService;

/** 
 * Represents an error_log entry in the database
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class Error_log {

    public static List<Error_log> queryRecent(DatabaseService service) {
        return service.perform().query(Error_log.class, "select * from error_log where sysdate - creation_date <= 1");
    }

    public static List<Error_log> queryRecentOfUser(DatabaseService service, int userId) {
        return service.perform().query(Error_log.class, "select * from ERROR_LOG where sysdate - creation_date <= 1 and creation_user_id = " + userId);
    }

    public long log_id;

    public String error_number;

    public String error_message;

    public Date creation_date;

    public String creation_user_id;

    public String stack_trace;

    public Error_log() {
        this(0, null, null, null, null, null);
    }

    public Error_log(long log_id, String error_number, String error_message, String stack_trace, Date creation_date, String creation_user_id) {
        this.log_id = log_id;
        this.error_number = error_number;
        this.error_message = error_message;
        this.creation_date = creation_date;
        this.creation_user_id = creation_user_id;
        this.stack_trace = stack_trace;
    }

}
