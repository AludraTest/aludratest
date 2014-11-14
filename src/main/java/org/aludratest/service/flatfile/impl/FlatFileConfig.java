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
package org.aludratest.service.flatfile.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.aludratest.config.Preferences;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.flatfile.FlatFileService;
import org.databene.commons.StringUtil;
import org.databene.formats.fixedwidth.FixedWidthRowTypeDescriptor;
import org.databene.formats.fixedwidth.FixedWidthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the configuration for a {@link FlatFileService} instance.
 * @author Volker Bergmann
 */
public final class FlatFileConfig {

    /** The {@link Locale} to apply if none has been configured explicitly. */
    private static final Locale DEFAULT_LOCALE = Locale.US;

    /** Logger of the {@link FlatFileConfig}. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FlatFileConfig.class);

    /** Creates a new FlatFileConfig instance which uses the given configuration object.
     * 
     * @param configuration The configuration object containing required configuration. */
    public FlatFileConfig(Preferences configuration) {

        // parse locale
        String localeName = configuration.getStringValue("locale");
        if (StringUtil.isEmpty(localeName)) {
            this.locale = DEFAULT_LOCALE;
            LOGGER.debug("'locale' not set in configuration, defaulting to " + this.locale);
        }
        else {
            this.locale = new Locale(localeName);
        }

        // parse formats
        try {
            this.rowFormats = new ArrayList<FixedWidthRowTypeDescriptor>();
            for (String key : configuration.getKeyNames()) {
                if (key.startsWith("beanformats.")) {
                    String spec = configuration.getStringValue(key);
                    String rowType = StringUtil.splitOnFirstSeparator(key, '.')[1];
                    FixedWidthRowTypeDescriptor rowDescriptor = FixedWidthUtil.parseBeanColumnsSpec(spec, rowType, "", locale);
                    rowFormats.add(rowDescriptor);
                }
                else if (key.startsWith("arrayformats.")) {
                    String spec = configuration.getStringValue(key);
                    String rowType = StringUtil.splitOnFirstSeparator(key, '.')[1];
                    FixedWidthRowTypeDescriptor descriptor = FixedWidthUtil.parseArrayColumnsSpec(spec, rowType, "", locale);
                    rowFormats.add(descriptor);
                }
            }
        }
        catch (ParseException pe) {
            throw new AutomationException("Invalid flatfile configuration", pe);
        }
    }

    /** The locale to use in formatting, defaults to 'en_US' */
    private Locale locale;

    /** Maps each row type/bean class to an array of column format descriptors. */
    private List<FixedWidthRowTypeDescriptor> rowFormats;

    // interface ---------------------------------------------------------------

    /** Returns the {@link #locale}
     *  @return the locale to use */
    public Locale getLocale() {
        return locale;
    }

    /** returns the {@link #rowFormats}.
     *  @return the row formats */
    public List<FixedWidthRowTypeDescriptor> getRowFormats() {
        return rowFormats;
    }

}
