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
package org.aludratest.app;

import java.io.File;

import org.databene.commons.BeanUtil;

final class AppUtil {

    private AppUtil() {
    }

    public static Class<?> classForPotentialResourceName(String classOrResourceName) {
        String resourceName = classOrResourceName.trim();

        String className;
        if (resourceName.endsWith(".java")) {
            className = deriveClassName(resourceName, "java");
            className = className.substring(0, className.length() - ".java".length());
        }
        else {
            className = resourceName;
        }
        return BeanUtil.forName(className);
    }

    /** Derives a file name from a resource name and source directory. */
    private static String deriveClassName(String resourceName, String sourceDir) {
        String dirMarker = "." + sourceDir + ".";
        String baseName = resourceName.replace(File.separatorChar, '.');
        int i = baseName.indexOf(".src.");
        if (i < 0) {
            throw new UnsupportedOperationException("Cannot handle this as class name: " + resourceName);
        }
        i = baseName.indexOf(dirMarker, i + ".src.".length());
        if (i < 0) {
            throw new UnsupportedOperationException("Cannot handle this as class name: " + resourceName);
        }
        i += dirMarker.length();
        return baseName.substring(i);
    }

}
