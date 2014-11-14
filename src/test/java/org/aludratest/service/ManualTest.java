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
package org.aludratest.service;

import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.PerformanceFailure;
import org.aludratest.service.pseudo.PseudoService;
import org.aludratest.testcase.AludraTestCase;
import org.aludratest.testcase.Test;

/**
 * AludraTest for manual verification of the framework behaviour.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public abstract class ManualTest extends AludraTestCase {

    @Test
    public void testFunctionalFailure() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        newTestStepGroup("Group 1");
        service.perform().throwException(new FunctionalFailure("Test Ex"));
    }

    @Test
    public void testPerformanceFailure() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        newTestStepGroup("Group 1");
        service.perform().throwException(new PerformanceFailure("Too slow"));
    }

    @Test
    public void testAccessFailure() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        newTestStepGroup("Group 1");
        service.perform().throwException(new AccessFailure("Access Failed"));
    }

    @Test
    public void testAutomationException() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        newTestStepGroup("Group 1");
        service.perform().throwException(new AutomationException("Automation Exception"));
    }

    @Test
    public void testFrameworkError() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        service.perform().throwException(new NullPointerException("null was null"));
    }

    @Test
    public void testSuccess() {
        PseudoService service = getService(ComponentId.create(PseudoService.class, "test"));
        sleep();
        newTestStepGroup("Group 1");
        service.perform().succeed("1", "2", "3");
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
