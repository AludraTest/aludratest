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
package org.aludratest.service.cmdline.impl;

import static org.junit.Assert.assertEquals;

import org.aludratest.AludraTest;
import org.aludratest.config.impl.SimplePreferences;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.cmdline.CommandLineProcess;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.util.data.IntData;
import org.aludratest.util.data.StringData;
import org.databene.commons.SystemInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Tests the {@link CommandLineService}.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class CommandLineActionImplTest {

    private static final String LF = SystemInfo.getLineSeparator();

    private AludraTest aludraTest;

    @Before
    public void setUp() {
        this.aludraTest = AludraTest.startFramework();
    }

    @After
    public void tearDown() {
        aludraTest.stopFramework();
    }

    @Test
    public void testSimpleCommandLineInvocation() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("java", "versiontest", service, 3000, 2000, "java", "-version");
        process.start();
        process.waitUntilFinished();
        // Checking only for space as on tavis-ci test fails as _JAVA_OPTIONS are returned instead of java version.
        process.errOut().nextLine().assertContains(new StringData(" "));
        process.destroy();
    }

    @Test
    public void testLineIteration() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("batch", "listpersons", service, 3000, 2000,
                testBatchPath("listpersons"));
        process.start();
        process.stdOut().nextLine().assertEquals(new StringData("Alice"));
        process.stdOut().nextLine().assertEquals(new StringData("Bob"));
        process.stdOut().nextLine().assertEquals(new StringData("Charly"));
        process.stdOut().assertEmpty();
        process.waitUntilFinished();
        process.destroy();
    }

    @Test(expected = AutomationException.class)
    public void testNonExistingProgram() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("nonexisting_prg", "nonexisting_prg", service, 3000, 2000,
                "nonexisting_prg");
        process.start();
        process.waitUntilFinished();
        process.destroy();
    }

    @Test
    public void testEnvironmentSettings() {
        // GIVEN a script that evaluates the environment variable TEST_ENV
        String[] command = echoCommands("TEST_ENV");

        // WHEN running it without the variable being set
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process1 = new CommandLineProcess("shell", "envtest-neg", service, 3000, 2000, command);
        process1.start();
        process1.waitUntilFinished();

        // THEN the output shall be empty
        process1.stdOut().nextLine().assertEquals(new StringData(""));

        // WHEN running it with the variable set to 'test_val'
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process2 = new CommandLineProcess("shell", "envtest-pos", service, 3000, 2000, command);
        process2.setEnvironmentVariable("TEST_ENV", "test_val");
        process2.start();
        process2.waitUntilFinished();

        // THEN the output shall be 'test_val'
        process2.stdOut().nextLine().assertEquals(new StringData("test_val"));
        process1.destroy();
        process2.destroy();
    }

    @Test
    public void testInteractiveCommandLineInvocation() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("batch", "interaction", service, 3000, 2000,
                testBatchPath("input"));
        process.start();
        process.stdOut().nextLine().assertPrefix(new StringData("Please enter name"));
        process.enterLine("Tester");
        process.stdOut().nextLine().assertEquals(new StringData("Hello Tester"));
        process.assertExitValue(new IntData(0));
        process.errOut().assertEmpty();
        process.destroy();
    }

    @Test
    public void testRedirectStdIn() throws Exception {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("batch", "interaction2", service, 3000, 3000,
                testBatchPath("input"));
        process.start();
        process.stdOut().nextLine().assertPrefix(new StringData("Please enter name"));
        process.stdIn().redirectFrom(new StringData("Tester" + LF));
        process.stdOut().nextLine().assertEquals(new StringData("Hello Tester"));
        process.assertExitValue(new IntData(0));
        process.errOut().assertEmpty();
        process.destroy();
    }

    @Test
    public void testRedirectStdOut() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("batch", "redir-stdout", service, 3000, 2000,
                testBatchPath("commandline"), "Tester");
        process.start();
        StringData out = new StringData();
        process.stdOut().redirectTo(out);
        assertEquals("Hello Tester", out.getValue().trim());
        process.assertExitValue(new IntData(0));
        process.errOut().assertEmpty();
        process.destroy();
    }

    @Test
    public void testRedirectErrOut() {
        CommandLineService service = getCommandLineService();
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess("batch", "redir-errout", service, 3000, 2000,
                testBatchPath("errout"));
        process.start();
        StringData out = new StringData();
        process.errOut().redirectTo(out);
        assertEquals("some error", out.getValue().trim());
        process.assertExitValue(new IntData(0));
        process.destroy();
    }

    // private helper ----------------------------------------------------------

    private CommandLineService getCommandLineService() {
        CommandLineServiceImpl service = new CommandLineServiceImpl();
        SimplePreferences preferences = new SimplePreferences();
        preferences.setValue(CommandLineServiceConfiguration.BASE_DIRECTORY, ".");
        service.configure(preferences);
        service.init(null);
        return service;
    }

    private String[] echoCommands(String variable) {
        String[] command;
        if (SystemInfo.isWindows()) {
            command = new String[] { "cmd.exe", "/C", "ECHO %" + variable + "%" };
        }
        else {
            command = new String[] { "/bin/bash", "-c", "echo $" + variable };
        }
        return command;
    }

    private String testBatchPath(String scriptName) {
        return "src/test/script/" + scriptName + batchFileSuffix();
    }

    private String batchFileSuffix() {
        return (SystemInfo.isWindows() ? ".bat" : ".sh");
    }

}
