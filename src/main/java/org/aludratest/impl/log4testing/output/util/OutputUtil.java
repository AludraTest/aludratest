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
package org.aludratest.impl.log4testing.output.util;

import java.io.File;
import java.util.regex.Matcher;

import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.util.Abbreviator;
import org.apache.commons.io.FilenameUtils;
import org.databene.commons.FileUtil;
import org.databene.commons.StringUtil;

/**
 * Provides utility methods for test log output.
 * @author Volker Bergmann
 */
public class OutputUtil {
    
    /** Private constructor of utility class preventing instantiation by other classes */
    private OutputUtil() {
    }

    /**
     * Creates an empty file for the provided test situation
     * @param testName
     * @param suffix
     * @param ignoreableRoot
     * @param abbreviating
     * @param outputDir
     * @return a {@link File} instance that points to the created file
     */
    public static File outputFile(String testName, String suffix, 
            String ignoreableRoot, boolean abbreviating, String outputDir) {
        String targetPath = OutputUtil.targetDirPath(testName, 
                ignoreableRoot, abbreviating, 
                new File(outputDir));
        targetPath += "." + suffix;
        File targetFile = new File(targetPath);
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (Exception e) {
                throw new TechnicalException("Error creating file " + targetFile, e);
            }
        }
        return targetFile;
    }

    /**
     * Determines the proper target directory path for the provided test situation.
     * @param testName
     * @param ignoreableRoot
     * @param abbreviating
     * @param baseDir
     * @return the path of the related target directory
     */
    public static String targetDirPath(String testName, String ignoreableRoot, boolean abbreviating, File baseDir) {
        String result = OutputUtil.displayName(testName, ignoreableRoot, abbreviating);
        String packagePath = result.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        return FilenameUtils.concat(baseDir.getAbsolutePath(), packagePath);
    }

    /**
     * Formats the name of a test, abbreviating the name if desired.
     * @param testName the original name of the test
     * @param ignorableRoot an optional root package name which can be cut off
     * @param abbreviating flag that indicates whether to apply abbreviations
     * @return formatted name of the test
     */
    public static String displayName(String testName, String ignorableRoot, boolean abbreviating) {
        String displayName = testName;
        if (!StringUtil.isEmpty(ignorableRoot)) {
            if (!ignorableRoot.endsWith(".")) {
                ignorableRoot = ignorableRoot + '.';
            }
            if (displayName.startsWith(ignorableRoot)) {
                displayName = displayName.substring(ignorableRoot.length());
            }
        }
        if (abbreviating) {
            displayName = Abbreviator.applyTo(displayName);
        }
        return displayName;
    }

    /**
     * @param outputFile
     * @param baseDir
     * @return the relative path from the given root directory to the given file
     */
    public static String pathFromBaseDir(File outputFile, File baseDir) {
        return FileUtil.relativePath(baseDir, outputFile, '/');
    }

    /**
     * @param outputFile
     * @param baseDir
     * @return the relative path form the given file to the given root directory
     */
    public static String pathToBaseDir(File outputFile, File baseDir) {
        return FileUtil.relativePath(outputFile, baseDir, '/');
    }

}
