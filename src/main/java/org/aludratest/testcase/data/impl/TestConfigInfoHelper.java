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
package org.aludratest.testcase.data.impl;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.exception.AutomationException;
import org.aludratest.testcase.data.Source;
import org.databene.commons.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Provides information from an the testConfiguration tab of a test data Excel document. By default, test names are created from
 * method name plus an incremental number (one test and number for each data set). A {@link TestConfigInfoHelper} can be used to
 * provide verbal test information instead of the number. This can be done by adding a tab named 'config' to the Excel document
 * and enter the text description for data set {@literal #}n in row {@literal #}n if row based, otherwise in column {@literal #}n.
 * The column (or row) bearing the description must have the header 'testConfiguration'.
 * @author Volker Bergmann
 * @author Yibo Wang */
public class TestConfigInfoHelper {

    /** The class' slf4j logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestConfigInfoHelper.class);

    private AludraTestConfig aludraConfig;

    public TestConfigInfoHelper(AludraTestConfig aludraConfig) {
        this.aludraConfig = aludraConfig;
    }

    // interface ---------------------------------------------------------------

    /** Reads the test configuration information from the corresponding tab of the test data file.
     * @param method the method for which to determine the test infos
     * @param invocations the expected number of invocations
     * @return a List of the test infos for the method */
    public List<TestDataLoadInfo> testInfos(Method method, int invocations) {
        // check if the Excel document is available
        String excelFilePath = testConfigurationFilePath(method);
        return ExcelConfigUtil.parseConfigSheet(excelFilePath, method, invocations, aludraConfig.isConfigTabRequired(),
                aludraConfig.isIgnoreEnabled());
    }

    // private helper methods ------------------------------------------------

    /** Returns the file path of the test configuration file for a given method. */
    private String testConfigurationFilePath(Method method) {
        String localFileName = getTestConfigFileName(method);
        if (localFileName == null) {
            return null;
        }
        String absoluteFilePath = SystemInfo.getCurrentDir() + File.separator
                + aludraConfig.getXlsRootPath();
        absoluteFilePath = absoluteFilePath.replace('/', File.separatorChar);
        String testClassName = method.getDeclaringClass().getName();
        absoluteFilePath = absoluteFilePath + File.separatorChar + testClassName.replace('.', File.separatorChar); // NOSONAR
        return absoluteFilePath + File.separator + localFileName;
    }

    /** Returns the name of the test configuration file for a given method. */
    private String getTestConfigFileName(Method method) {
        Source source = firstSourceAnnotation(method);
        if (source != null) {
            String uri = source.uri();
            if (uri == null || uri.isEmpty()) {
                throw new AutomationException("No source URL defined in @Source");
            }
            return uri;
        }
        else {
            return null;
        }
    }

    /** Scans a method and its parameters for @Source annotations and returns the first one found. */
    private static Source firstSourceAnnotation(Method method) {
        Source source = method.getAnnotation(Source.class);
        if (source != null) {
            return source;
        }
        Annotation[][] paramAnnos = method.getParameterAnnotations();
        for (int iP = 0; iP < paramAnnos.length; iP++) {
            for (int iA = 0; iA < paramAnnos[iP].length; iA++) {
                if (paramAnnos[iP][iA] instanceof Source) {
                    return (Source) paramAnnos[iP][iA];
                }
            }
        }
        return null;
    }

}
