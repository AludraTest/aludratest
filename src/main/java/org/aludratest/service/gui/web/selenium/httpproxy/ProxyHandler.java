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
package org.aludratest.service.gui.web.selenium.httpproxy;

import static org.aludratest.service.gui.web.selenium.httpproxy.ProxyConstants.HTTP_CONN_KEEPALIVE;
import static org.aludratest.service.gui.web.selenium.httpproxy.ProxyConstants.HTTP_OUT_CONN;

import java.io.IOException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link HttpRequestHandler} for handling requests and responses.
 * @author Volker Bergmann
 */
public class ProxyHandler implements HttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyHandler.class);

    private final HttpHost target;
    private final HttpProcessor httpproc;
    private final HttpRequestExecutor httpexecutor;
    private final ConnectionReuseStrategy connStrategy;

    public ProxyHandler(HttpHost target, HttpProcessor httpproc, HttpRequestExecutor httpexecutor) {
        this.target = target;
        this.httpproc = httpproc;
        this.httpexecutor = httpexecutor;
        this.connStrategy = DefaultConnectionReuseStrategy.INSTANCE;
    }

    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        HttpClientConnection conn = (HttpClientConnection) context.getAttribute(HTTP_OUT_CONN);

        context.setAttribute(HttpCoreContext.HTTP_CONNECTION, conn);
        context.setAttribute(HttpCoreContext.HTTP_TARGET_HOST, this.target);

        // Remove hop-by-hop headers
        /*request.removeHeaders(HTTP.CONTENT_LEN);
        request.removeHeaders(HTTP.TRANSFER_ENCODING);
        request.removeHeaders(HTTP.CONN_DIRECTIVE);
        request.removeHeaders("Keep-Alive");
        request.removeHeaders("Proxy-Authenticate");
        request.removeHeaders("TE");
        request.removeHeaders("Trailers");
        request.removeHeaders("Upgrade");*/

        this.httpexecutor.preProcess(request, this.httpproc, context);

        LOGGER.debug(">> Request URI: " + request.getRequestLine().getUri());
        if (LOGGER.isTraceEnabled()) {
            for (Header header : request.getAllHeaders()) {
                LOGGER.trace(header.toString());
            }
        }

        HttpResponse targetResponse = this.httpexecutor.execute(request, conn, context);
        this.httpexecutor.postProcess(response, this.httpproc, context);

        // Remove hop-by-hop headers
        /*
        targetResponse.removeHeaders(HTTP.CONTENT_LEN);
        targetResponse.removeHeaders(HTTP.TRANSFER_ENCODING);
        targetResponse.removeHeaders(HTTP.CONN_DIRECTIVE);
        targetResponse.removeHeaders("Keep-Alive");
        targetResponse.removeHeaders("TE");
        targetResponse.removeHeaders("Trailers");
        targetResponse.removeHeaders("Upgrade");
        */
        response.setStatusLine(targetResponse.getStatusLine());
        response.setHeaders(targetResponse.getAllHeaders());
        response.setEntity(targetResponse.getEntity());

        LOGGER.debug("<< Response: " + response.getStatusLine());

        boolean keepalive = this.connStrategy.keepAlive(response, context);
        context.setAttribute(HTTP_CONN_KEEPALIVE, Boolean.valueOf(keepalive));
    }

}
