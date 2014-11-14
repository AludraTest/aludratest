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
package org.aludratest.impl.log4testing.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aludratest.impl.log4testing.data.TestCaseLog;
import org.aludratest.impl.log4testing.util.LogUtil;
import org.aludratest.service.Action;
import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.util.SystemConnectorProxy;
import org.aludratest.util.AludraTestUtil;
import org.aludratest.util.ControlFlowHandler;

/**
 * {@link InvocationHandler} implementation for the dynamic proxy API.
 * It is intended to wrap {@link AludraService} objects X and add
 * logging features to them: Any object Y returned by from a call to
 * X' methods perform(), verify() or check() is wrapped with a
 * {@link ControlFlowHandler} proxy that controls execution and
 * a {@link LoggingInvocationHandler} proxy that logs any method
 * invocation of Y to log4testing.
 * @author Volker Bergmann
 */
public class AludraServiceInvocationHandler implements InvocationHandler {

    /** The service object to wrap. */
    private Object realObject;

    /** A system connector to provide to the {@link ControlFlowHandler} for checking asynchronous error occurrences. Usage of an
     * {@link SystemConnectorProxy} allows me to pass the connector proxy to the service proxies and exchange the real connector
     * afterwards. */
    private SystemConnectorProxy systemConnector;

    /** The {@link ComponentId} of the instance. */
    private ComponentId<?> serviceId;

    /** The log4testing test case to log the information to. */
    private TestCaseLog testCase;

    /** A pool of previously used proxies for reuse. */
    private Map<String, Object> proxyPool;

    private AludraContext context;

    /** Constructor
     * @param realObject the service object to wrap
     * @param serviceId
     * @param testCase the testCase to log invocation information to.
     * @param context The AludraTest context to use to e.g. retrieve configuration. */
    public AludraServiceInvocationHandler(Object realObject, ComponentId<?> serviceId, TestCaseLog testCase, AludraContext context) {
        this.realObject = realObject;
        this.systemConnector = new SystemConnectorProxy(null);
        this.serviceId = serviceId;
        this.testCase = testCase;
        this.context = context;
        this.proxyPool = new HashMap<String, Object>();
    }

    /**
     * Performs the actual method invocation on the real object,
     * and on calls to methods perform(), verify() or check() it
     * creates or reuses logging proxies that track the
     * invocation information with log4testing.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable { //NOSONAR
        // Note: The following code provokes 'Security - Array is stored directly' in SonarCube,
        //       but the behavior is correct, since this is wrapper code which has to forward
        //       invocation data unchanged.
        String methodName = method.getName();
        if ("setSystemConnector".equals(methodName)) {
            SystemConnector newConnector = (SystemConnector) args[0];
            this.systemConnector.setRealSystemConnector(newConnector);
            return method.invoke(realObject, this.systemConnector);
        } else if (methodName.equals("perform") || methodName.equals("verify") || methodName.equals("check")) {
            Object invocationResult = method.invoke(realObject, args);
            Class<?> interfaceType = method.getReturnType();
            Object wrappedResult = getOrCreateProxy(invocationResult, interfaceType);
            if (wrappedResult instanceof Action) {
                ((Action) wrappedResult).setSystemConnector(this.systemConnector);
            }
            return wrappedResult;
        } else {
            return method.invoke(realObject, args);
        }
    }


    // private helper methods --------------------------------------------------

    private Object getOrCreateProxy(Object realResult, Class<?> interfaceType) {
        String objectId = interfaceType.getName() + "@" + System.identityHashCode(realResult);
        Object result = proxyPool.get(objectId);
        if (result == null) {
            result = createProxy(realResult, interfaceType, objectId);
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object createProxy(Object realObject, Class interfaceType, String objectId) {
        Object result = wrapWithLogger(interfaceType, realObject, serviceId);
        result = AludraTestUtil.wrapWithControlFlowHandler(result, interfaceType, serviceId, systemConnector, testCase, context);
        proxyPool.put(objectId, result);
        return result;
    }

    /**
     * Creates a new logging wrapper that wraps the given object and logs invocations.
     */
    private <T> T wrapWithLogger(Class<? super T> interfaceType, T object, ComponentId<?> serviceId) {
        return LogUtil.wrapWithLogger(object, interfaceType, serviceId, testCase);
    }

}
