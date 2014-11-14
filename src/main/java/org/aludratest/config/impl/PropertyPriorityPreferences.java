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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.aludratest.AludraTest;
import org.aludratest.config.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preferences wrapper dealing with the complex AludraText configuration overwrite mechanisms.
 * 
 * @author falbrech
 * 
 */
public class PropertyPriorityPreferences extends AbstractPreferences {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyPriorityPreferences.class);

    private static final String SYSPROP_PREFIX = "ALUDRATEST_CONFIG/";

    /**
     * The pattern for a environment-global service system property (valid for all service instances, until overwritten by an
     * instance specific property for the same service type). The parameters are:
     * <ol>
     * <li>Component / Service ID</li>
     * <li>Property name (maybe including slashes)</li>
     * </ol>
     */
    private static final String SYSPROP_ENV_NAME = SYSPROP_PREFIX + "{0}/{1}";

    private static final String SYSPROP_ENV_INSTANCE_NAME = SYSPROP_PREFIX + "{0}/_{1}/{2}";

    /**
     * The patterns for property files to search for property to find. They are checked in the order of their appearance in this
     * array. The parameters passed to <code>MessageFormat.format()</code> are, for each array entry:
     * <ol>
     * <li>Environment name</li>
     * <li>Component / Service ID</li>
     * <li>Service instance name, or XYZ if no service instance</li>
     * </ol>
     */
    private static final String[] PROPFILE_NAMES = { "config/_{0}/{2}/{1}.properties", "config/_{0}/{1}.properties",
        "config/{2}/{1}.properties", "config/{1}.properties"
    };

    private String componentName;

    private String serviceInstanceName;

    private Preferences delegate;

    private String relativePath;


    public PropertyPriorityPreferences(String componentName, Preferences delegate) {
        this(componentName, null, delegate);
    }

    public PropertyPriorityPreferences(String componentName, String serviceInstanceName,
            Preferences delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate is null");
        }
        if (componentName == null) {
            throw new IllegalArgumentException("componentName is null");
        }
        this.componentName = componentName;
        this.serviceInstanceName = serviceInstanceName;
        this.delegate = delegate;
    }

    private PropertyPriorityPreferences(String componentName, String serviceInstanceName,
            String relativePath,
            Preferences delegate) {
        this(componentName, serviceInstanceName, delegate);
        this.relativePath = relativePath;
    }

    @Override
    public String[] getKeyNames() {
        // this is quite complex, because all potential files and even the system properties
        // have to be scanned for potential matches!
        List<String> keyNames = new ArrayList<String>();
        keyNames.addAll(Arrays.asList(delegate.getKeyNames()));

        // properties files on classpath
        keyNames.addAll(getPropertiesFilesSubNames(false));

        // system property matches
        keyNames.addAll(getSystemPropertiesSubNames(false));

        return keyNames.toArray(new String[0]);
    }

    @Override
    protected String internalGetStringValue(String key) {
        String fullPropName = (relativePath == null ? key : (relativePath + "/" + key));

        // top priority - system property
        String sysPropValue = checkSystemProperties(fullPropName);
        if (sysPropValue != null) {
            return sysPropValue;
        }

        // now for the .properties files on the classpath
        String propertiesFilesValue = checkPropertiesFiles(fullPropName);
        if (propertiesFilesValue != null) {
            return propertiesFilesValue;
        }

        // last fallback: The delegate, which should be default value
        return delegate.getStringValue(key);
    }

    private String checkSystemProperties(String fullPropName) {
        // check service instance name system property, if service instance is set
        if (serviceInstanceName != null) {
            String propName = MessageFormat.format(SYSPROP_ENV_INSTANCE_NAME, componentName, serviceInstanceName,
                    fullPropName);
            String value = System.getProperty(propName);
            if (value != null) {
                return value;
            }
        }
        // check component system property
        String propName = MessageFormat.format(SYSPROP_ENV_NAME, componentName, fullPropName);
        return System.getProperty(propName);
    }

    private List<Properties> loadPropertiesFiles() {
        List<Properties> result = new ArrayList<Properties>();

        String instanceName = serviceInstanceName == null ? "XYZ" : serviceInstanceName;

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = PropertyPriorityPreferences.class.getClassLoader();
        }

        for (String pnPattern : PROPFILE_NAMES) {
            String pfName = MessageFormat.format(pnPattern, AludraTest.getEnvironmentName(), componentName,
                    instanceName);
            URL url = cl.getResource(pfName);
            if (url != null) {
                InputStream in = null;
                try {
                    in = url.openStream();
                    Properties p = new Properties();
                    p.load(in);
                    result.add(p);
                }
                catch (IOException e) {
                    LOG.error("Could not load configuration file " + url, e);
                }
                finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    }
                    catch (Exception e) {
                        LOG.debug("Exception when closing input stream", e);
                    }
                }
            }
        }

        return result;
    }

    private String checkPropertiesFiles(String fullPropName) {
        for (Properties p : loadPropertiesFiles()) {
            if (p.containsKey(fullPropName)) {
                return p.getProperty(fullPropName);
            }
        }
        return null;
    }

    private List<String> getPropertiesFilesSubNames(boolean nodeNames) {
        List<String> result = new ArrayList<String>();

        for (Properties p : loadPropertiesFiles()) {
            addMatchingSubNames(p, nodeNames, result);
        }

        return result;
    }

    private void addMatchingSubNames(Properties p, boolean nodeNames, List<String> result) {
        for (String key : p.stringPropertyNames()) {
            if (relativePath == null || key.startsWith(relativePath + "/")) {
                String remainderKey = relativePath == null ? key : key.substring(relativePath.length() + 1);
                if (!remainderKey.contains("/") && !nodeNames) {
                    result.add(remainderKey);
                }
                else if (remainderKey.contains("/") && nodeNames) {
                    result.add(remainderKey.substring(0, remainderKey.indexOf('/')));
                }
            }
        }
    }

    private List<String> getSystemPropertiesSubNames(boolean nodeNames) {
        // build temporary property set with all nodes applying to this preferences

        Properties p = new Properties();
        for (String key : System.getProperties().stringPropertyNames()) {
            if (key.startsWith(SYSPROP_PREFIX)) {
                // remove prefix, check if applicable to us
                key = key.substring(SYSPROP_PREFIX.length());
                if (key.startsWith(componentName + "/")) {
                    key = key.substring(componentName.length() + 1);
                    // also instance to remove?
                    if (key.startsWith("_") && key.contains("/")) {
                        key = key.substring(key.indexOf('/') + 1);
                    }
                    p.setProperty(key, "1");
                }
            }
        }

        List<String> result = new ArrayList<String>();
        addMatchingSubNames(p, nodeNames, result);
        return result;
    }

    @Override
    public Preferences getChildNode(String name) {
        Preferences child = delegate.getChildNode(name);
        // could be a path only available via properties files etc, so assume an empty delegate
        if (child == null) {
            child = new SimplePreferences();
        }
        String path = relativePath == null ? name : relativePath + "/" + name;
        return new PropertyPriorityPreferences(componentName, serviceInstanceName, path, child);
    }

    @Override
    public String[] getChildNodeNames() {
        List<String> nodeNames = new ArrayList<String>();

        // this is quite complex, because all potential files and even the system properties
        // have to be scanned for potential matches!
        nodeNames.addAll(Arrays.asList(delegate.getChildNodeNames()));

        // properties files on classpath
        nodeNames.addAll(getPropertiesFilesSubNames(true));

        // system property matches
        nodeNames.addAll(getSystemPropertiesSubNames(true));

        return nodeNames.toArray(new String[0]);
    }

}
