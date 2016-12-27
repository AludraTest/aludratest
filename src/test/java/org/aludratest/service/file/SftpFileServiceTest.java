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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link FileService} with the SFTP protocol.
 * @author Volker Bergmann
 */
@SuppressWarnings("javadoc")
public class SftpFileServiceTest extends AbstractLocalFileServiceTest {

    private SshServer sshd;

    @Override
    public void prepareFiles() throws IOException {
        super.prepareFiles();
        setupSftpServer();
    }

    public void setupSftpServer() throws IOException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setHost("127.0.0.1");
        sshd.setPort(4922);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("target/hostkey.ser"));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) {
                return "ftpuser".equals(username) && "topsecret".equals(password);
            }
        });

        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
        userAuthFactories.add(new UserAuthPassword.Factory());
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);

        // prepare directory for test files
        VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory(ROOT.getAbsolutePath());
        sshd.setFileSystemFactory(fileSystemFactory);

        sshd.start();
    }

    @Override
    public void closeTestCase() {
        try {
            if (sshd != null)
                sshd.stop();
        }
        catch (Exception e) {
            Assert.fail("Unexpected exception when closing SFTP server: " + e.getMessage());
        }
        super.closeTestCase();
    }

    @Test
    public void testGetRootChildren() {
        FileService service = getService(FileService.class, "sftptest");
        List<String> list = service.perform().getChildren("/");
        assertTrue(list.size() > 0);
    }

    @Test
    public void testGetSubfolderChildren() {
        FileService service = getService(FileService.class, "sftptest");
        List<String> list = service.perform().getChildren("sub");
        assertTrue(list.size() == 1);
        assertEquals("sub/subfile.txt", list.get(0));
    }

    @Test
    public void testgetInputStream() {
        FileService service = getService(FileService.class, "sftptest");
        service.perform().readTextFile("file.txt");
    }

    @Test
    public void testWrite() {
        FileService service = getService(FileService.class, "sftptest");
        service.perform().writeTextFile("aludratest.txt", "This is a test for AludraTest's FileService", true);
    }

}
