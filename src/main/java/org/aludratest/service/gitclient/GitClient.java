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

import java.io.StringReader;

import org.aludratest.dict.ActionWordLibrary;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.cmdline.CommandLineProcess;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.gitclient.data.AddData;
import org.aludratest.service.gitclient.data.BranchCreationData;
import org.aludratest.service.gitclient.data.BranchDeletionData;
import org.aludratest.service.gitclient.data.BranchListData;
import org.aludratest.service.gitclient.data.CheckoutData;
import org.aludratest.service.gitclient.data.CloneData;
import org.aludratest.service.gitclient.data.CommitData;
import org.aludratest.service.gitclient.data.FetchData;
import org.aludratest.service.gitclient.data.MergeData;
import org.aludratest.service.gitclient.data.MvData;
import org.aludratest.service.gitclient.data.PullData;
import org.aludratest.service.gitclient.data.PushData;
import org.aludratest.service.gitclient.data.RebaseData;
import org.aludratest.service.gitclient.data.ResetData;
import org.aludratest.service.gitclient.data.RmData;
import org.aludratest.service.gitclient.data.StatusData;
import org.aludratest.service.gitclient.data.VersionData;
import org.aludratest.util.data.StringData;
import org.apache.commons.io.LineIterator;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;

/** Provides access to a git command line client using the {@link CommandLineService}.
 * @author Volker Bergmann */
public class GitClient implements ActionWordLibrary<GitClient> {

    private static final String GIT_COMMAND = "git";

    private static final String GIT_PROCESS_TYPE = "git";
    private static final String GIT_VERSION_PROCESS_NAME = "--version";
    private static final String GIT_STATUS_PROCESS_NAME = "status";
    private static final String GIT_STASH_SAVE_PROCESS_NAME = "stash save";
    private static final String GIT_STASH_POP_PROCESS_NAME = "stash pop";
    private static final String GIT_ADD_PROCESS_NAME = "add";
    private static final String GIT_RM_PROCESS_NAME = "rm";
    private static final String GIT_MV_PROCESS_NAME = "mv";
    private static final String GIT_CLONE_PROCESS_NAME = "clone";
    private static final String GIT_LIST_BRANCHES_PROCESS_NAME = "branch --list";
    private static final String GIT_CREATE_BRANCH_PROCESS_NAME = "branch (create)";
    private static final String GIT_DELETE_BRANCH_PROCESS_NAME = "branch --delete";
    private static final String GIT_CHECKOUT_PROCESS_NAME = "checkout";
    private static final String GIT_COMMIT_PROCESS_NAME = "commit";
    private static final String GIT_MERGE_PROCESS_NAME = "merge";
    private static final String GIT_PULL_PROCESS_NAME = "pull";
    private static final String GIT_PUSH_PROCESS_NAME = "push";
    private static final String GIT_REBASE_PROCESS_NAME = "rebase";
    private static final String GIT_RESET_SOFT_PROCESS_NAME = "reset --soft";
    private static final String GIT_RESET_MIXED_PROCESS_NAME = "reset --mixed";
    private static final String GIT_RESET_HARD_PROCESS_NAME = "reset --hard";

    private final CommandLineService service;

    private String workingDirectory;
    private int timeout;

    /** @param service */
    public GitClient(CommandLineService service) {
        this(service, 3000);
    }

    /** @param service
     * @param timeout */
    public GitClient(CommandLineService service, int timeout) {
        this.timeout = timeout;
        this.service = service;
        this.workingDirectory = SystemInfo.getCurrentDir();
        // try to call the git client in order to prove its availability
        version(new VersionData());
    }

    // operational interface ---------------------------------------------------

    /** Queries the git client for its version number.
     * @param data an instance of the data class that receives the query result
     * @return a reference to this */
    public GitClient version(VersionData data) {
        String output = invokeGenerically(GIT_VERSION_PROCESS_NAME, "--version");
        String versionNumber = extractVersionNumber(output);
        data.setVersionNumber(versionNumber);
        return this;
    }

