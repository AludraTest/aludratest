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
package org.aludratest.impl.log4testing.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.aludratest.impl.log4testing.observer.TestObserver;
import org.aludratest.impl.log4testing.observer.TestObserverManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class that reads the log4testing configuration file.
 * @author Marcel Malitz
 * @author Volker Bergmann
 */
public class Log4TestingConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log4TestingConfiguration.class);

    private static final String LOG4_TESTING_CONFIG_FILE = "log4testing.xml";

    private static final String XPATH_SYSPROPS = "/log4testing/sysprops/*";

    private static final String XPATH_REPLACE_REGEX = "regex";
    private static final String XPATH_REPLACE_REPLACEMENT = "replacement";
    private static final String XPATH_REPLACE = "/log4testing/attachment//replace";

    // singleton instance ------------------------------------------------------

    private static Log4TestingConfiguration instance = null;

    // instance attributes -----------------------------------------------------

    private Document xmlConfig;

    // default constructor -----------------------------------------------------

    /** Reads the configuration file and parses its properties */
    private Log4TestingConfiguration() {
        try {
            URL configLocation;
            File configfile = new File(LOG4_TESTING_CONFIG_FILE);
            if (!configfile.exists()) {
                configLocation = Thread.currentThread().getContextClassLoader().getResource(LOG4_TESTING_CONFIG_FILE);
                if (configLocation == null) {
                    configLocation = Log4TestingConfiguration.class.getClassLoader().getResource(LOG4_TESTING_CONFIG_FILE);
                }
            } else {
                try {
                    configLocation = configfile.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new ConfigurationError("Error parsing config location. ", e);
                }
            }
            if (configLocation == null) {
                throw new ConfigurationError("No " + LOG4_TESTING_CONFIG_FILE
                        + " found on classpath. Please add a Log4Testing config file.");
            }
            final SAXReader reader = new SAXReader();
            this.xmlConfig = reader.read(configLocation);
        } catch (DocumentException e) {
            throw new ConfigurationError("Error loading " + LOG4_TESTING_CONFIG_FILE, e);
        }
    }

    /** @return the singleton instance */
    public static synchronized Log4TestingConfiguration getInstance() {
        if (instance == null) {
            instance = new Log4TestingConfiguration();
            instance.init();
        }
        return instance;
    }


    // interface ---------------------------------------------------------------

    public String replace(String output) {
        @SuppressWarnings("unchecked")
        List<Node> replaceNodes = xmlConfig.selectNodes(XPATH_REPLACE);
        String result = output;
        for (Node replaceNode : replaceNodes) {
            String regex = replaceNode.selectSingleNode(XPATH_REPLACE_REGEX).getStringValue();
            String replacement = replaceNode.selectSingleNode(XPATH_REPLACE_REPLACEMENT).getStringValue();
            result = result.replaceAll(regex, replacement);
        }
        return result;
    }


    // private helpers ---------------------------------------------------------

    private static void parseSystemProperties(Document config) {
        final List<?> sysProps = config.selectNodes(XPATH_SYSPROPS);
        for (Object sysProp : sysProps) {
            Element element = (Element) sysProp;
            System.setProperty(element.getName(), element.getText());
        }
    }

    private void init() {
        parseSystemProperties(xmlConfig);
        parseObservers();
    }

    /** Creates the observers defined in the configuration file
     *  and registers them with the {@link TestObserverManager}. */
    private void parseObservers() {
        @SuppressWarnings("unchecked")
        List<Node> observerNodes = xmlConfig.selectNodes("log4testing/observers/observer");
        if (observerNodes.size() == 0) {
            throw new ConfigurationError("No observers defined");
        }
        for (Node node : observerNodes) {
            try {
                TestObserver observer = ReflectionUtil.createBean((Element) node, TestObserver.class);
                TestObserverManager.getInstance().addObserver(observer);
            } catch (ConfigurationError e) {
                LOGGER.error("Error initailizing observer", e);
            }
        }
    }

}
