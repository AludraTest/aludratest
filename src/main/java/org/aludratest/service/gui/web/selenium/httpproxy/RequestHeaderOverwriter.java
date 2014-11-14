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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

/**
 * {@link HttpProcessor} that sets an HTTP header, removing possible former values.
 * @author Volker Bergmann
 */
public class RequestHeaderOverwriter extends AbstractHttpProcessor {

    private String name;
    private String value;

    public RequestHeaderOverwriter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        request.removeHeaders(name);
        request.addHeader(name, value);
    }

}
