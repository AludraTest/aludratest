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
package org.aludratest.testcase.event.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.aludratest.exception.AutomationException;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.data.ParamConverter;
import org.aludratest.testcase.event.TestStepArgumentMarker;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.attachment.Attachment;
import org.joda.time.DateTime;

public final class TestStepInfoBean implements TestStepInfo {

    private static AtomicInteger nextId = new AtomicInteger();

    /** Cache of annotation classes tested for marking test arguments. These won't change at run-time, so caching is safe. */
    private static Map<Class<? extends Annotation>, Boolean> testArgumentMarkers = new HashMap<Class<? extends Annotation>, Boolean>();

    private int id;

    private ComponentId<? extends AludraService> serviceId;

    private String command;

    private TestStatus testStatus;

    private DateTime startingTime;

    private DateTime finishingTime;

    private String result;

    private String errorMessage;

    private Throwable error;

    // private List<ErrorReport> errorReports;

    private List<Attachment> attachments;

    private Map<Class<? extends Annotation>, Object[]> arguments;

    public TestStepInfoBean() {
        this.id = nextId.incrementAndGet();
        this.startingTime = DateTime.now();
        this.testStatus = TestStatus.PASSED;
    }

    /** Copies the following information from the given test step into this object:
     * <ul>
     * <li>Command</li>
     * <li>Service ID</li>
     * <li>Arguments</li>
     * </ul>
     * 
     * @param otherTestStep Other test step object (e.g. previous test step) to copy information from.
     * @param includeAttachments If to include attachments in copy. */
    public void copyBaseInfoFrom(TestStepInfoBean otherTestStep) {
        this.serviceId = otherTestStep.serviceId;
        this.command = otherTestStep.command;
        if (otherTestStep.arguments != null) {
            this.arguments = new HashMap<Class<? extends Annotation>, Object[]>(otherTestStep.arguments);
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public DateTime getStartingTime() {
        return startingTime;
    }

    @Override
    public DateTime getFinishingTime() {
        return finishingTime;
    }

    public void setFinishingTime(DateTime finishingTime) {
        this.finishingTime = finishingTime;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public TestStatus getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(TestStatus testStatus) {
        this.testStatus = testStatus;
    }

    @Override
    public ComponentId<? extends AludraService> getServiceId() {
        return serviceId;
    }

    public void setServiceId(ComponentId<? extends AludraService> serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Iterable<Attachment> getAttachments() {
        if (attachments == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(attachments);
    }

    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<Attachment>();
        }
        attachments.add(attachment);
    }

    // @Override
    // public Iterable<ErrorReport> getSystemErrorReports() {
    // return errorReports == null ? Collections.<ErrorReport> emptyList() : Collections.unmodifiableList(errorReports);
    // }
    //
    // public void addSystemErrorReport(ErrorReport report) {
    // if (errorReports == null) {
    // errorReports = new ArrayList<ErrorReport>();
    // }
    // errorReports.add(report);
    // }

    @Override
    public Object[] getArguments(Class<? extends Annotation> annotationType) {
        if (arguments == null) {
            return new Object[0];
        }

        Object[] result = arguments.get(annotationType);
        return result == null ? new Object[0] : result;
    }

    public void setArguments(Class<? extends Annotation> annotationType, Object[] arguments) {
        if (this.arguments == null) {
            this.arguments = new HashMap<Class<? extends Annotation>, Object[]>();
        }
        this.arguments.put(annotationType, arguments);
    }

    public void setCommandNameAndArguments(Method method, Object[] parameters) {
        setCommand(method.getName());

        if (parameters == null || parameters.length == 0) {
            return;
        }

        Map<Class<? extends Annotation>, List<Object>> fillMap = new HashMap<Class<? extends Annotation>, List<Object>>();

        // check for the presence of a Logging annotation and modify values accordingly
        Annotation[][] paramsAnnos = method.getParameterAnnotations();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] paramAnnos = paramsAnnos[Math.min(i, paramsAnnos.length - 1)];
            boolean consumedArg = false;
            // find converter, if present
            Format paramFormat = findParamConverter(paramAnnos);

            for (Annotation paramAnno : paramAnnos) {
                if (isTestArgumentMarkerAnnotation(paramAnno.annotationType())) {
                    List<Object> ls = fillMap.get(paramAnno.annotationType());
                    if (ls == null) {
                        ls = new ArrayList<Object>();
                        fillMap.put(paramAnno.annotationType(), ls);
                    }
                    ls.add(paramFormat == null ? parameters[i] : paramFormat.format(parameters[i]));
                    consumedArg = true;
                }
            }
            if (!consumedArg) {
                List<Object> ls = fillMap.get(null);
                if (ls == null) {
                    ls = new ArrayList<Object>();
                    fillMap.put(null, ls);
                }
                ls.add(paramFormat == null ? parameters[i] : paramFormat.format(parameters[i]));
            }
        }

        // "copy" fill map to arguments
        for (Map.Entry<Class<? extends Annotation>, List<Object>> entry : fillMap.entrySet()) {
            setArguments(entry.getKey(), entry.getValue().toArray(new Object[0]));
        }
    }

    private Format findParamConverter(Annotation[] paramAnnos) {
        for (Annotation a : paramAnnos) {
            if (a.annotationType() == ParamConverter.class) {
                try {
                    return ((ParamConverter) a).value().newInstance();
                }
                catch (Exception e) {
                    throw new AutomationException("Could not instantiate parameter converter class ", e);
                }
            }
        }

        return null;
    }

    private static boolean isTestArgumentMarkerAnnotation(Class<? extends Annotation> annoClass) {
        if (testArgumentMarkers.containsKey(annoClass)) {
            return testArgumentMarkers.get(annoClass).booleanValue();
        }

        boolean result = annoClass.isAnnotationPresent(TestStepArgumentMarker.class);
        testArgumentMarkers.put(annoClass, Boolean.valueOf(result));
        return result;
    }

    @Override
    public String toString() {
        return "TestStepInfoBean [id=" + id + ", serviceId=" + serviceId + ", command=" + command + ", testStatus=" + testStatus
                + ", startingTime=" + startingTime + ", finishingTime=" + finishingTime + ", errorMessage=" + errorMessage
                + ", error=" + error + "]";
    }

}
