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
package org.aludratest.service.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.aludratest.service.AbstractAludraServiceTest;
import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link FileService} with the FTP protocol.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class FtpFileServiceTest extends AbstractAludraServiceTest {

    private FtpServer server;

    private Listener listener;

    @Override
    public void prepareTestCase() {
        super.prepareTestCase();
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        // set the port of the listener
        factory.setPort(4921);
        // replace the default listener
        serverFactory.addListener("default", listener = factory.createListener());
        // set test user manager
        serverFactory.setUserManager(new TestUserManager());
        // set config
        ConnectionConfig config = new DefaultConnectionConfig(false, 0, 100, 0, 100, 2);
        serverFactory.setConnectionConfig(config);
        // start the server
        server = serverFactory.createServer();
        try {
            server.start();
        }
        catch (FtpException e) {
            Assert.fail("Unexpected FTP exception: " + e.getMessage());
        }
    }

    @Override
    public void closeTestCase() {
        // assert that there is an active FTP connection (so that FileService did not use local FS)
        Assert.assertFalse(listener.getActiveSessions().isEmpty());
        super.closeTestCase();
        server.stop();
    }

    @Test
    public void testGetRootChildren() {
        FileService service = getService(FileService.class, "ftptest");
        List<String> list = service.perform().getChildren("/");
        assertTrue(list.size() > 0);
        int dirCount = 0;
        int fileCount = 0;
        for (String fileName : list) {
            if (service.check().isDirectory(fileName)) {
                dirCount++;
            }
            else {
                fileCount++;
            }
        }
        assertEquals(2, dirCount);
        assertEquals(1, fileCount);
    }

    private static class TestUserManager implements UserManager {

        private BaseUser ftpUser = new BaseUser() {
            @Override
            public AuthorizationRequest authorize(AuthorizationRequest request) {
                return request;
            }
        };

        public TestUserManager() {
            ftpUser.setName("ftpuser");
            ftpUser.setHomeDirectory(new java.io.File("src/test/resources/fileServiceTest").getAbsolutePath());
        }

        @Override
        public User getUserByName(String username) throws FtpException {
            if (ftpUser.getName().equals(username)) {
                return ftpUser;
            }
            return null;
        }

        @Override
        public String[] getAllUserNames() throws FtpException {
            return new String[] { ftpUser.getName() };
        }

        @Override
        public void delete(String username) throws FtpException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void save(User user) throws FtpException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean doesExist(String username) throws FtpException {
            return ftpUser.getName().equals(username);
        }

        @Override
        public User authenticate(Authentication authentication) throws AuthenticationFailedException {
            if (authentication instanceof UsernamePasswordAuthentication) {
                UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) authentication;
                if (ftpUser.getName().equals(auth.getUsername()) && "topsecret".equals(auth.getPassword())) {
                    return ftpUser;
                }
            }
            throw new AuthenticationFailedException();
        }

        @Override
        public String getAdminName() throws FtpException {
            return "admin";
        }

        @Override
        public boolean isAdmin(String username) throws FtpException {
            return false;
        }
    }

}
