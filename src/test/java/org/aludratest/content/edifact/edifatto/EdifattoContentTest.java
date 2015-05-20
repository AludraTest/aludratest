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
package org.aludratest.content.edifact.edifatto;

import java.io.IOException;
import java.io.InputStream;

import org.aludratest.content.edifact.edifatto.EdifattoContent;
import org.databene.commons.IOUtil;
import org.databene.edifatto.ComparisonSettings;
import org.databene.edifatto.model.Interchange;
import org.databene.edifatto.util.NameBasedXMLComparisonModel;
import org.junit.Test;

/** 
 * Tests the {@link EdifattoContent}
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class EdifattoContentTest {

    @Test
    public void test() throws IOException {
        EdifattoContent handler = new EdifattoContent();
        InputStream in1 = IOUtil.getInputStreamForURI("ediTest/IFTDGN_1.edi");
        Interchange interchange1 = handler.readInterchange(in1);
        InputStream in2 = IOUtil.getInputStreamForURI("ediTest/IFTDGN_2.edi");
        Interchange interchange2 = handler.readInterchange(in2);
        handler.diff(interchange1, interchange2, new ComparisonSettings(), new NameBasedXMLComparisonModel());
    }

}
