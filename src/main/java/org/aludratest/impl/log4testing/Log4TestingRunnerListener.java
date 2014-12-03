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
package org.aludratest.impl.log4testing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.impl.log4testing.data.TestSuiteLog;
import org.aludratest.scheduler.AbstractRunnerListener;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.attachment.Attachment;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = RunnerListener.class, hint = "log4testing")
public class Log4TestingRunnerListener extends AbstractRunnerListener {

    @Override
    public void startingTestProcess(RunnerTree runnerTree) {
        // parse tree and prepare all logs
        TestSuiteLog root = TestLogger.getTestSuite(runnerTree.getRoot().getName());
        parseRunnerTree(runnerTree.getRoot(), root);
    }

    @Override
    public void startingTestLeaf(RunnerLeaf runnerLeaf) {
        TestCaseLog log = TestLogger.getTestCase(runnerLeaf.getName());
        if (Boolean.TRUE.equals(runnerLeaf.getAttribute(CommonRunnerLeafAttributes.IGNORE))) {
            log.ignore();
        }
        log.start();
    }

    @Override
    public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
        TestCaseLog log = TestLogger.getTestCase(runnerLeaf.getName());
        log.finish();
    }

    @Override
    public void finishedTestGroup(RunnerGroup runnerGroup) {
        TestSuiteLog log = TestLogger.getTestSuite(runnerGroup.getName());
        if (runnerGroup.getChildren().isEmpty()) {
            log.startAndFinishEmpty();
        }
    }

    @Override
    public void finishedTestProcess(RunnerTree runnerTree) {
        // cleanups?
    }

    @Override
    public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
        TestCaseLog log = TestLogger.getTestCase(runnerLeaf.getName());
        log.newTestStepGroup(groupName);
    }

    @Override
    public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
        TestCaseLog log = TestLogger.getTestCase(runnerLeaf.getName());

        log.newTestStep();

        log.getLastTestStep().setCommand(testStepInfo.getCommand());
        log.getLastTestStep().setStatus(testStepInfo.getTestStatus());
        log.getLastTestStep().setService(testStepInfo.getServiceId() == null ? null : testStepInfo.getServiceId().toString());

        for (Attachment a : testStepInfo.getAttachments()) {
            log.getLastTestStep().addAttachment(a);
        }

        log.getLastTestStep().setError(testStepInfo.getError());
        log.getLastTestStep().setErrorMessage(testStepInfo.getErrorMessage());

        // copy the markers
        log.getLastTestStep().setElementName(getSingleStringArgument(testStepInfo, ElementName.class));
        log.getLastTestStep().setElementType(getSingleStringArgument(testStepInfo, ElementType.class));
        log.getLastTestStep().setTechnicalLocator(getSingleStringArgument(testStepInfo, TechnicalLocator.class));
        log.getLastTestStep().setTechnicalArguments(getArgumentsString(testStepInfo, TechnicalArgument.class));
        log.getLastTestStep().setUsedArguments(getArgumentsString(testStepInfo, null));

        // convert stack trace into comment
        if (testStepInfo.getError() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            testStepInfo.getError().printStackTrace(pw);
            pw.flush();
            log.getLastTestStep().setComment(sw.toString().replace("\n", "<br />"));
        }
    }

    private String getArgumentsString(TestStepInfo testStepInfo, Class<? extends Annotation> annotClass) {
        Object[] args = testStepInfo.getArguments(annotClass);
        if (args == null || args.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (Object o : args) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(o == null ? "null" : o.toString());
        }

        return sb.toString();
    }

    private String getSingleStringArgument(TestStepInfo testStepInfo, Class<? extends Annotation> annotClass) {
        Object[] args = testStepInfo.getArguments(annotClass);
        if (args == null || args.length == 0) {
            return null;
        }
        return args[0] == null ? null : args[0].toString();
    }

    private void parseRunnerTree(RunnerGroup group, TestSuiteLog log) {
        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerLeaf) {
                log.addTestCase(TestLogger.getTestCase(node.getName()));
            }
            else {
                TestSuiteLog child = TestLogger.getTestSuite(node.getName());
                log.addTestSuite(child);
                parseRunnerTree((RunnerGroup) node, child);
            }
        }
    }

}