    /** Provides the status.
     * @param data
     * @return a reference to this */
    public GitClient status(StatusData data) {
        String output = invokeGenerically(GIT_STATUS_PROCESS_NAME, "status", "--short", "--branch");
        LineIterator iterator = new LineIterator(new StringReader(output));
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith("##")) {
                data.setCurrentBranch(line.substring(3));
            }
            else {
                StringData filePath = new StringData(line.substring(3));
                char statusCode = line.substring(0, 2).trim().charAt(0);
                switch (statusCode) {
                    case '?':
                        data.getUntrackedFiles().add(filePath);
                        break;
                    case '\'':
                        data.getUnmodifiedFiles().add(filePath);
                        break;
                    case 'M':
                        data.getModifiedFiles().add(filePath);
                        break;
                    case 'A':
                        data.getAddedFiles().add(filePath);
                        break;
                    case 'D':
                        data.getDeletedFiles().add(filePath);
                        break;
                    case 'R':
                        data.getRenamedFiles().add(filePath);
                        break;
                    case 'C':
                        data.getCopiedFiles().add(filePath);
                        break;
                    case 'U':
                        data.getUpdatedFiles().add(filePath);
                        break;
                    default:
                        throw new TechnicalException("Unknown status '" + statusCode + "' in git output: " + line);
                }
            }
        }
        return this;
    }

    /** Adds files to the index
     * @param data
     * @return a reference to this */
    public GitClient add(AddData data) {
        invokeGenerically(GIT_ADD_PROCESS_NAME, "add", data.getFilePattern());
        return this;
    }

    /** Provides the name of the current branch.
     * @param data a StringData object that receives the operations result
     * @return a reference to this */
    public GitClient getCurrentBranch(StringData data) {
        BranchListData list = new BranchListData();
        listBranches(list);
        data.setValue(list.getCurrentBranch());
        return this;
    }

    /** Lists branches.
     * @param data
     * @return a reference to this */
    public GitClient listBranches(BranchListData data) {
        String output = invokeGenerically(GIT_LIST_BRANCHES_PROCESS_NAME, "branch", "--list");
        LineIterator iterator = new LineIterator(new StringReader(output));
        while (iterator.hasNext()) {
            String line = iterator.next();
            boolean current = line.startsWith("*");
            String branch = line.substring(2).trim();
            data.getBranches().add(new StringData(branch));
            if (current) {
                data.setCurrentBranch(branch);
            }
        }
        return this;
    }

    /** Creates a branch.
     * @param data
     * @return a reference to this */
    public GitClient createBranch(BranchCreationData data) {
        invokeGenerically(GIT_CREATE_BRANCH_PROCESS_NAME, "branch", data.getBranchName());
        return this;
    }

    /** Deletes a branch.
     * @param data
     * @return a reference to this */
    public GitClient deleteBranch(BranchDeletionData data) {
        invokeGenerically(GIT_DELETE_BRANCH_PROCESS_NAME, "branch", "--delete", data.getBranchName());
        return this;
    }

    /** Checks out a branch or paths to the working tree.
     * @param data
     * @return a reference to this */
    public GitClient checkout(CheckoutData data) {
        invokeGenerically(GIT_CHECKOUT_PROCESS_NAME, "checkout", data.getBranchName());
        return this;
    }

    /** Clones a repository into a new directory.
     * @param data
     * @return a reference to this */
    public GitClient clone(CloneData data) {
        invokeGenerically(GIT_CLONE_PROCESS_NAME, "clone", data.getRepository());
        return this;
    }

    /** Records changes to the repository.
     * @param data
     * @return a reference to this */
    public GitClient commit(CommitData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("commit");
        if (!StringUtil.isEmpty(data.getMessage())) {
            builder.add("-m").add(escapeArg(data.getMessage()));
        }
        invokeGenerically(GIT_COMMIT_PROCESS_NAME, builder.toArray());
        return this;
    }

    /** Downloads objects and refs from another repository.
     * @param data
     * @return a reference to this */
    public GitClient fetch(FetchData data) {
        invokeGenerically(GIT_CLONE_PROCESS_NAME, "fetch", data.getRepository());
        return this;
    }

    /** Creates an empty git repository or reinitializes an existing one.
     * @return a reference to this */
    public GitClient init() {
        invokeGenerically(GIT_CLONE_PROCESS_NAME, "init");
        return this;
    }

    /** Join two or more development histories together.
     * @param data
     * @return a reference to this */
    public GitClient merge(MergeData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("merge");
        if (!StringUtil.isEmpty(data.getMessage())) {
            builder.add("-m").add(escapeArg(data.getMessage()));
        }
        for (StringData branch : data.getBranches()) {
            builder.add(branch.getValue());
        }
        invokeGenerically(GIT_MERGE_PROCESS_NAME, builder.toArray());
        return this;
    }

    /** Moves or renames a file, directory, or symlink
     * @param data
     * @return a reference to this */
    public GitClient mv(MvData data) {
        invokeGenerically(GIT_MV_PROCESS_NAME, "mv", data.getSource(), data.getDestination());
        return this;
    }

    /** Fetches from and merges with another repository or a local branch
     * @param data
     * @return a reference to this */
    public GitClient pull(PullData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("pull");
        if (!StringUtil.isEmpty(data.getRepository())) {
            builder.add(data.getRepository());
            if (!StringUtil.isEmpty(data.getRefspec())) {
                builder.add(data.getRefspec());
            }
        }
        invokeGenerically(GIT_PULL_PROCESS_NAME, builder.toArray());
        return this;
    }

    /** Updates remote refs along with associated objects.
     * @param data
     * @return a reference to this */
    public GitClient push(PushData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("push");
        if (!StringUtil.isEmpty(data.getRepository())) {
            builder.add(data.getRepository());
            if (!StringUtil.isEmpty(data.getRefspec())) {
                builder.add(data.getRefspec());
            }
        }
        invokeGenerically(GIT_PUSH_PROCESS_NAME, builder.toArray());
        return this;
    }

    /** Forward-ports local commits to the updated upstream head.
     * @param data
     * @return a reference to this */
    public GitClient rebase(RebaseData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("rebase");
        if (!StringUtil.isEmpty(data.getNewbase())) {
            builder.add("--onto").add(data.getNewbase());
        }
        if (!StringUtil.isEmpty(data.getUpstream())) {
            builder.add(data.getUpstream());
        }
        for (StringData branch : data.getBranches()) {
            builder.add(branch.getValue());
        }
        invokeGenerically(GIT_REBASE_PROCESS_NAME, builder.toArray());
        return this;
    }

    /** Resets the current HEAD to the specified state. It does not touch the index file nor the working tree at all (but resets
     * the head to the specified commit, just like all modes do). This leaves all your changed files "Changes to be committed", as
     * git status would put it.
     * @param data
     * @return a reference to this */
    public GitClient resetSoft(ResetData data) {
        invokeGenerically(GIT_RESET_SOFT_PROCESS_NAME, "reset", "--soft", data.getCommit());
        return this;
    }

    /** Resets the current HEAD to the specified state. Resets the index but not the working tree (i.e., the changed files are
     * preserved but not marked for commit) and reports what has not been updated. This is the default action.
     * @param data
     * @return a reference to this */
    public GitClient resetMixed(ResetData data) {
        invokeGenerically(GIT_RESET_MIXED_PROCESS_NAME, "reset", "--mixed", data.getCommit());
        return this;
    }

    /** Resets the current HEAD to the specified state. Resets the index and working tree. Any changes to tracked files in the
     * working tree since the specified commit are discarded.
     * @param data
     * @return a reference to this */
    public GitClient resetHard(ResetData data) {
        invokeGenerically(GIT_RESET_HARD_PROCESS_NAME, "reset", "--hard", data.getCommit());
        return this;
    }

    /** Removes files from the working tree and from the index.
     * @param data
     * @return a reference to this */
    public GitClient rm(RmData data) {
        invokeGenerically(GIT_RM_PROCESS_NAME, "rm", data.getFilePattern());
        return this;
    }

    /** Saves the workspace to the stash.
     * @return a reference to this */
    public GitClient stashSave() {
        invokeGenerically(GIT_STASH_SAVE_PROCESS_NAME, "stash", "save");
        return this;
    }

    /** Puts back previously stashed contents to the workspace.
     * @return a reference to this */
    public GitClient stashPop() {
        invokeGenerically(GIT_STASH_POP_PROCESS_NAME, "stash", "pop");
        return this;
    }

    /** Provides individually parameterized git invocations.
     * @param processName
     * @param args
     * @return the process' output to stdout */
    public String invokeGenerically(String processName, String... args) {
        // prepare invocation
        String[] commands = new String[args.length + 1];
        commands[0] = GIT_COMMAND;
        System.arraycopy(args, 0, commands, 1, args.length);
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess(GIT_PROCESS_TYPE, processName, service, timeout, commands);
        if (!StringUtil.isEmpty(workingDirectory)) {
            process.setWorkingDirectory(workingDirectory);
        }

        // invoke and wait until finished
        process.start();
        process.waitUntilFinished();

        // check err out
        StringData errorOutput = new StringData();
        process.errOut().redirectTo(errorOutput);
        if (!StringUtil.isEmpty(errorOutput.getValue())) {
            throw new TechnicalException("Error invoking process: " + errorOutput);
        }

        // get and return stdout
        StringData textOutput = new StringData();
        process.stdOut().redirectTo(textOutput);
        return textOutput.getValue();
    }

    // internal helper methods -------------------------------------------------

    private String escapeArg(String arg) {
        return (containsWhitespace(arg) ? '"' + arg + '"' : arg);
    }

    private boolean containsWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    static String extractVersionNumber(String text) {
        String expectedHeader = "git version ";
        if (!text.startsWith(expectedHeader)) {
            throw new TechnicalException("Unexpected output: " + text);
        }
        text = text.substring(expectedHeader.length()).trim();
        String[] tokens = StringUtil.splitOnFirstSeparator(text, ' ');
        String versionNumber = tokens[0];
        return versionNumber;
    }

    // ActionWordLibrary implementation ----------------------------------------

    @Override
    public GitClient verifyState() {
        return null;
    }

}
