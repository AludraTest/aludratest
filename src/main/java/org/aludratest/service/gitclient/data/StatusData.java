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
package org.aludratest.service.gitclient.data;

import java.util.ArrayList;
import java.util.List;

import org.aludratest.service.gitclient.GitClient;
import org.aludratest.util.data.StringData;

/** Wraps data for the invocation of the {@link GitClient}'s status method.
 * @see GitClient#status
 * @author Volker Bergmann */
public class StatusData extends AbstractGitData {

    private String currentBranch;
    private List<StringData> untrackedFiles;
    private List<StringData> unmodifiedFiles;
    private List<StringData> modifiedFiles;
    private List<StringData> addedFiles;
    private List<StringData> deletedFiles;
    private List<StringData> renamedFiles;
    private List<StringData> copiedFiles;
    private List<StringData> updatedFiles;

    /** Public default constructor. */
    public StatusData() {
        setCurrentBranch(null);
        this.untrackedFiles = new ArrayList<StringData>();
        this.unmodifiedFiles = new ArrayList<StringData>();
        this.modifiedFiles = new ArrayList<StringData>();
        this.addedFiles = new ArrayList<StringData>();
        this.deletedFiles = new ArrayList<StringData>();
        this.renamedFiles = new ArrayList<StringData>();
        this.copiedFiles = new ArrayList<StringData>();
        this.updatedFiles = new ArrayList<StringData>();
    }

    /** @return the name of the current branch */
    public String getCurrentBranch() {
        return currentBranch;
    }

    /** Sets the name of the current branch.
     * @param currentBranch the name of the current branch to set */
    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }

    /** @return the list of untracked files. */
    public List<StringData> getUntrackedFiles() {
        return untrackedFiles;
    }

    /** Sets the list of untracked files
     * @param untrackedFiles the list of to set */
    public void setUntrackedFiles(List<StringData> untrackedFiles) {
        this.untrackedFiles = untrackedFiles;
    }

    /** @return the list of unmodified files */
    public List<StringData> getUnmodifiedFiles() {
        return unmodifiedFiles;
    }

    /** Sets the list of unmodified files
     * @param unmodifiedFiles the list of unmodified files to set */
    public void setUnmodifiedFiles(List<StringData> unmodifiedFiles) {
        this.unmodifiedFiles = unmodifiedFiles;
    }

    /** @return the list of modified files */
    public List<StringData> getModifiedFiles() {
        return modifiedFiles;
    }

    /** Sets the list of modified files
     * @param modifiedFiles the list of modified files to set */
    public void setModifiedFiles(List<StringData> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }

    /** @return the list of added files */
    public List<StringData> getAddedFiles() {
        return addedFiles;
    }

    /** Sets the list of added files
     * @param addedFiles the list of added files */
    public void setAddedFiles(List<StringData> addedFiles) {
        this.addedFiles = addedFiles;
    }

    /** @return the list of deleted files */
    public List<StringData> getDeletedFiles() {
        return deletedFiles;
    }

    /** Sets the list of deleted files
     * @param deletedFiles the list of deleted files to set */
    public void setDeletedFiles(List<StringData> deletedFiles) {
        this.deletedFiles = deletedFiles;
    }

    /** @return the list of renamed files */
    public List<StringData> getRenamedFiles() {
        return renamedFiles;
    }

    /** Sets the list of renamed files
     * @param renamedFiles the list of renamed files to set */
    public void setRenamedFiles(List<StringData> renamedFiles) {
        this.renamedFiles = renamedFiles;
    }

    /** @return the list of copied files */
    public List<StringData> getCopiedFiles() {
        return copiedFiles;
    }

    /** Sets the list of copied files
     * @param copiedFiles the list of copied files to set */
    public void setCopiedFiles(List<StringData> copiedFiles) {
        this.copiedFiles = copiedFiles;
    }

    /** @return the list of updated files */
    public List<StringData> getUpdatedFiles() {
        return updatedFiles;
    }

    /** Sets the list of updated files
     * @param updatedFiles the list of updated files to set */
    public void setUpdatedFiles(List<StringData> updatedFiles) {
        this.updatedFiles = updatedFiles;
    }

}
