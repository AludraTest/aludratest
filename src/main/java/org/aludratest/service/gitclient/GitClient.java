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
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.TechnicalException;
import org.aludratest.service.cmdline.CommandLineProcess;
import org.aludratest.service.cmdline.CommandLineService;
import org.aludratest.service.gitclient.data.AddData;
import org.aludratest.service.gitclient.data.BranchCreationData;
import org.aludratest.service.gitclient.data.BranchDeletionData;
import org.aludratest.service.gitclient.data.BranchListData;
import org.aludratest.service.gitclient.data.CheckoutData;
import org.aludratest.service.gitclient.data.CloneRepositoryData;
import org.aludratest.service.gitclient.data.CommitData;
import org.aludratest.service.gitclient.data.ConfigData;
import org.aludratest.service.gitclient.data.FetchData;
import org.aludratest.service.gitclient.data.InvocationData;
import org.aludratest.service.gitclient.data.LogData;
import org.aludratest.service.gitclient.data.LogItemData;
import org.aludratest.service.gitclient.data.MergeData;
import org.aludratest.service.gitclient.data.MvData;
import org.aludratest.service.gitclient.data.PullData;
import org.aludratest.service.gitclient.data.PushData;
import org.aludratest.service.gitclient.data.RebaseData;
import org.aludratest.service.gitclient.data.RenamedStatusData;
import org.aludratest.service.gitclient.data.ResetData;
import org.aludratest.service.gitclient.data.RmData;
import org.aludratest.service.gitclient.data.StatusData;
import org.aludratest.service.gitclient.data.VersionData;
import org.aludratest.util.data.StringData;
import org.apache.commons.io.LineIterator;
import org.databene.commons.ArrayBuilder;
import org.databene.commons.Assert;
import org.databene.commons.CollectionUtil;
import org.databene.commons.StringUtil;
import org.databene.commons.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides access to a git command line client using the {@link CommandLineService}.
 * @author Volker Bergmann */
public class GitClient implements ActionWordLibrary<GitClient> {

    private static final String GIT_COMMAND = "git";

    private static final String GIT_PROCESS_TYPE = "git";

