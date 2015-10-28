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
package org.aludratest.testcase.event;

import java.lang.annotation.Annotation;

import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.attachment.Attachment;
import org.joda.time.DateTime;

/** TODO FAL javadoc
 * @author falbrech */
public interface TestStepInfo {

    int getId();

    DateTime getStartingTime();

    DateTime getFinishingTime();

    TestStatus getTestStatus();

    ComponentId<? extends AludraService> getServiceId();

    String getCommand();

    /** Returns the result of the corresponding method call.
     * @return the result of the corresponding method call */
    String getResult();

    Throwable getError();

    public String getErrorMessage();

    public Iterable<Attachment> getAttachments();

    /** Returns the arguments for this test step which were marked with the given Annotation. Use <code>null</code> as parameter to
     * retrieve the arguments for this test step which were <b>not</b> marked with any Annotation.
     * 
     * @param annotationType Annotation the parameters were marked with. The annotation itself must be marked with the
     *            <code>TestStepArgumentMarker</code> annotation.
     * @return The arguments for this test step which were marked with the given Annotation, possibly an empty array, but never
     *         <code>null</code>. */
    public Object[] getArguments(Class<? extends Annotation> annotationType);

}
