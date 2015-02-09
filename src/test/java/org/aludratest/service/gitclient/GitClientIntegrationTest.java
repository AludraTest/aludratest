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
package org.aludratest.service.gitclient;

import static org.junit.Assert.assertFalse;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.gitclient.data.BranchListData;
import org.aludratest.service.gitclient.data.StatusData;
import org.aludratest.service.gitclient.data.VersionData;
import org.aludratest.testcase.After;
import org.aludratest.util.data.StringData;
import org.databene.commons.StringUtil;
import org.junit.Before;
import org.junit.Test;

/** Tests the {@link GitClient} against an installation of git.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class GitClientIntegrationTest extends AbstractAludraServiceTest {

    // TODO test all methods

    private CommandLineService service;

    @Before
    public void prepareService() {
        this.service = getLoggingService(CommandLineService.class, "test");
    }

    @After
    public void closeService() {
        // service instance is closed by parent classes,
        // only need to remove the reference within this class
        this.service = null;
    }

    @Test
    public void testVersion() {
        VersionData data = new VersionData();
        new GitClient(service).version(data);
        System.out.println(data);
        assertFalse("git version number is empty", StringUtil.isEmpty(data.getVersionNumber()));
    }

    @Test
    public void testStatus() {
        StatusData data = new StatusData();
        new GitClient(service).status(data);
        System.out.println("Status: On branch " + data.getCurrentBranch());
        assertFalse("Branch name is empty", StringUtil.isEmpty(data.getCurrentBranch()));
    }

    @Test
    public void testListBranches() {
        BranchListData data = new BranchListData();
        new GitClient(service).listBranches(data);
        System.out.println("Branches:");
        for (StringData branch : data.getBranches()) {
            if (branch.getValue().equals(data.getCurrentBranch())) {
                System.out.print("* ");
            }
            else {
                System.out.print("  ");
            }
            System.out.println(branch);
        }
    }

}
