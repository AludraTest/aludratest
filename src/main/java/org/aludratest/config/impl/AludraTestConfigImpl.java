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
package org.aludratest.config.impl;

import java.util.Locale;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.Configurable;
import org.aludratest.config.ConfigurationException;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.scheduler.sort.Alphabetic;
import org.aludratest.service.Implementation;
import org.databene.commons.StringUtil;
import org.databene.commons.version.VersionInfo;

/** Default implementation of the AludraTestConfig interface. Provides an accessor to the instance, if any, but this should only be
 * used by AludraTest internal components.
 * 
 * @author Volker Bergmann
 * @author falbrech */
@Implementation({ AludraTestConfig.class })
public class AludraTestConfigImpl implements AludraTestConfig, Configurable {

    private static final String DEFAULT_XLS_ROOT = "xls/javatest";

    private static final boolean CONFIG_TAB_REQUIRED_DEFAULT = false;

    private static final boolean IGNORE_ENABLED_DEFAULT = false;

    /** Configuration property of implementation (undocumented) */
    private static final String XLS_FORMATTED_BY_DEFAULT_PROP = "xls.formatted.by.default";

    // attributes --------------------------------------------------------------

    private boolean stopTestCaseOnInteractionException;

    private boolean stopTestCaseOnVerificationException;

    /** property for testing */
    private boolean stopTestCaseOnOtherException;

    private String xlsRootPath;

    private boolean xlsFormattedByDefault;

    private double numericTolerance;

    /** properties for aludra clients */
    private int numberOfThreads;
    private boolean configTabRequired;
    private boolean ignoreEnabled;
    private String version;

    private boolean debugAttachmentsOnFrameworkException;

    private String sorterName;


    // constructor -------------------------------------------------------------

    /** Creates a new configuration implementation object. */
    public AludraTestConfigImpl() {
        readAludraTestVersion();
    }

    // implementation ----------------------------------------------------------

    @Override
    public String getPropertiesBaseName() {
        return "aludratest";
    }

    @Override
    public void configure(Preferences configuration) {
        readGeneralConfig(configuration);
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isStopTestCaseOnInteractionException() {
        return stopTestCaseOnInteractionException;
    }

    @Override
    public boolean isStopTestCaseOnVerificationException() {
        return stopTestCaseOnVerificationException;
    }

    @Override
    public boolean isStopTestCaseOnOtherException() {
        return stopTestCaseOnOtherException;
    }

    @Override
    public boolean isDebugAttachmentsOnFrameworkException() {
        return debugAttachmentsOnFrameworkException;
    }

    @Override
    public String getXlsRootPath() {
        return xlsRootPath;
    }

    @Override
    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    /** @return {@link #xlsFormattedByDefault} */
    public boolean isXlsFormattedByDefault() {
        return xlsFormattedByDefault;
    }

    @Override
    public boolean isConfigTabRequired() {
        return configTabRequired;
    }

    @Override
    public boolean isIgnoreEnabled() {
        return ignoreEnabled;
    }

    @Override
    public double getNumericTolerance() {
        return numericTolerance;
    }

    @Override
    public String getRunnerTreeSorterName() {
        return sorterName;
    }

    // private helper methods --------------------------------------------------

    private void readAludraTestVersion() {
        this.version = VersionInfo.getInfo("org.aludratest.aludratest", false).getVersion();
    }

    private void readGeneralConfig(Preferences config) {
        // exception configuration
        this.stopTestCaseOnInteractionException = config.getBooleanValue(STOP_ON_INTERACTION_PROP, true);
        this.stopTestCaseOnVerificationException = config.getBooleanValue(STOP_ON_VERIFICATION_PROP, true);
        this.stopTestCaseOnOtherException = config.getBooleanValue("stop.testcase.on.other.exception.TEST", false);

        // xls root path
        this.xlsRootPath = config.getStringValue(XLS_ROOT_PROP);
        if (StringUtil.isEmpty(xlsRootPath)) {
            this.xlsRootPath = DEFAULT_XLS_ROOT;
        }

        // number of threads
        this.numberOfThreads = config.getIntValue(NUMBER_OF_THREADS_PROP);
        if (this.numberOfThreads == 0) {
            throw new ConfigurationException("Property '" + NUMBER_OF_THREADS_PROP + "' has not been set to an integer!");
        }

        // xls formatted by default?
        this.xlsFormattedByDefault = config.getBooleanValue(XLS_FORMATTED_BY_DEFAULT_PROP, true);

        // default locale
        String defaultLocaleSpec = config.getStringValue(DEFAULT_LOCALE_PROP);
        if (!StringUtil.isEmpty(defaultLocaleSpec)) {
            Locale.setDefault(new Locale(defaultLocaleSpec));
        }

        // config tab required?
        this.configTabRequired = config.getBooleanValue(CONFIG_TAB_REQUIRED_PROP, CONFIG_TAB_REQUIRED_DEFAULT);

        // ignore enabled?
        this.ignoreEnabled = config.getBooleanValue(IGNORE_ENABLED_PROP, IGNORE_ENABLED_DEFAULT);

        this.numericTolerance = config.getDoubleValue(NUMERIC_TOLERANCE_PROP, 0.000001);

        this.debugAttachmentsOnFrameworkException = config.getBooleanValue(DEBUG_ON_FRAMEWORK_EXCEPTION_PROP, false);

        this.sorterName = config.getStringValue(RUNNER_TREE_SORTER_PROP, Alphabetic.class.getSimpleName());
    }

}