    private static final String GIT_CONFIG_PROCESS_NAME = "config";
    private static final String GIT_VERSION_PROCESS_NAME = "--version";
    private static final String GIT_STATUS_PROCESS_NAME = "status";
    private static final String GIT_LOG_PROCESS_NAME = "log";
    private static final String GIT_STASH_SAVE_PROCESS_NAME = "stash save";
    private static final String GIT_STASH_POP_PROCESS_NAME = "stash pop";
    private static final String GIT_ADD_PROCESS_NAME = "add";
    private static final String GIT_RM_PROCESS_NAME = "rm";
    private static final String GIT_MV_PROCESS_NAME = "mv";
    private static final String GIT_CLONE_PROCESS_NAME = "clone";
    private static final String GIT_FETCH_PROCESS_NAME = "fetch";
    private static final String GIT_INIT_PROCESS_NAME = "init";
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GitClient.class);

    private static final Object LF = SystemInfo.getLineSeparator();

    private final CommandLineService service;

    private String relativeWorkingDirectory;
    private int processTimeout;
    private int responseTimeout;

    /** @param service */
    public GitClient(CommandLineService service) {
        this(service, 10000, 3000);
    }

    /** @param service
     * @param processTimeout
     * @param responseTimeout */
    public GitClient(CommandLineService service, int processTimeout, int responseTimeout) {
        this.processTimeout = processTimeout;
        this.responseTimeout = responseTimeout;
        this.service = service;
        this.relativeWorkingDirectory = ".";
    }

    /** Returns the working directory of the process.
     * @return the working directory of the process */
    public StringData getRelativeWorkingDirectory() {
        return new StringData(relativeWorkingDirectory);
    }

    /** Sets the working directory.
     * @param relativeWorkingDirectory the workingDirectory to set.
     * @return a reference to this */
    public GitClient setRelativeWorkingDirectory(StringData relativeWorkingDirectory) {
        this.relativeWorkingDirectory = relativeWorkingDirectory.getValue();
        return this;
    }

    /**
     * @return the associated CommandLineService's configured base.directory
     */
    public String getBaseDirectory() {
        return service.getBaseDirectory();
    }

    // operational interface ---------------------------------------------------

    /** Calls git's config feature.
     * @param data the settings to apply
     * @return a reference to this */
    public GitClient config(ConfigData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class).add("config");
        if (!StringUtil.isEmpty(data.getKey())) {
            builder.add(data.getKey());
            if (!StringUtil.isEmpty(data.getValue())) {
                builder.add('"' + data.getValue() + '"');
                if (!StringUtil.isEmpty(data.getValueRegex())) {
                    builder.add(data.getValueRegex());
                }
            }
        }
        invokeGenericallyAndGetStdOut(GIT_CONFIG_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Queries the git client for its version number.
     * @param data an instance of the data class that receives the query result
     * @return a reference to this */
    public GitClient version(VersionData data) {
        String output = invokeGenericallyAndGetStdOut(GIT_VERSION_PROCESS_NAME, true, "--version");
        String versionNumber = extractVersionNumber(output);
        data.setVersionNumber(versionNumber);
        return this;
    }

    /** Provides the status.
     * @param data
     * @return a reference to this */
    public GitClient status(StatusData data) {

        // clear status object for supporting data object reuse
        data.getUntrackedFiles().clear();
        data.getUnmodifiedFiles().clear();
        data.getModifiedFiles().clear();
        data.getAddedFiles().clear();
        data.getDeletedFiles().clear();
        data.getRenamedFiles().clear();
        data.getCopiedFiles().clear();
        data.getUpdatedFiles().clear();

        // invoke git
        String output = invokeGenericallyAndGetStdOut(GIT_STATUS_PROCESS_NAME, true, "status", "--short", "--branch");
        LineIterator iterator = new LineIterator(new StringReader(output));
        while (iterator.hasNext()) {
            String line = iterator.next();
            LOGGER.debug("Git Status output: {}", line);
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
                        data.getRenamedFiles().add(parseRename(filePath.getValue()));
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

    /** Provides the git log.
     * @param data
     * @return */
    public GitClient log(LogData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class).add("log");
        if (data.getMaxCount() != null) {
            builder.add("--max-count=" + data.getMaxCount());
        }
        String output = invokeGenericallyAndGetStdOut(GIT_LOG_PROCESS_NAME, true, builder.toArray());
        LineIterator iterator = new LineIterator(new StringReader(output));
        while (iterator.hasNext()) {
            parseLogItem(iterator, data);
        }
        return this;
    }

    /** Adds files to the index
     * @param data
     * @return a reference to this */
    public GitClient add(AddData data) {
        invokeGenericallyAndGetStdOut(GIT_ADD_PROCESS_NAME, true, "add", data.getFilePattern());
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
        String output = invokeGenericallyAndGetStdOut(GIT_LIST_BRANCHES_PROCESS_NAME, true, "branch", "--list");
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
        invokeGenericallyAndGetStdOut(GIT_CREATE_BRANCH_PROCESS_NAME, true, "branch", data.getBranchName());
        return this;
    }

    /** Deletes a branch.
     * @param data
     * @return a reference to this */
    public GitClient deleteBranch(BranchDeletionData data) {
        invokeGenericallyAndGetStdOut(GIT_DELETE_BRANCH_PROCESS_NAME, true, "branch", "--delete", data.getBranchName());
        return this;
    }

    /** Checks out a branch or paths to the working tree.
     * @param data
     * @return a reference to this */
    public GitClient checkout(CheckoutData data) {
        CommandLineProcess<?> process = invokeGenerically(GIT_CHECKOUT_PROCESS_NAME, false, "checkout", data.getBranchName());
        String expectedErrOut = "Switched to branch '" + data.getBranchName() + "'";
        String actualErrOut = getErrOut(process, true);
        if (!actualErrOut.equals(expectedErrOut)) {
            throw new AutomationException("Expected err out \"" + expectedErrOut + "\", but encountered \"" + actualErrOut + "\"");
        }
        return this;
    }

    /** Clones a repository into a new directory.
     * @param data
     * @return a reference to this */
    public GitClient cloneRepository(CloneRepositoryData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("clone");
        builder.add(Assert.notNull(data.getRepository(), "repository"));
        if (!StringUtil.isEmpty(data.getDirectory())) {
            builder.add(data.getDirectory());
        }
        invokeGenericallyAndGetStdOut(GIT_CLONE_PROCESS_NAME, false, builder.toArray());
        return this;
    }

    /** Records changes to the repository.
     * @param data
     * @return a reference to this */
    public GitClient commit(CommitData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("commit");
        if (Boolean.parseBoolean(data.getAllowEmpty())) {
            builder.add("--allow-empty");
        }
        if (!StringUtil.isEmpty(data.getMessage())) {
            builder.add("-m").add(quoteArg(data.getMessage()));
        }
        invokeGenericallyAndGetStdOut(GIT_COMMIT_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Downloads objects and refs from another repository.
     * @param data
     * @return a reference to this */
    public GitClient fetch(FetchData data) {
        CommandLineProcess<?> process = invokeGenerically(GIT_FETCH_PROCESS_NAME, false, "fetch", data.getRepository());
        String errOut = getErrOut(process, true);
        if (!errOut.startsWith("From ")) {
            throw new AutomationException("Unexpected error output: " + errOut);
        }
        return this;
    }

    /** Creates an empty git repository or reinitializes an existing one.
     * @return a reference to this */
    public GitClient init() {
        invokeGenericallyAndGetStdOut(GIT_INIT_PROCESS_NAME, true, "init");
        return this;
    }

    /** Join two or more development histories together.
     * @param data
     * @return a reference to this */
    public GitClient merge(MergeData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class);
        builder.add("merge");
        if (!StringUtil.isEmpty(data.getMessage())) {
            builder.add("-m").add(quoteArg(data.getMessage()));
        }
        for (StringData branch : data.getBranches()) {
            builder.add(branch.getValue());
        }
        invokeGenericallyAndGetStdOut(GIT_MERGE_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Moves or renames a file, directory, or symlink
     * @param data
     * @return a reference to this */
    public GitClient mv(MvData data) {
        invokeGenericallyAndGetStdOut(GIT_MV_PROCESS_NAME, true, "mv", data.getSource(), data.getDestination());
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
        invokeGenericallyAndGetStdOut(GIT_PULL_PROCESS_NAME, true, builder.toArray());
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
        CommandLineProcess<?> process = invokeGenerically(GIT_PUSH_PROCESS_NAME, false, builder.toArray());
        String errOut = getErrOut(process, true);
        if (!errOut.startsWith("To ")) {
            throw new AutomationException("Unexpected error output: " + errOut);
        }
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
        invokeGenericallyAndGetStdOut(GIT_REBASE_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Resets the current HEAD to the specified state. It does not touch the index file nor the working tree at all (but resets
     * the head to the specified commit, just like all modes do). This leaves all your changed files "Changes to be committed", as
     * git status would put it.
     * @param data
     * @return a reference to this */
    public GitClient resetSoft(ResetData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class).add("reset").add("--soft");
        if (data.getCommit() != null) {
            builder.add(data.getCommit());
        }
        invokeGenericallyAndGetStdOut(GIT_RESET_SOFT_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Resets the current HEAD to the specified state. Resets the index but not the working tree (i.e., the changed files are
     * preserved but not marked for commit) and reports what has not been updated. This is the default action.
     * @param data
     * @return a reference to this */
    public GitClient resetMixed(ResetData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class).add("reset").add("--mixed");
        if (data.getCommit() != null) {
            builder.add(data.getCommit());
        }
        invokeGenericallyAndGetStdOut(GIT_RESET_MIXED_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Resets the current HEAD to the specified state. Resets the index and working tree. Any changes to tracked files in the
     * working tree since the specified commit are discarded.
     * @param data
     * @return a reference to this */
    public GitClient resetHard(ResetData data) {
        ArrayBuilder<String> builder = new ArrayBuilder<String>(String.class).add("reset").add("--hard");
        if (data.getCommit() != null) {
            builder.add(data.getCommit());
        }
        invokeGenericallyAndGetStdOut(GIT_RESET_HARD_PROCESS_NAME, true, builder.toArray());
        return this;
    }

    /** Removes files from the working tree and from the index.
     * @param data
     * @return a reference to this */
    public GitClient rm(RmData data) {
        invokeGenericallyAndGetStdOut(GIT_RM_PROCESS_NAME, true, "rm", data.getFilePattern());
        return this;
    }

    /** Saves the workspace to the stash.
     * @return a reference to this */
    public GitClient stashSave() {
        invokeGenericallyAndGetStdOut(GIT_STASH_SAVE_PROCESS_NAME, true, "stash", "save");
        return this;
    }

    /** Puts back previously stashed contents to the workspace.
     * @return a reference to this */
    public GitClient stashPop() {
        invokeGenericallyAndGetStdOut(GIT_STASH_POP_PROCESS_NAME, true, "stash", "pop");
        return this;
    }

    /** Provides individually parameterized git invocations.
     * @param data the invocation data
     * @return the process' output to stdout */
    public GitClient invokeGenerically(InvocationData data) {
        boolean failOnErrOut = Boolean.parseBoolean(data.getFailOnErrOut());
        String stdOut = invokeGenericallyAndGetStdOut(data.getProcessName(), failOnErrOut, CollectionUtil.toArray(data.getArgs()));
        data.setStdOut(stdOut);
        return this;
    }

    // internal helper methods -------------------------------------------------

    /** Provides individually parameterized git invocations.
     * @param processName
     * @param args
     * @return the process' output to stdout */
    private String invokeGenericallyAndGetStdOut(String processName, boolean failOnErrOut, String... args) {
        CommandLineProcess<?> process = invokeGenerically(processName, failOnErrOut, args);
        return getStdOut(process);
    }

    private CommandLineProcess<?> invokeGenerically(String processName, boolean failOnErrOut, String... args) {
        // prepare invocation
        String[] commands = new String[args.length + 1];
        commands[0] = GIT_COMMAND;
        System.arraycopy(args, 0, commands, 1, args.length);
        @SuppressWarnings("rawtypes")
        CommandLineProcess<?> process = new CommandLineProcess(GIT_PROCESS_TYPE, processName, service, processTimeout,
                responseTimeout, commands);
        process.setRelativeWorkingDirectory(relativeWorkingDirectory);

        // invoke and wait until finished
        LOGGER.debug("Starting command line process: {}", process);
        process.start();
        process.waitUntilFinished();

        // check err out
        StringData errorOutput = new StringData();
        process.errOut().redirectTo(errorOutput);
        if (failOnErrOut && !StringUtil.isEmpty(errorOutput.getValue())) {
            throw new TechnicalException("Error invoking process: " + errorOutput);
        }
        return process;
    }

    private String quoteArg(String arg) {
        return '"' + arg + '"';
    }

    private String getStdOut(CommandLineProcess<?> process) {
        StringData textOutput = new StringData();
        process.stdOut().redirectTo(textOutput);
        return textOutput.getValue();
    }

    private String getErrOut(CommandLineProcess<?> process, boolean trim) {
        StringData textOutput = new StringData();
        process.errOut().redirectTo(textOutput);
        String text = textOutput.getValue();
        if (trim) {
            text = text.trim();
        }
        return text;
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

    private void parseLogItem(LineIterator iterator, LogData data) {
        String commit = null;
        String merge = null;
        String author = null;
        String date = null;
        // parse headers
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith("commit ")) {
                commit = parseKeyValuePair("commit ", line);
            }
            else if (line.startsWith("Merge: ")) {
                merge = parseKeyValuePair("Merge: ", line);
            }
            else if (line.startsWith("Author: ")) {
                author = parseKeyValuePair("Author: ", line);
            }
            else if (line.startsWith("Date: ")) {
                date = parseKeyValuePair("Date: ", line);
            }
            else if (StringUtil.isEmpty(line)) {
                break;
            }
            else {
                LOGGER.warn("Unrecognized log line: " + line);
            }
        }
        // parse message
        String message = parseMessage(iterator);
        // add log item
        LogItemData item = new LogItemData(commit, merge, author, date, message);
        data.getItems().add(item);
    }

    private String parseKeyValuePair(String key, String line) {
        return line.substring(key.length()).trim();
    }

    private String parseMessage(LineIterator iterator) {
        if (!iterator.hasNext()) {
            throw new TechnicalException("Expected commit message not available");
        }
        StringBuilder message = new StringBuilder();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (!StringUtil.isEmpty(line)) {
                if (message.length() > 0) {
                    message.append(LF);
                }
                message.append(line.trim());
            }
            else {
                return message.toString();
            }
        }
        return message.toString();
    }

    /** Parses file status information like <code>file1.txt -> file2.txt</code>.
     * @param fileInfo
     * @return */
    static RenamedStatusData parseRename(String fileInfo) {
        String separator = " -> ";
        int sepIndex1 = fileInfo.indexOf(separator);
        if (sepIndex1 < 0) {
            throw new TechnicalException("Unsupported change format: " + fileInfo);
        }
        int sepIndex2 = sepIndex1 + separator.length();
        String fromPath = fileInfo.substring(0, sepIndex1);
        String toPath = fileInfo.substring(sepIndex2);
        return new RenamedStatusData(fromPath, toPath);
    }

    // ActionWordLibrary implementation ----------------------------------------

    @Override
    public GitClient verifyState() {
        return null;
    }

}
