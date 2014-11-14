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
package org.aludratest.app.excelwizard;

import java.io.File;

import org.aludratest.AludraTestTest;
import org.aludratest.app.excelwizard.CLIExcelCreator;
import org.aludratest.data.configtests.ConfigTestWithProperConfig;
import org.databene.commons.FileUtil;
import org.junit.Assert;
import org.junit.Test;

public class CLIExcelCreatorTest {

    private final static String PROP_KEY = "ALUDRATEST_CONFIG/aludratest/javatest.xls.root";

    @Test
    public void testCLIExcelCreator() throws Exception {
        // inject testing excel path
        File f = new File("./target/testfiles/xlstest");
        // delete if existing
        FileUtil.deleteDirectoryIfExists(f);
        f.mkdirs();

        String oldProp = System.getProperty(PROP_KEY);
        System.setProperty(PROP_KEY, "./target/testfiles/xlstest");

        try {
            AludraTestTest.setInstance(null);
            CLIExcelCreator.main(new String[] { ConfigTestWithProperConfig.class.getName() });
            Assert.assertEquals(1, f.getAbsoluteFile().listFiles().length);
        }
        finally {
            if (oldProp == null) {
                System.getProperties().remove(PROP_KEY);
                Assert.assertNull(System.getProperty(PROP_KEY));
            }
            else {
                System.setProperty(PROP_KEY, oldProp);
            }
            AludraTestTest.setInstance(null);
        }
    }

}
