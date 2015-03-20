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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.aludratest.service.AbstractAludraServiceTest;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.gitclient.data.AddData;
import org.aludratest.service.gitclient.data.BranchCreationData;
import org.aludratest.service.gitclient.data.BranchDeletionData;
import org.aludratest.service.gitclient.data.BranchListData;
import org.aludratest.service.gitclient.data.CloneRepositoryData;
import org.aludratest.service.gitclient.data.CommitData;
import org.aludratest.service.gitclient.data.InvocationData;
import org.aludratest.service.gitclient.data.LogData;
import org.aludratest.service.gitclient.data.LogItemData;
import org.aludratest.service.gitclient.data.MvData;
import org.aludratest.service.gitclient.data.RenamedStatusData;
import org.aludratest.service.gitclient.data.ResetData;
import org.aludratest.service.gitclient.data.RmData;
import org.aludratest.service.gitclient.data.StatusData;
import org.aludratest.service.gitclient.data.VersionData;
import org.aludratest.testcase.After;
import org.aludratest.util.data.StringData;
import org.databene.commons.FileUtil;
import org.databene.commons.IOUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

/** Tests the {@link GitClient} against an installation of git.
 * @author Volker Bergmann */
@SuppressWarnings("javadoc")
public class GitClientIntegrationTest extends AbstractAludraServiceTest {

    private static final String ADDED_FILE = "added.txt";
    private static final String UNTRACKED_FILE = "untracked.txt";
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
    public void testVersion() throws Exception {
        VersionData data = new VersionData();
        new GitClient(service, 10000).version(data);
        System.out.println(data);
        assertFalse("git version number is empty", StringUtil.isEmpty(data.getVersionNumber()));
    }

    @Test
    public void testStatus() throws Exception {
        StatusData data = new StatusData();
        new GitClient(service, 10000).status(data);
        System.out.println("Status: On branch " + data.getCurrentBranch());
        assertFalse("Branch name is empty", StringUtil.isEmpty(data.getCurrentBranch()));
    }

    @Test
    public void testLog() throws Exception {
        LogData data = LogData.createWithMaxCount(5);
        new GitClient(service, 10000).log(data);
        for (LogItemData entry : data.getItems()) {
            System.out.println(entry);
            System.out.println();
        }
    }

