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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.log4testing.AttachmentLog;
import org.aludratest.log4testing.TestStatus;
import org.aludratest.log4testing.config.Log4TestingConfiguration;
import org.aludratest.log4testing.engine.Log4TestingEngine;
import org.aludratest.scheduler.AbstractRunnerListener;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.attachment.Attachment;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** RunnerListener which uses Log4Testing for writing test logs. To configure Log4Testing, see Log4Testing documentation (hint: you
 * need a log4testing.xml on your classpath root or in your working directory).
 *
 * @author falbrech */
@Component(role = RunnerListener.class, hint = "log4testing")
public class Log4TestingRunnerListener extends AbstractRunnerListener {

    private static final Logger LOG = LoggerFactory.getLogger(Log4TestingRunnerListener.class);

    // this key is used to attach log objects to the runner tree nodes
    private static final String LOG_ATTR = Log4TestingRunnerListener.class.getName() + "_log";

    @Requirement
    private AludraTestConfig configuration;

    private Log4TestingEngine engine;

    private Log4TestingAludraTestFramework framework;

    private TestSuiteLogImpl rootSuite;

    private Log4TestingConfiguration logConfiguration;

    /** Default constructor, called by IoC framework. Uses default log configuration. */
    public Log4TestingRunnerListener() {
    }

    /** Constructor for testing purposes only. Allows to specify custom log configuration.
     *
     * @param logConfiguration Custom log configuration. */
    public Log4TestingRunnerListener(Log4TestingConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
    }

    @Override
    public void startingTestProcess(RunnerTree runnerTree) {
        if (engine == null) {
            framework = new Log4TestingAludraTestFramework();
            engine = logConfiguration != null
                    ? (configuration.isAludratestLoggingDisabled()
                            ? Log4TestingEngine.newEngine(new XmlBasedEmptyLog4TestingConfiguration())
                            : Log4TestingEngine.newEngine(logConfiguration))
                    : (configuration.isAludratestLoggingDisabled()
                            ? Log4TestingEngine.newEngine(new XmlBasedEmptyLog4TestingConfiguration())
                            : Log4TestingEngine.newEngine());
                    engine.applyTo(framework);
        }

        rootSuite = new TestSuiteLogImpl(runnerTree.getRoot().getName());
        runnerTree.getRoot().setAttribute(LOG_ATTR, rootSuite);

        parseRunnerTree(runnerTree.getRoot(), rootSuite);
        framework.fireStartingTestProcess(rootSuite);
    }

    @Override
    public void startingTestGroup(RunnerGroup runnerGroup) {
        TestSuiteLogImpl log = (TestSuiteLogImpl) runnerGroup.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        log.setStartTime(DateTime.now());
        framework.fireStartingTestSuite(log);
    }

    @Override
    public void startingTestLeaf(RunnerLeaf runnerLeaf) {
        TestCaseLogImpl log = (TestCaseLogImpl) runnerLeaf.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        if (Boolean.TRUE.equals(runnerLeaf.getAttribute(CommonRunnerLeafAttributes.IGNORE))) {
            log.setIgnored(true);
            String reason = (String) runnerLeaf.getAttribute(CommonRunnerLeafAttributes.IGNORE_REASON);
            if (reason != null) {
                log.setIgnoredReason(reason);
            }
        }

        log.setStartTime(DateTime.now());
        framework.fireStartingTestCase(log);
    }

