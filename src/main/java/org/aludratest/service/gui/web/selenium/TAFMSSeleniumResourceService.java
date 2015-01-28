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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;
import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AutomationException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.plexus.component.annotations.Requirement;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;

/** Uses a TAFMS server to obtain Selenium resources. The TAFMS server deals with priorities among different users.
 * 
 * @author falbrech */
@ConfigProperties({
    @ConfigProperty(name = "tafms.url", type = String.class, description = "The base URL to the TAFMS server providing the resources", defaultValue = "http://127.0.0.1:8080/tafms"),
    @ConfigProperty(name = "tafms.user", type = String.class, description = "The user name to use for the TAFMS server", required = true),
    @ConfigProperty(name = "tafms.password", type = String.class, description = "The password to use for the TAFMS server", required = true),
    @ConfigProperty(name = "tafms.jobName", type = String.class, description = "A name to send to TAFMS as identifying job name. This is used for logging and statistics.", required = false),
    @ConfigProperty(name = "tafms.niceLevel", type = int.class, description = "The nice level to use for this job (-20 to 19). This only affects the priority in relation to other running jobs by the same TAFMS user. The lower the value, the higher the priority.", defaultValue = "0") })
public class TAFMSSeleniumResourceService implements SeleniumResourceService, Configurable {

    private static final Logger LOG = LoggerFactory.getLogger(TAFMSSeleniumResourceService.class);

    @Requirement
    private AludraTestConfig aludraConfig;

    private Preferences configuration;

    private Map<String, String> hostResourceIds = new ConcurrentHashMap<String, String>();

    @Override
    public String acquire() {
        // prepare a JSON query to the given TAFMS server
        JSONObject query = new JSONObject();

        try {
            query.put("resourceType", "selenium");
            query.put("niceLevel", configuration.getIntValue("tafms.niceLevel", 0));
            String jobName = configuration.getStringValue("tafms.jobName");
            if (jobName != null && !"".equals(jobName)) {
                query.put("jobName", jobName);
            }
        }
        catch (JSONException e) {
        }

        // prepare authentication
        DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
        provider.addCredentials(configuration.getStringValue("tafms.user"), configuration.getStringValue("tafms.password"));

        CloseableHttpClient client = HttpClientBuilder.create().setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .disableConnectionState().disableAutomaticRetries().setDefaultCredentialsProvider(provider).build();

        try {
            boolean wait;

            do {
                // send a POST request to resource URL
                HttpPost request = new HttpPost(getTafmsUrl() + "resource");

                // attach query as JSON string data
                request.setEntity(new StringEntity(query.toString(), ContentType.APPLICATION_JSON));

                CloseableHttpResponse response = null;

                // fire request
                response = client.execute(request);

                try {
                    if (response.getStatusLine() == null) {
                        throw new ClientProtocolException("No HTTP status line transmitted");
                    }

                    String message = extractMessage(response);
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        LOG.error("Exception when querying TAFMS server for resource. HTTP Status: "
                                + response.getStatusLine().getStatusCode() + ", message: " + message);
                        return null;
                    }

                    JSONObject object = new JSONObject(message);
                    if (object.has("errorMessage")) {
                        LOG.error("TAFMS server reported an error: " + object.get("errorMessage"));
                        return null;
                    }

                    // continue wait?
                    if (object.has("waiting") && object.getBoolean("waiting")) {
                        wait = true;
                        query.put("requestId", object.getString("requestId"));
                    }
                    else {
                        JSONObject resource = object.optJSONObject("resource");
                        if (resource == null) {
                            LOG.error("TAFMS server response did not provide a resource. Message was: " + message);
                            return null;
                        }

                        String url = resource.getString("url");
                        hostResourceIds.put(url, object.getString("requestId"));

                        return url;
                    }
                }
                finally {
                    IOUtils.closeQuietly(response);
                }
            }
            while (wait);

            // should never come here
            return null;
        }
        catch (ClientProtocolException e) {
            LOG.error("Exception in HTTP transmission", e);
            return null;
        }
        catch (IOException e) {
            LOG.error("Exception in communication with TAFMS server", e);
            return null;
        }
        catch (JSONException e) {
            LOG.error("Invalid JSON received from TAFMS server", e);
            return null;
        }
        finally {
            IOUtils.closeQuietly(client);
        }
    }

    @Override
    public void release(String server) {
        if (server == null) {
            return;
        }

        String resourceKey = hostResourceIds.remove(server);
        if (resourceKey == null) {
            return;
        }

        CloseableHttpClient client = HttpClientBuilder.create().setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .disableConnectionState().disableAutomaticRetries().build();

        // send a DELETE request to resource URL
        HttpDelete request = new HttpDelete(getTafmsUrl() + "resource/" + resourceKey);
        CloseableHttpResponse response = null;

        try {
            response = client.execute(request);
        }
        catch (IOException e) {
            LOG.warn("Could not release TAFMS resource", e);
        }
        finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(client);
        }
    }

    @Override
    public int getHostCount() {
        // Just return number of configured Threads, as resource is "shared" and number can vary
        return aludraConfig.getNumberOfThreads();
    }

    @Override
    public String getPropertiesBaseName() {
        return "tafms";
    }

    @Override
    public void fillDefaults(MutablePreferences preferences) {
    }

    @Override
    public void configure(Preferences preferences) {
        int niceLevel = preferences.getIntValue("tafms.niceLevel", 0);
        if (niceLevel < -20 || niceLevel > 19) {
            throw new AutomationException("Illegal value for tafms.niceLevel: " + niceLevel
                    + ". Value must be from -20 to +19, inclusive");
        }

        String url = preferences.getStringValue("tafms.url");
        try {
            new URL(url);
        }
        catch (Exception e) {
            throw new AutomationException("Illegal URL for tafms.url: " + url, e);
        }

        String user = preferences.getStringValue("tafms.user");
        if (user == null || "".equals(user)) {
            throw new AutomationException("TAFMS user name is missing");
        }
        String password = preferences.getStringValue("tafms.password");
        if (password == null || "".equals(password)) {
            throw new AutomationException("TAFMS password is missing");
        }

        configuration = preferences;
    }

    private String getTafmsUrl() {
        String url = configuration.getStringValue("tafms.url");
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    private String extractMessage(HttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            return null;
        }

        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(in, baos);
            return new String(baos.toByteArray(), "UTF-8");
        }
        finally {
            EntityUtils.consumeQuietly(entity);
            IOUtils.closeQuietly(in);
        }

    }

}
