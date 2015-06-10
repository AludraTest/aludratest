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
package org.aludratest.impl.log4testing.output.writer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.TestObject;
import org.aludratest.impl.log4testing.data.TestStepContainer;
import org.aludratest.impl.log4testing.data.TestSuiteLogComponent;
import org.aludratest.impl.log4testing.output.util.OutputUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.databene.commons.IOUtil;
import org.databene.formats.html.util.HTMLUtil;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes log files based on a Velocity template.
 * @author Marcel Malitz
 * @author Volker Bergmann
 * @param <T> The type of test step container to write
 */
public abstract class VelocityWriter<T extends TestStepContainer> extends FileWriter<T> {
    
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static volatile VelocityEngine engine;

    // instance attributes -----------------------------------------------------

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String template;

    private String variable;


    // method interface --------------------------------------------------------

    /** @param template the template to set */
    public void setTemplate(String template) {
        this.template = template;
    }

    /** @return the {@link #template} */
    public String getTemplate() {
        return template;
    }

    /** @param variable the variable to set */
    public void setVariable(String variable) {
        this.variable = variable;
    }

    /** @return the variable */
    public String getVariable() {
        return variable;
    }

    @Override
    public void validate() {
        // check template property
        assertNotEmpty("template", template);

        // check variable property
        assertNotEmpty("variable", variable);

        // validate properties of parent class
        super.validate();
    }

    @Override
    public File write(T testStepContainer, String path) {
        try {
            String content = format(testStepContainer);
            File file = new File(path);
            IOUtil.writeBytes(content.getBytes(UTF_8), file);
            return file;
        } catch (IOException e) {
            throw new TechnicalException("Error writing file " + path, e);
        }
    }


    // private methods ---------------------------------------------------------

    private String format(TestObject testObject) {
        StringWriter buffer = new StringWriter();
        String templateFileName = getTemplate();
        try {
            VelocityEngine ve = getEngine();
            Template templateInstance;
            // synchronized to avoid concurrency errors when engine caches
            // templates
            synchronized (ve) {
                templateInstance = ve.getTemplate(templateFileName);
            }
            templateInstance.merge(createContext(testObject), buffer);
        } catch (Exception e) {
            logger.error("Problem while merging an test object with the template " + templateFileName + ".", e);
        }
        return buffer.toString();
    }

    private VelocityContext createContext(TestObject testObject) {
        VelocityContext context = new VelocityContext();
        context.put(getVariable(), testObject);
        context.put("name", new NameFormat());
        context.put("time", new TimeFormat());
        context.put("html", new HtmlFormat());
        return context;
    }

    private static VelocityEngine getEngine() {
        if (engine == null) {
            synchronized (VelocityWriter.class) {
                if (engine == null) {
                    engine = new VelocityEngine();
                    engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
                            org.apache.velocity.slf4j.Slf4jLogChute.class.getName());
                    engine.setProperty("file.resource.loader.class", ClasspathResourceLoader.class.getName());
                    engine.setProperty("eventhandler.include.class",
                            "org.apache.velocity.app.event.implement.IncludeRelativePath");
                    engine.init();
                }
            }
        }
        return engine;
    }


    // Helper classes ----------------------------------------------------------

    /**
     * Formats names
     * @author Volker Bergmann
     */
    public class NameFormat {
        /** @param test the test object to format
         *  @return the display name of a test object */
        public String format(TestSuiteLogComponent test) {
            return OutputUtil.displayName(test.getName(), getIgnoreableRoot(), isAbbreviating());
        }
    }

    /**
     * Formats Periods.
     * @author Volker Bergmann
     */
    public class TimeFormat {
        private static final int HOURS_PER_DAY = 24;

        private DecimalFormat nf2 = new DecimalFormat("00");
        private DecimalFormat nf3 = new DecimalFormat("000");

        /** @return the format information for table headers */
        public String getHeader() {
            return (isShortTimeFormat() ? "[hh:mm:ss'ms]" : "");
        }

        /** @param period the Period to format
         * @return the text representation of the period */
        public String format(Period period) {
            if (isShortTimeFormat()) {
                return nf2.format(period.getDays() * HOURS_PER_DAY + period.getHours()) + ":" + nf2.format(period.getMinutes()) + ":" + nf2.format(period.getSeconds()) + "'" + nf3.format(period.getMillis());
            } else {
                return period.getDays() + " days " + period.getHours() + " hours " + period.getMinutes() + " minutes " + period.getSeconds() + " seconds " + period.getMillis() + " ms";
            }
        }
    }

    /** Formats texts in HTML.
     * @author Volker Bergmann */
    public class HtmlFormat {

        /** @param text the text to format in HTML
         * @return an HTML representation of the text */
        public String format(String text) {
            return HTMLUtil.escape(text).replace("\n", "<br />");
        }
    }

}