    @Test
    public void testAdd() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                createFile("some_file.txt", "some content", true, gitClient);
                StatusData status = getStatus(gitClient);
                List<StringData> addedFiles = status.getAddedFiles();
                assertEquals(1, addedFiles.size());
                assertEquals("some_file.txt", addedFiles.get(0).getValue());
            }
        });
    }

    @Test
    public void testGetCurrentBranch() throws Exception {
        StringData branchData = new StringData();
        new GitClient(service, 10000).getCurrentBranch(branchData);
        assertNotNull(branchData.getValue());
    }

    @Test
    public void testListBranches() throws Exception {
        BranchListData data = new BranchListData();
        new GitClient(service, 10000).listBranches(data);
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

    @Test
    public void testCreateAndDeleteBranch() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                final String branchName = "branch2";
                // there must be a committed file before a branch can be created
                createFile("file1.txt", "content1", true, gitClient);
                gitClient.commit(new CommitData("commit1"));
                // create the branch
                gitClient.createBranch(new BranchCreationData(branchName));
                // verify its existence
                BranchListData list = new BranchListData();
                gitClient.listBranches(list);
                assertTrue(branchExists(branchName, gitClient));
                // delete the branch
                gitClient.deleteBranch(new BranchDeletionData(branchName));
                // verify its absence
                assertFalse(branchExists(branchName, gitClient));
            }
        });
    }

    @Test
    public void testCommit() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                String fileName = "file1.txt";
                // first create and commit a file
                createFile(fileName, "content", true, gitClient);
                // verify its presence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(fileName, status.getAddedFiles()));
                // commit the file
                gitClient.commit(new CommitData("commit1"));
                // verify its change
                status = getStatus(gitClient);
                assertFalse(containsFile(fileName, status.getAddedFiles()));
            }
        });
    }

    @Test
    public void testMv() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                final String fileName1 = "file1.txt";
                final String fileName2 = "file2.txt";
                // first create and commit a file
                createFile(fileName1, "content1", true, gitClient);
                // verify its presence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(fileName1, status.getAddedFiles()));
                // commit the file
                gitClient.commit(new CommitData("commit1"));
                // move the file
                gitClient.mv(new MvData(fileName1, fileName2));
                // verify its change
                status = getStatus(gitClient);
                assertTrue(status.getRenamedFiles().contains(new RenamedStatusData(fileName1, fileName2)));
            }
        });
    }

    @Test
    public void testRm() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                // first create and commit a file
                final String fileName = "file1.txt";
                createFile(fileName, "content1", true, gitClient);
                // verify its presence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(fileName, status.getAddedFiles()));
                // commit the file
                gitClient.commit(new CommitData("commit1"));
                // delete the file
                gitClient.rm(new RmData(fileName));
                // verify its deletion
                status = getStatus(gitClient);
                assertTrue(containsFile(fileName, status.getDeletedFiles()));
            }
        });
    }

    @Test
    public void testResetSoft() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                createTestFilesForReset(gitClient);
                // reset
                gitClient.resetSoft(new ResetData());
                // verify their absence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
                assertTrue(containsFile(ADDED_FILE, status.getAddedFiles()));
            }
        });
    }

    @Test
    public void testResetMixed() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                createTestFilesForReset(gitClient);
                // reset
                gitClient.resetMixed(new ResetData());
                // verify their absence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
                assertFalse(containsFile(ADDED_FILE, status.getAddedFiles()));
                assertTrue(containsFile(ADDED_FILE, status.getUntrackedFiles()));
            }
        });
    }

    @Test
    public void testResetHard() throws Exception {
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                createTestFilesForReset(gitClient);
                // reset
                gitClient.resetHard(new ResetData());
                // verify their absence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
                assertFalse(containsFile(ADDED_FILE, status.getAddedFiles()));
            }
        });
    }

    @Test
    public void testStashSaveAndPop() throws Exception {
        // Skip the test on Mac OSX Yosemite there is a known issue
        if ("Mac OS X".equals(SystemInfo.getOsName()) && SystemInfo.getOsVersion().startsWith("10.10.")) {
            // for the description of the issue see
            // https://stackoverflow.com/questions/24022582/osx-10-10-yosemite-beta-on-git-pull-git-sh-setup-no-such-file-or-directory
            throw new AssumptionViolatedException("Skipping testStashSaveAndPop() due to known git issue on Mac OSX Yosemite.");
        }
        runInNewRepo(new GitTest() {
            @Override
            public void run(GitClient gitClient) throws Exception {
                // create a first commit
                createFile("test.txt", "content", true, gitClient);
                gitClient.commit(new CommitData("initial commit"));
                // create test files
                createTestFilesForReset(gitClient);
                // stash save
                gitClient.stashSave();
                // verify the files' absence
                StatusData status = getStatus(gitClient);
                assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
                assertFalse(containsFile(ADDED_FILE, status.getUntrackedFiles()));
                assertFalse(containsFile(ADDED_FILE, status.getAddedFiles()));
                // stash pop
                gitClient.stashPop();
                // verify the files' absence
                status = getStatus(gitClient);
                assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
                assertTrue(containsFile(ADDED_FILE, status.getAddedFiles()));
            }
        });
    }

    @Test
    public void testCloneRepository() throws Exception {
        File tempDir = createTempDirectory();
        try {
            GitClient gitClient1 = new GitClient(service, 60000).setWorkingDirectory(new StringData(tempDir.getAbsolutePath()));
            gitClient1.cloneRepository(new CloneRepositoryData("https://github.com/AludraTest/aludratest.git"));
            File projectDir = new File(tempDir, "aludratest");
            GitClient gitClient2 = new GitClient(service, 10000)
            .setWorkingDirectory(new StringData(projectDir.getAbsolutePath()));
            getStatus(gitClient2);
        }
        finally {
            FileUtil.deleteDirectory(tempDir);
        }
    }

    @Test
    public void testCheckout() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testFetch() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testMerge() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testPull() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testPush() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testRebase() throws Exception {
        // throw new UnsupportedOperationException("Not implemented"); // TODO implement test
    }

    @Test
    public void testInvokeGenerically() throws Exception {
        InvocationData data = new InvocationData("git", "--version");
        new GitClient(service, 10000).invokeGenerically(data);
        String stdOut = data.getStdOut();
        System.out.println(stdOut);
        assertTrue("Version info does not start with 'git version '", stdOut.startsWith("git version "));
    }

    // private methods ---------------------------------------------------------

    private void runInNewRepo(GitTest test) throws Exception {
        File tempDir = createTempDirectory();
        try {
            GitClient gitClient = new GitClient(service, 10000).setWorkingDirectory(new StringData(tempDir.getAbsolutePath()));
            gitClient.init();
            test.run(gitClient);
        }
        finally {
            FileUtil.deleteDirectory(tempDir);
        }
    }

    private File createTempDirectory() {
        String tempRoot = SystemInfo.getTempDir();
        File tempDir = new File(tempRoot, UUID.randomUUID().toString());
        tempDir.mkdir();
        return tempDir;
    }

    private static File createFile(String name, String content, boolean add, GitClient gitClient) throws IOException {
        String tempDir = gitClient.getWorkingDirectory().getValue();
        File file = new File(tempDir, name);
        IOUtil.writeTextFile(file.getAbsolutePath(), content);
        if (add) {
            gitClient.add(new AddData(name));
        }
        return file;
    }

    private static StatusData getStatus(GitClient gitClient) {
        StatusData status = new StatusData();
        gitClient.status(status);
        System.out.println(status);
        return status;
    }

    private static boolean branchExists(String branchName, GitClient gitClient) {
        BranchListData list = new BranchListData();
        gitClient.listBranches(list);
        for (StringData branch : list.getBranches()) {
            if (branch.getValue().equals(branchName)) {
                return true;
            }
        }
        return false;
    }

    private void createTestFilesForReset(GitClient gitClient) throws IOException {
        // give git-reset a target commit
        InvocationData data = new InvocationData("git", "commit", "--allow-empty", "-m", "Initial commit.");
        gitClient.invokeGenerically(data);
        // create the files
        createFile(UNTRACKED_FILE, "untracked", false, gitClient);
        createFile(ADDED_FILE, "added", true, gitClient);
        // verify their presence
        StatusData status = getStatus(gitClient);
        assertTrue(containsFile(UNTRACKED_FILE, status.getUntrackedFiles()));
        assertTrue(containsFile(ADDED_FILE, status.getAddedFiles()));
    }

    private static boolean containsFile(String fileName, List<StringData> list) {
        for (StringData file : list) {
            if (file.getValue().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public static abstract class GitTest {
        public abstract void run(GitClient gitClient) throws Exception;
    }

}
