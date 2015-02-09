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
import static org.aludratest.service.gui.web.selenium.httpproxy.ProxyConstants.HTTP_IN_CONN;
import static org.aludratest.service.gui.web.selenium.httpproxy.ProxyConstants.HTTP_OUT_CONN;

import java.io.IOException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes a single HTTP request.
 * @author Volker Bergmann
 */
public class RequestProcessorThread extends Thread {

    /** The logger of the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessorThread.class);

    private AuthenticatingHttpProxy owner;

    private final HttpService httpservice;

    private final HttpServerConnection inconn;

    private final HttpClientConnection outconn;

    /** Creates a RequestProcessorThread and initializes all attributes. */
    public RequestProcessorThread(AuthenticatingHttpProxy owner, HttpService httpservice, HttpServerConnection inconn, HttpClientConnection outconn) {
        this.owner = owner;
        this.httpservice = httpservice;
        this.inconn = inconn;
        this.outconn = outconn;
    }

    /** The {@link Thread}'s worker method which processes the request. */
    @Override
    public void run() {
        LOGGER.debug("New request processor thread");

        // Create context and bind connection objects to the execution context
        HttpContext context = new BasicHttpContext(null);
        context.setAttribute(HTTP_IN_CONN, this.inconn);
        context.setAttribute(HTTP_OUT_CONN, this.outconn);

        // checking request's keep-alive attribute
        Boolean keepAliveObj = (Boolean) context.getAttribute(HTTP_CONN_KEEPALIVE);
        boolean keepAlive = (keepAliveObj != null && keepAliveObj.booleanValue());

        // handle in/out character transfer according to keep-alive setting
        try {
            while (!Thread.interrupted()) {
                if (!this.inconn.isOpen()) {
                    this.outconn.close();
                    break;
                }
                LOGGER.debug("Handling request");

                this.httpservice.handleRequest(this.inconn, context);

                if (!keepAlive) {
                    this.outconn.close();
                    this.inconn.close();
                    LOGGER.debug("Finishing request");
                    break;
                }
            }
        } catch (ConnectionClosedException ex) {
            if (keepAlive && owner.isRunning()) {
                LOGGER.error("Client closed connection");
            } else {
                LOGGER.debug("Client closed connection");
            }
        } catch (IOException ex) {
            LOGGER.error("I/O error: " + ex.getMessage());
        } catch (HttpException ex) {
            LOGGER.error("Unrecoverable HTTP protocol violation: " + ex.getMessage());
        } finally {
            try {
                this.inconn.shutdown();
            } catch (IOException ignore) {
                // ignore possible exceptions
            }
            try {
                this.outconn.shutdown();
            } catch (IOException ignore) {
                // ignore possible exceptions
            }
            LOGGER.debug("Finished connection thread");
        }
    }

}