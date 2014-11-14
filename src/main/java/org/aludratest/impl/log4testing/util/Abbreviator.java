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
package org.aludratest.impl.log4testing.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.databene.commons.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Abbreviator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Abbreviator.class);

    private static final String CONFIG_FILE_NAME = "abbreviation.properties";

    private static List<Abbreviation> abbreviations;

    static {
        load();
    }

    /** Private constructor of utility class preventing instantiation by other classes */
    private Abbreviator() {
    }

    public static String applyTo(String text) {
        String out = text;
        for (Abbreviation abbreviation : abbreviations) {
            out = abbreviation.applyTo(out);
        }
        return out;
    }

    private static void load() {
        BufferedReader reader = null;
        try {
            abbreviations = new ArrayList<Abbreviation>();
            InputStream in = Abbreviator.class.getResourceAsStream("/" + CONFIG_FILE_NAME);
            if (in != null) {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    int sep = line.indexOf('=');
                    if (sep > 0) {
                        String key = line.substring(0, sep);
                        String value = line.substring(sep + 1);
                        abbreviations.add(new Abbreviation(key, value));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occured reading " + CONFIG_FILE_NAME + ". ", e);
        } finally {
            IOUtil.close(reader);
        }
    }

    static class Abbreviation {
        private String original;
        private String replacement;

        private Abbreviation(String original, String replacement) {
            this.original = original;
            this.replacement = replacement;
        }

        public String applyTo(String text) {
            return text.replace(original, replacement);
        }
    }

}
