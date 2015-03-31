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
package org.aludratest.service.util;

import org.aludratest.exception.PerformanceFailure;
import org.aludratest.service.SystemConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods for controlling the completion of (synchronous or asynchronous) tasks.
 * @author Volker Bergmann
 */
public class TaskCompletionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCompletionUtil.class);

    /** Private constructor for preventing instantiation of utility class. */
    private TaskCompletionUtil() { }

    /**
     * Waits a limited amount of time for an activity to start and finish
     * using the {@link SystemConnector#isBusy()} of the connector object.
     * First a startTimeout period is spent waiting for an activity to occur.
     * If no activity was noticed, the method returns false immediately.
     * Otherwise the method waits for the period of the completionTimeout
     * until no activity is reported any more. If the activity is noticed
     * to be stopped, the method returns true immediately.
     * If the system is still busy after the timeout, a {@link PerformanceFailure}
     * is thrown.
     * @param connector the {@link SystemConnector} for controlling the system
     * @param failureMessage the exception message text to use in case of a timeout
     * @param startTimeout the initial period to wait in milliseconds until activity is observed
     * @param completionTimeout the maximum time to wait milliseconds for task completion (including the startTimeout)
     * @param pollingInterval the interval to wait between checking the activity status in milliseconds
     * @return true if an activity was observed, otherwise false
     * @throws PerformanceFailure if the completionTimeout was exceeded before activity ended
     */
    public static boolean waitForActivityAndCompletion(SystemConnector connector, final String failureMessage,
            int startTimeout, int completionTimeout, int pollingInterval) {
        long startTime = System.currentTimeMillis();
        if (!waitUntilBusy(connector, startTime + startTimeout, startTimeout, pollingInterval)) {
            return false;
        }
        int dt = (int) (System.currentTimeMillis() - startTime);
        // a task is running, I wait until it is finished or I get impatient...
        LOGGER.debug("Activity was observed on {}, now waiting for completion " +
                "until the completionTimeout of {} ms", connector, completionTimeout);
        waitUntilNotBusy(connector, completionTimeout - dt, pollingInterval, failureMessage);
        LOGGER.debug("{} finished its activity", connector);
        return true;
    }

    /** Repeats a delayed loop until the provided {@link SystemConnector} reports false on invocation of
     * {@link SystemConnector#isBusy()}.
     * 
     * @param connector the {@link SystemConnector} to query if the system is busy
     * @param timeout the maximum number of milliseconds to wait
     * @param pollingInterval the number of milliseconds to wait between system state queries
     * @param failureMessage The failure message to emit if the system is still busy after timeout */
    public static void waitUntilNotBusy(SystemConnector connector, int timeout, int pollingInterval, String failureMessage) {
        LOGGER.debug("waitUntilNotBusy({})", connector);
        long timeoutTime = System.currentTimeMillis() + timeout;
        boolean busy;
        long time;
        do {
            busy = isBusy(connector);
            LOGGER.debug("{} is {}", connector, (busy ? "busy" : "not busy"));
            if (busy) {
                sleep(pollingInterval);
            }
            time = System.currentTimeMillis();
        } while (busy && time < timeoutTime);
        if (busy) {
            // timeout is exceeded and the task is still busy,
            // so I throw an appropriate exception
            LOGGER.error("waitUntilNotBusy({}) gave up waiting after the completionTimeout period of {} ms", connector, timeout);
            throw new PerformanceFailure(failureMessage);
        }
        else {
            LOGGER.debug("waitUntilNotBusy({}) finished successfully", connector);
        }
    }

    // implementation ----------------------------------------------------------

    private static boolean waitUntilBusy(SystemConnector connector, long timeoutTime, int timeoutInterval, int pollingInterval) {
        LOGGER.debug("waitUntilBusy({})", connector);
        long time;
        boolean busy;
        do {
            busy = isBusy(connector);
            LOGGER.debug("{} is {}", connector, (busy ? "busy" : "not busy"));
            if (!busy) {
                sleep(pollingInterval);
            }
            time = System.currentTimeMillis();
        } while (!busy && time < timeoutTime);
        if (!busy) {
            // I probably missed the task execution and better stop here
            LOGGER.debug("waitUntilBusy({}) gave up after the timeout of {} ms", timeoutInterval);
            return false;
        }
        LOGGER.debug("waitUntilBusy({}) finished successfully", connector);
        return true;
    }

    private static boolean isBusy(SystemConnector connector) {
        if (connector == null) {
            return false;
        }
        SystemBusyIndicator busyIndicator = connector.getConnector(SystemBusyIndicator.class);
        return busyIndicator != null ? busyIndicator.isBusy() : false;
    }

    private static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            LOGGER.error("Thread.sleep() was interrupted. ", e);
        }
    }

}
