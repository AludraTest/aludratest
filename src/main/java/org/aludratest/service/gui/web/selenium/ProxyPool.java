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
package org.aludratest.service.gui.web.selenium;

import org.aludratest.service.gui.web.selenium.httpproxy.AuthenticatingHttpProxy;
import org.aludratest.util.ObjectPool;

/**
 * Creates and manages a pool of {@link AuthenticatingHttpProxy} instances
 * using an {@link ObjectPool}.
 * @author Volker Bergmann
 */
public class ProxyPool {

    /** The {@link ObjectPool} which does the real proxy management */
    private ObjectPool<AuthenticatingHttpProxy> proxies;

    /** Creates a pool of proxies which forward all calls to the same target server and port, but each one listening on a different
     * local port. The used local port numbers begin with 'firstLocalPort' (e.g. 8000) and use the following port numbers (e.g.
     * 8001, 8002, ...)
     * @param targetHost the target host
     * @param targetPortCfg the configured target port
     * @param firstLocalPort the first local port to be opened by the proxies
     * @param poolSize The size of the pool. */
    public ProxyPool(String targetHost, int targetPortCfg, int firstLocalPort, int poolSize) {
        int targetPort = (targetPortCfg >= 0 ? targetPortCfg : 80);
        this.proxies = new ObjectPool<AuthenticatingHttpProxy>(poolSize, false);
        for (int i = 0; i < poolSize; i++) {
            int localPort = firstLocalPort + i;
            this.proxies.add(new AuthenticatingHttpProxy(localPort, targetHost, targetPort));
        }
    }

    /** Acquires a proxy from the pool for exclusive use by a single client, waiting if necessary.
     *  @return a new proxy from the pool
     *  @throws InterruptedException if the wait has been interrupted */
    public AuthenticatingHttpProxy acquire() throws InterruptedException {
        return proxies.acquire();
    }

    /** Puts back a used proxy into the pool.
     *  The client has to call this to put its proxy back to the pool after using it.
     *  @param proxy The proxy to release*/
    public void release(AuthenticatingHttpProxy proxy) {
        proxies.release(proxy);
    }

}
