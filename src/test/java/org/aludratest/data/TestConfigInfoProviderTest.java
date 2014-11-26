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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.config.impl.AludraTestingTestConfigImpl;
import org.aludratest.data.configtests.ConfigTestWithMissingSource;
import org.aludratest.data.configtests.ConfigTestWithAddidtionalConfigRow;
import org.aludratest.data.configtests.ConfigTestWithHierarchy;
import org.aludratest.data.configtests.ConfigTestWithIgnoreConfig;
import org.aludratest.data.configtests.ConfigTestWithIgnoredMethod;
import org.aludratest.data.configtests.ConfigTestWithMissingConfigRow;
import org.aludratest.data.configtests.ConfigTestWithMissingTab;
import org.aludratest.data.configtests.ConfigTestWithProperConfig;
import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.data.TestLogger;
import org.aludratest.scheduler.AludraSuiteParser;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.testcase.TestStatus;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link TestConfigInfoHelper}.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class TestConfigInfoProviderTest extends AbstractAludraServiceTest {
    /*
    @AfterClass
    public static void notifyFinishedTestProcess() {
    	TestObserverManager.getInstance().notifyFinishedTestProcess(TestLogger.getRootSuite());
    }*/

    @Before
    public void prepare() {
        TestLogger.clear();
        AludraTestingTestConfigImpl.getTestInstance().setXlsRootPath("src/test/resources");
    }

    @Test
    public void testProperConfig() {
        String testClassName = ConfigTestWithProperConfig.class.getName();
        RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
        tree.performAllTestsAndWait(1);
        List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
        assertEquals(2, tests.size());
        assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.PASSED);
        assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.PASSED);
    }

    @Test
    public void testHierarchy() {
        String testClassName = ConfigTestWithHierarchy.class.getName();
        RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
        tree.performAllTestsAndWait(1);
        List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
        assertEquals(2, tests.size());
        assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.PASSED);
        assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.PASSED);
    }

    @Test
    public void testIgnoredMethod() {
        String testClassName = ConfigTestWithIgnoredMethod.class.getName();
        RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
        tree.performAllTestsAndWait(1);
        List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
        assertEquals(2, tests.size());
        assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.IGNORED);
        assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.IGNORED);
    }

    @Test
    public void testIgnoreEnabled() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean ignoreEnabled = cfg.isIgnoreEnabled();
        cfg.setIgnoreEnabled(true);
        try {
            String testClassName = ConfigTestWithIgnoreConfig.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
            assertEquals(2, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.IGNORED);
            assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.PASSED);
        } finally {
            cfg.setIgnoreEnabled(ignoreEnabled);
        }
    }

    @Test
    public void testIgnoreDisabled() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean ignoreEnabled = cfg.isIgnoreEnabled();
        cfg.setIgnoreEnabled(false);
        try {
            String testClassName = ConfigTestWithIgnoreConfig.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
            assertEquals(2, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.INCONCLUSIVE);
            assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.PASSED);
        } finally {
            cfg.setIgnoreEnabled(ignoreEnabled);
        }
    }

    @Test
    public void testMissingSource() {
        String testClassName = ConfigTestWithMissingSource.class.getName();
        RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
        tree.performAllTestsAndWait(1);
        List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
        assertEquals(1, tests.size());
        assertNameAndStatus(tests.get(0), testClassName + ".test_error_1", TestStatus.FAILEDAUTOMATION);
    }

    @Test
    public void testMissingTab_required() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean configTabRequired = cfg.isConfigTabRequired();
        cfg.setConfigTabRequired(true);
        try {
            String testClassName = ConfigTestWithMissingTab.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
            assertEquals(2, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test_error_1", TestStatus.FAILEDAUTOMATION);
            assertNameAndStatus(tests.get(1), testClassName + ".test_error_2", TestStatus.FAILEDAUTOMATION);
        } finally {
            cfg.setConfigTabRequired(configTabRequired);
        }
    }

    @Test
    public void testMissingTab_optional() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean configTabRequired = cfg.isConfigTabRequired();
        cfg.setConfigTabRequired(false);
        try {
            String testClassName = ConfigTestWithMissingTab.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            List<RunnerLeaf> tests = extractTestCaseList(tree.getRoot());
            assertEquals(2, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test-0", TestStatus.PASSED);
            assertNameAndStatus(tests.get(1), testClassName + ".test-1", TestStatus.PASSED);
        } finally {
            cfg.setConfigTabRequired(configTabRequired);
        }
    }

    @Test
    public void testMissingConfigRow() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean configTabRequired = cfg.isConfigTabRequired();
        cfg.setConfigTabRequired(false);
        try {
            String testClassName = ConfigTestWithMissingConfigRow.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            RunnerGroup root = tree.getRoot();
            List<RunnerLeaf> tests = extractTestCaseList(root);
            assertEquals(2, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.PASSED);
            assertNameAndStatus(tests.get(1), testClassName + ".test_error_1", TestStatus.FAILEDAUTOMATION);
        } finally {
            cfg.setConfigTabRequired(configTabRequired);
        }
    }

    @Test
    public void testAdditionalConfigRow() {
        AludraTestingTestConfigImpl cfg = AludraTestingTestConfigImpl.getTestInstance();
        boolean configTabRequired = cfg.isConfigTabRequired();
        cfg.setConfigTabRequired(false);
        try {
            String testClassName = ConfigTestWithAddidtionalConfigRow.class.getName();
            RunnerTree tree = new AludraSuiteParser(aludra).parse(testClassName);
            tree.performAllTestsAndWait(1);
            RunnerGroup root = tree.getRoot();
            List<RunnerLeaf> tests = extractTestCaseList(root);
            assertEquals(3, tests.size());
            assertNameAndStatus(tests.get(0), testClassName + ".test-testing Alice", TestStatus.PASSED);
            assertNameAndStatus(tests.get(1), testClassName + ".test-testing Bob", TestStatus.PASSED);
            assertNameAndStatus(tests.get(2), testClassName + ".test_error_1", TestStatus.FAILEDAUTOMATION);
        } finally {
            cfg.setConfigTabRequired(configTabRequired);
        }
    }

    // private helpers ---------------------------------------------------------

    private static List<RunnerLeaf> extractTestCaseList(RunnerGroup group) {
        return extractTestCaseList(group, new ArrayList<RunnerLeaf>());
    }

    private static List<RunnerLeaf> extractTestCaseList(RunnerGroup group, List<RunnerLeaf> list) {
        for (RunnerNode node : group.getChildren()) {
            if (node instanceof RunnerGroup) {
                extractTestCaseList((RunnerGroup) node, list);
            } else {
                list.add((RunnerLeaf) node);
            }
        }
        return list;
    }

    private static void assertNameAndStatus(RunnerLeaf testCase, String name, TestStatus status) {
        TestCaseLog logCase = testCase.getLogCase();
        assertEquals(status, logCase.getStatus());
        assertEquals(name, logCase.getName());
    }

}
