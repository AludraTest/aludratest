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
package org.aludratest.util.poll;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.aludratest.exception.AutomationException;
import org.aludratest.util.timeout.TimeoutService;
import org.databene.commons.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executes a {@link PolledTask} repeatedly until it is successful or a timeout is exceeded.
 * The timeout is configured in the attribute {@link #timeout},
 * the pause interval between task invocations is determined by {@link #interval}.
 * @author Volker Bergmann
 */
public class PollService {

    /** The logger of the class */
    private static final Logger LOGGER = LoggerFactory.getLogger(PollService.class);

    /** The maximum number of milliseconds to spend with polling. */
    private int timeout;

    /** The number of milliseconds to wait between poll operations. */
    private int interval;

    /** Constructor initializing all attributes.
     *  @param timeout
     *  @param interval */
    public PollService(int timeout, int interval) {
        Assert.notNegative(timeout, "timeout");
        Assert.notNegative(interval, "interval");
        this.timeout = timeout;
        this.interval = interval;
    }

    /** @return the {@link #timeout} */
    public int getTimeout() {
        return timeout;
    }

    /** @return the {@link #interval} */
    public int getInterval() {
        return interval;
    }

    /** Executes the {@link PolledTask} repeatedly until it is successful or a timeout is exceeded.
     * @param task
     * @return a not-null result of type R returned by the task
     * @throws T if the timeout is exceeded before a task invocation returned a not-null result value */
    public <T> T poll(PolledTask<T> task) {
        long startMillis = System.currentTimeMillis();
        boolean errorOccurred = false;
        long elapsedTime = 0;
        do {
            long remainingTime = timeout - elapsedTime;
            T result = null;
            try {
                result = invokeWithTimeout(task, remainingTime);
            }
            catch (TimeoutException e) {
                LOGGER.debug("Timeout exceeded while executing task {}", task);
                break;
            }
            catch (AutomationException e) {
                throw e;
            }
            if (result != null) {
                LOGGER.debug("Task {} finished successfully, result: {}", task, result);
                return result;
            }
            else if (interval < remainingTime) {
                LOGGER.debug("Task {} did not succeed, retrying in {} ms", task, interval);
                try {
                    Thread.sleep(interval);
                }
                catch (InterruptedException e) {
                    LOGGER.error("Interrupted while polling", e);
                    errorOccurred = true;
                }
            }
            elapsedTime = System.currentTimeMillis() - startMillis;
        } while (!errorOccurred && elapsedTime < timeout);
        LOGGER.debug("Task {} did not succeed within the timeout of {} ms", task, timeout);
        return task.timedOut();
    }

    private static <T> T invokeWithTimeout(final PolledTask<T> task, long timeout) throws TimeoutException {
        Callable<T> callable = new Callable<T>() {
            @Override
            public T call() throws Exception {
                return task.run();
            }
        };
        try {
            return TimeoutService.call(callable, timeout);
        }
        catch (TimeoutException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AutomationException("Unexpected error executing task " + task, e);
        }
    }

}
