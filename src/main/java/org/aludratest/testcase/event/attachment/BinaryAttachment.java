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

import org.aludratest.util.DataUtil;
import org.apache.commons.codec.binary.Base64;

public class BinaryAttachment extends Attachment {

    private byte[] fileData;
    private String fileExtension;

    public BinaryAttachment(String label, byte[] fileData, String fileExtension) {
        super(label);
        if (fileExtension == null) {
            throw new IllegalArgumentException("fileExtension is null");
        }
        this.fileData = (fileData != null ? fileData.clone() : null);
        this.fileExtension = fileExtension;
    }

    @Override
    public byte[] getFileData() {
        return fileData;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String getFileDataAsBase64String() {
        final Base64 base64 = new Base64();
        byte[] encodedData = base64.encode(fileData);
        return new String(encodedData, DataUtil.UTF_8);
    }

}