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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.aludratest.log4testing.AbstractAttachmentLog;
import org.aludratest.testcase.event.attachment.Attachment;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementation of the AttachmentLog interface which uses temporary files to store attachment contents. This reduces overall
 * memory usage of the test run. This implementation is used when <code>AludraTestConfig.isAttachmentsFileBuffer()</code> returns
 * <code>true</code>.
 * 
 * @author falbrech */
public class LocalFileAttachmentLog extends AbstractAttachmentLog {

    private Logger LOG = LoggerFactory.getLogger(LocalFileAttachmentLog.class);

    private File localFile;

    /** Constructs a new file-based attachment log for the given attachment. The contents of the attachment are immediately written
     * to a temporary file, which is automatically deleted on VM exit.
     * 
     * @param attachment Attachment to create an attachment log for.
     * 
     * @throws IOException If the attachment could not be written to a temporary file (e.g. disk full). */
    public LocalFileAttachmentLog(Attachment attachment) throws IOException {
        super(attachment.getLabel(), attachment.getFileExtension());
        localFile = File.createTempFile("aludraTestAttachment", attachment.getFileExtension());
        FileUtils.writeByteArrayToFile(localFile, attachment.getFileData());
        localFile.deleteOnExit();
    }

    @Override
    public InputStream getFileContents() {
        try {
            return new FileInputStream(localFile);
        }
        catch (FileNotFoundException e) {
            // file must have been deleted in the meantime
            LOG.error("The temporary file used by attachment " + getLabel() + " has been deleted from outside AludraTest", e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

}
