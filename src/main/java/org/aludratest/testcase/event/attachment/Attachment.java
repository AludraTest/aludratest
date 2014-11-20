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
package org.aludratest.testcase.event.attachment;

/**
 * An Attachment is used to add some additional informations as file (e.g. html or png) to a TestStep.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public abstract class Attachment {

    private String label;

    private String fileName;

    protected Attachment(String label) {
        this.setLabel(label);
    }

    /** @return the file extension */
    public abstract String getFileExtension();

    /** @return the binary file data */
    public abstract byte[] getFileData();

    /** Sets the {@link #fileName}.
     *  @param fileName the file name to set */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** @return the {@link #fileName} */
    public String getFileName() {
        return fileName;
    }

    /** Sets the {@link #label}
     *  @param label the label to set */
    public void setLabel(String label) {
        this.label = label;
    }

    /** @return the label */
    public String getLabel() {
        return label;
    }

    /** @return the file data formatted as Base64 string */
    public abstract String getFileDataAsBase64String();

}
