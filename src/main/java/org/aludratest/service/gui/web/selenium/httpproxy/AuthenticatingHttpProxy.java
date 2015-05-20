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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.aludratest.exception.AutomationException;
import org.aludratest.util.DataUtil;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.databene.commons.Base64Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Opens a server socket at the {@link #localPort}, receives HTTP requests,
 * injects custom HTTP request headers 
 * and forwards the request to the server {@link #realHost}.   
 * @author Volker Bergmann
 */
public class AuthenticatingHttpProxy {

    /** The logger of the class */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatingHttpProxy.class);

    /** The buffer size for the socket */
    private static final int BUFSIZE = 8 * 1024;

    /** The String-formatted IP address of the host on which the process is running. */
    private String localHost;

    /** The local port on which to listen for incoming connections. */
    private int localPort;

    /** The host to which to forward the incoming requests. */
    private HttpHost realHost;

    /** The custom HTTP header information to inject. */
    private Map<String, String> customHeaders;

    /** A boolean flag for controlling the server's main listener loop. */
    private boolean running;

    private ServerThread serverThread;

    /** Constructor with the central configuration settings. */
    public AuthenticatingHttpProxy(int localPort, String targetHost, int targetPort) {
        try {
            this.localHost = InetAddress.getLocalHost().getHostAddress();
            this.localPort = localPort;
            this.realHost = new HttpHost(targetHost, targetPort);
            this.customHeaders = new HashMap<String, String>();
            this.running = false;
            this.serverThread = null;
        } catch (UnknownHostException e) {
            throw new AutomationException("Error initializing " + getClass(), e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    /** sets an 'Authorization' HTTP request header for BASIC authentication. */
    public void setBasicAuthentication(String user, String password) {
        if (user != null || password != null) {
            String code = Base64Codec.encode((user + ":" + password).getBytes(DataUtil.UTF_8));
            setCustomRequestHeader("Authorization", "Basic " + code);
        }
    }

    /** Sets the name-value pair of a custom HTTP request header to add to forwarded calls.
     *  For each request header to be set, all incoming request headers of the same key 
     *  are deleted, then the new value is set. */
    public void setCustomRequestHeader(String key, String value) {
        customHeaders.put(key, value);
    }

    public String mapTargetToProxyUrl(String requestedUrlString) {
        try {
            URL requestedUrl = new URL(requestedUrlString);
            int requestedPort = requestedUrl.getPort();
            if (requestedPort == -1) {
                requestedPort = 80; // NOSONAR
            }
            if (requestedUrl.getHost().equals(realHost.getHostName()) && requestedPort == realHost.getPort()) {
                URL proxyUrl = new URL(requestedUrl.getProtocol(), localHost, localPort, requestedUrl.getFile());
                return proxyUrl.toString();
            } else {
                return requestedUrlString;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        serverThread = new ServerThread(localPort, realHost);
        this.running = true;
        serverThread.start();
    }

    public void stop() {
        this.running = false;
        try {
            if (this.serverThread != null) { // this only happens when start() has failed
                this.serverThread.serversocket.close();
                this.serverThread = null;
            }
        } catch (IOException e) {
            LOGGER.error("Error closing server socket. ", e);
        }
    }

    class ServerThread extends Thread {

        private final HttpHost target;
        private final HttpService httpService;
        private final ServerSocket serversocket;

        public ServerThread(int port, HttpHost target) throws IOException {
            this.target = target;
            this.serversocket = new ServerSocket(port);
            this.setDaemon(true);

            HttpRequestInterceptor authenticator = new HttpRequestInterceptor() {
                @Override
                public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                    for (Map.Entry<String, String> customHeader : customHeaders.entrySet()) {
                        String key = customHeader.getKey();
                        String value = customHeader.getValue();
                        request.removeHeaders(key);
                        if (value != null) {
                            request.addHeader(key, value);
                        }
                    }
                }
            };

            // Set up HTTP protocol processor for incoming connections
            HttpProcessor inhttpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                    //new RequestContent(),
                    authenticator, new RequestHeaderOverwriter(HTTP.TARGET_HOST, target.toHostString()), new RequestConnControl(), new RequestExpectContinue(true) });

            // Set up HTTP protocol processor for outgoing connections
            HttpProcessor outhttpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { new ResponseDate(), new ResponseContent(), new ResponseConnControl() });

            // Set up outgoing request executor
            HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

            // Set up incoming request handler
            UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
            reqistry.register("*", new ProxyHandler(this.target, outhttpproc, httpexecutor));

            // Set up the HTTP service
            this.httpService = new HttpService(inhttpproc, reqistry);
        }

        @Override
        public void run() {
            LOGGER.info("Listening on " + localHost + ":" + localPort);
            while (running && !Thread.interrupted()) {
                try {
                    int bufsize = BUFSIZE;
                    // Set up incoming HTTP connection
                    Socket insocket = this.serversocket.accept();
                    DefaultBHttpServerConnection inconn = new DefaultBHttpServerConnection(bufsize);
                    LOGGER.debug("Incoming connection from " + insocket.getInetAddress());
                    inconn.bind(insocket);

                    // Set up outgoing HTTP connection
                    Socket outsocket = new Socket(this.target.getHostName(), this.target.getPort());
                    DefaultBHttpClientConnection outconn = new DefaultBHttpClientConnection(bufsize);
                    outconn.bind(outsocket);
                    LOGGER.debug("Outgoing connection to " + outsocket.getInetAddress());

                    // Start worker thread
                    Thread t = new RequestProcessorThread(AuthenticatingHttpProxy.this, this.httpService, inconn, outconn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException e) {
                    break;
                } catch (SocketException e) {
                    if (running) {
                        LOGGER.error("Socket error: " + e.getMessage(), e);
                    }
                    break;
                } catch (IOException e) {
                    LOGGER.error("I/O error initialising connection thread: " + e.getMessage(), e);
                    break;
                }
            }
            if (this.serversocket != null) {
                try {
                    this.serversocket.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing server socket. ", e);
                }
            }
        }
    }

}
