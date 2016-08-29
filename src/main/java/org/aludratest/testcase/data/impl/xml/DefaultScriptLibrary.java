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
package org.aludratest.testcase.data.impl.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.exception.TechnicalException;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/** Default JavaScript library shipped with AludraTest.
 *
 * @author falbrech */
@Component(role = ScriptLibrary.class, hint = "default")
public final class DefaultScriptLibrary implements ScriptLibrary {

    private static final String JS_ADD_DAYS_TO_NOW = "function addDaysToNow(days_add) { return new Date(new Date().getTime() + days_add * 24 * 60 * 60 * 1000); }";
    private static final String JS_ADD_HOURS_TO_NOW = "function addHoursToNow(hours_add) { return new Date(new Date().getTime() + hours_add * 60 * 60 * 1000); }";

    @Requirement
    private AludraTestConfig aludraConfig;

    @Override
    public void addFunctionsToContext(Context context, Scriptable scope) {
        // "compile" functions into scope
        context.evaluateString(scope, JS_ADD_DAYS_TO_NOW, "jsAddDaysToNow", 1, null);
        context.evaluateString(scope, JS_ADD_HOURS_TO_NOW, "jsAddHoursToNow", 1, null);

        // complex Date format addition
        loadScript(context, scope, "date-format.js");

        // timetravel() function
        long timetravelDiff = aludraConfig.getScriptSecondsOffset() * 1000;
        if (timetravelDiff == 0) {
            context.evaluateString(scope, "function timetravel(date) { return date; }", "jsTimetravel", 1, null);
        }
        else {
            context.evaluateString(scope,
                    "function timetravel(date) { return new Date(date.getTime() + (" + timetravelDiff + ")); }", "jsTimetravel",
                    1,
                    null);
        }

    }

    private void loadScript(Context context, Scriptable scope, String jsResourceName) {
        InputStream in = DefaultScriptLibrary.class.getResourceAsStream(jsResourceName);
        try {
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            context.evaluateReader(scope, reader, jsResourceName, 1, null);
        }
        catch (IOException e) {
            throw new TechnicalException("Could not read date-format.js resource", e);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

}
