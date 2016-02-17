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
import java.io.InputStream;

import org.aludratest.log4testing.AbstractAttachmentLog;
import org.aludratest.testcase.event.attachment.Attachment;

/** Memory-based implementation of the AttachmentLog interface. This is somewhat faster than the file-based implementation, but
 * increases overall memory usage of the test run. This implementation is used when
 * <code>AludraTestConfig.isAttachmentsFileBuffer()</code> returns <code>false</code>.
 * 
 * @author falbrech */
public final class MemoryAttachmentLog extends AbstractAttachmentLog {

    private byte[] data;

    /** Constructs a new memory-based attachment log for the given attachment. The data array of the attachment is not copied; only
     * its reference is stored.
     * 
     * @param attachment Attachment to create an attachment log for. */
    public MemoryAttachmentLog(Attachment attachment) {
        super(attachment.getLabel(), attachment.getFileExtension());
        this.data = attachment.getFileData();
    }

    @Override
    public InputStream getFileContents() {
        return new ByteArrayInputStream(data);
    }

}
