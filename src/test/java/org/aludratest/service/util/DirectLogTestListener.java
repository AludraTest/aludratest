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
package org.aludratest.service.util;

import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.testcase.event.InternalTestListener;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.attachment.Attachment;
import org.databene.commons.StringUtil;

public class DirectLogTestListener implements InternalTestListener {

    private TestCaseLog testCaseLog;

    public DirectLogTestListener(TestCaseLog testCaseLog) {
        this.testCaseLog = testCaseLog;
    }

    @Override
    public void newTestStepGroup(String name) {
        testCaseLog.newTestStepGroup(name);
    }

    @Override
    public void newTestStep(TestStepInfo testStep) {
        testCaseLog.newTestStep(testStep.getTestStatus(), testStep.getCommand(), "");

        testCaseLog.getLastTestStep().setCommand(testStep.getCommand());
        // do NOT call setStatus here, as it finishes the step
        // testCaseLog.getLastTestStep().setStatus(testStep.getTestStatus());
        testCaseLog.getLastTestStep().setService(testStep.getServiceId() == null ? null : testStep.getServiceId().toString());

        for (Attachment a : testStep.getAttachments()) {
            testCaseLog.getLastTestStep().addAttachment(a);
        }

        copyArgumentsFromTestStep(testStep);
        testCaseLog.getLastTestStep().setError(testStep.getError());
        testCaseLog.getLastTestStep().setErrorMessage(testStep.getErrorMessage());
        testCaseLog.getLastTestStep().setStatus(testStep.getTestStatus());
    }

    private void copyArgumentsFromTestStep(TestStepInfo testStep) {
        // extract technical locator, if any
        Object[] params = testStep.getArguments(TechnicalLocator.class);
        if (params.length > 0 && params[0] != null) {
            testCaseLog.getLastTestStep().setTechnicalLocator(params[0].toString());
        }

        // extract element name and element type, if any
        params = testStep.getArguments(ElementName.class);
        if (params.length > 0 && params[0] != null) {
            testCaseLog.getLastTestStep().setElementName(params[0].toString());
        }
        params = testStep.getArguments(ElementType.class);
        if (params.length > 0 && params[0] != null) {
            testCaseLog.getLastTestStep().setElementType(params[0].toString());
        }

        // combine other arguments
        params = testStep.getArguments(null);
        String[] strings = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            strings[i] = params[i] == null ? "null" : params[i].toString();
        }

        testCaseLog.getLastTestStep().setUsedArguments(StringUtil.concat('\n', strings));
    }

}