    @Override
    public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
        TestCaseLogImpl log = (TestCaseLogImpl) runnerLeaf.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        log.setEndTime(DateTime.now());
        framework.fireFinishedTestCase(log);
    }

    @Override
    public void finishedTestGroup(RunnerGroup runnerGroup) {
        TestSuiteLogImpl log = (TestSuiteLogImpl) runnerGroup.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        log.setEndTime(DateTime.now());
        framework.fireFinishedTestSuite(log);

        // TODO must startAndFinishEmpty() somehow be considered?
    }

    @Override
    public void finishedTestProcess(RunnerTree runnerTree) {
        TestSuiteLogImpl log = (TestSuiteLogImpl) runnerTree.getRoot().getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        if (log.getEndTime() == null) {
            log.setEndTime(DateTime.now());
        }
        framework.fireFinishedTestProcess(log);
    }

    @Override
    public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
        TestCaseLogImpl log = (TestCaseLogImpl) runnerLeaf.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        log.addTestStepGroup(new TestStepGroupLogImpl(groupName, log));
    }

    @Override
    public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
        TestCaseLogImpl log = (TestCaseLogImpl) runnerLeaf.getAttribute(LOG_ATTR);
        if (log == null) {
            return;
        }

        // get last test step group; if none, create dummy one
        List<TestStepGroupLogImpl> groups = log.getTestStepGroups();
        if (groups.isEmpty()) {
            newTestStepGroup(runnerLeaf, "Test Steps");
            groups = log.getTestStepGroups();
        }
        TestStepGroupLogImpl group = groups.get(groups.size() - 1);

        // end previous test step
        DateTime now = DateTime.now();
        List<TestStepLogImpl> steps = group.getTestSteps();
        if (!steps.isEmpty()) {
            steps.get(steps.size() - 1).setEndTime(now);
        }

        TestStepLogImpl step = new TestStepLogImpl(group);

        step.setStartTime(now);
        step.setCommand(testStepInfo.getCommand());
        step.setStatus(convertStatus(testStepInfo.getTestStatus()));
        step.setService(testStepInfo.getServiceId() == null ? null : testStepInfo.getServiceId().toString());

        for (Attachment a : testStepInfo.getAttachments()) {
            step.addAttachment(createAttachmentLog(a));
        }

        step.setResult(testStepInfo.getResult());
        step.setError(testStepInfo.getError());
        step.setErrorMessage(testStepInfo.getErrorMessage());

        // copy the markers
        step.setElementName(getSingleStringArgument(testStepInfo, ElementName.class));
        step.setElementType(getSingleStringArgument(testStepInfo, ElementType.class));
        step.setTechnicalLocator(getSingleStringArgument(testStepInfo, TechnicalLocator.class));
        step.setTechnicalArguments(getArgumentsString(testStepInfo, TechnicalArgument.class));
        step.setUsedArguments(getArgumentsString(testStepInfo, null));

        // convert stack trace into comment
        if (testStepInfo.getError() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            testStepInfo.getError().printStackTrace(pw);
            pw.flush();
            step.setComment(sw.toString());
        }
    }

    private AttachmentLog createAttachmentLog(Attachment attachment) {
        if (configuration.isAttachmentsFileBuffer()) {
            try {
                return new LocalFileAttachmentLog(attachment);
            }
            catch (IOException e) {
                LOG.error("Could not buffer attachment to local file. Falling back to memory-based buffer", e);
            }
        }
        return new MemoryAttachmentLog(attachment);
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
            sb.append(toString(o));
        }

        return sb.toString();
    }

    private String getSingleStringArgument(TestStepInfo testStepInfo, Class<? extends Annotation> annotClass) {
        Object[] args = testStepInfo.getArguments(annotClass);
        if (args == null || args.length == 0) {
            return null;
        }
        return toString(args[0]);
    }

    private void parseRunnerTree(RunnerGroup group, TestSuiteLogImpl log) {
        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerLeaf) {
                node.setAttribute(LOG_ATTR, new TestCaseLogImpl(node.getName(), log));
            }
            else {
                TestSuiteLogImpl suite = new TestSuiteLogImpl(node.getName(), log);
                node.setAttribute(LOG_ATTR, suite);
                parseRunnerTree((RunnerGroup) node, suite);
            }
        }
    }

    private String toString(Object o) { // NOSONAR number of returns is appropriate
        if (o == null) {
            return "null";
        }

        if (o.getClass().isArray()) {
            if (!o.getClass().getComponentType().isPrimitive()) {
                return Arrays.toString((Object[]) o);
            }
            else if (o.getClass().getComponentType() == int.class) {
                return Arrays.toString((int[]) o);
            }
            else if (o.getClass().getComponentType() == float.class) {
                return Arrays.toString((float[]) o);
            }
            else if (o.getClass().getComponentType() == boolean.class) {
                return Arrays.toString((boolean[]) o);
            }
            else if (o.getClass().getComponentType() == double.class) {
                return Arrays.toString((double[]) o);
            }
            else if (o.getClass().getComponentType() == short.class) { // NOSONAR I need to support short generically
                return Arrays.toString((short[]) o); // NOSONAR I need to support short generically
            }
        }

        return o.toString();
    }

    private static TestStatus convertStatus(org.aludratest.testcase.TestStatus status) { // NOSONAR number of returns is
        // appropriate
        switch (status) {
            case FAILED:
                return TestStatus.FAILED;
            case FAILEDACCESS:
                return TestStatus.FAILEDACCESS;
            case FAILEDAUTOMATION:
                return TestStatus.FAILEDAUTOMATION;
            case FAILEDPERFORMANCE:
                return TestStatus.FAILEDPERFORMANCE;
            case IGNORED:
                return TestStatus.IGNORED;
            case INCONCLUSIVE:
                return TestStatus.INCONCLUSIVE;
            case PASSED:
                return TestStatus.PASSED;
            case PENDING:
                return TestStatus.PENDING;
            case RUNNING:
                return TestStatus.RUNNING;
        }

        throw new IllegalArgumentException("Unsupported test status value: " + status);
    }

}
