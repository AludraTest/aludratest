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
package org.aludratest.util.timeout;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Performs Java invocations applying a timeout.
 * @author Volker Bergmann */
public class TimeoutService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /** Performs the invocation and throws a {@link TimeoutException} if the invocation time exceeds the specified timeout.
     * @param callable the {@link Callable} to call
     * @param timeout the maximum tolerated execution time in milliseconds
     * @return the result of the callable's {@link Callable#call()} method invocation
     * @throws Exception */
    public static <T> T call(Callable<T> callable, long timeout) throws Exception { // NOSONAR
        Future<T> handler = null;
        try {
            handler = EXECUTOR_SERVICE.submit(callable);
            return handler.get(timeout, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e) {
            // FIXME this does not stop Selenium if it hangs. The SocketInputStream#read0 method does not seem to
            // check Thread's interrupted state.
            handler.cancel(true);
            throw e;
        }
        catch (ExecutionException e) {
            // unwrap execution exception if the cause resolves to an a child class of Exception
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            else {
                throw e;
            }
        }
    }

    /** Creates a Callable that wraps a target object of type Callable, forwarding the invocation of its own
     * {@link Callable#call()} method to the targets call method and applying a timeout. If the timeout is exceeded, a
     * TimeoutException is thrown.
     * @param target the target to wrap and forward calls to
     * @param timeout the timeout to apply on target invocation
     * @return the result of the target's {@link Callable#call()} invocation */
    public static <T> Callable<T> createCallableWithTimeout(Callable<T> target, long timeout) {
        return new CallableWithTimeout<T>(target, timeout);
    }

    /** Wraps another {@link Callable} applying a timeout to the invocation of {@link Callable#call()}.
     * @param <E> the return type of the {@link Callable#call()} method. */
    public static class CallableWithTimeout<E> implements Callable<E> {

        private final Callable<E> target;
        private final long timeout;

        protected CallableWithTimeout(Callable<E> target, long timeout) {
            this.target = target;
            this.timeout = timeout;
        }

        @Override
        public E call() throws Exception {
            return TimeoutService.call(target, timeout);
        }

    }

}
