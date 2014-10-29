/**
 * Copyright (c) 2014, Caixa Magica Software Lda (CMS).
 * The work has been developed in the TIMBUS Project and the above-mentioned are Members of the TIMBUS Consortium.
 * TIMBUS is supported by the European Union under the 7th Framework Programme for research and technological
 * development and demonstration activities (FP7/2007-2013) under grant agreement no. 269940.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including without
 * limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTIBITLY, or FITNESS FOR A PARTICULAR
 * PURPOSE. In no event and under no legal theory, whether in tort (including negligence), contract, or otherwise,
 * unless required by applicable law or agreed to in writing, shall any Contributor be liable for damages, including
 * any direct, indirect, special, incidental, or consequential damages of any character arising as a result of this
 * License or out of the use or inability to use the Work.
 * See the License for the specific language governing permissions and limitation under the License.
 */
package net.timbusproject.extractors.rpmsoftwareextractor;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

public class SSHManager {

    private Session session;

    public SSHManager(String username, String fqdn) throws JSchException { this(username, fqdn, 22); }
    public SSHManager(String username, String fqdn, int port) throws JSchException {
        this(username, fqdn, port, null, null);
    }
    public SSHManager(String username, String fqdn, int port, String privateKey) throws JSchException {
        this(username, fqdn, port, privateKey, null);
    }
    public SSHManager(String username, String fqdn, int port, InputStream knownHosts) throws JSchException {
        this(username, fqdn, port, null, knownHosts);
    }
    public SSHManager(String username, String fqdn, int port, String privateKey, InputStream knownHosts) throws JSchException {
        JSch jSch = new JSch();
        if (privateKey != null && !privateKey.isEmpty()) jSch.addIdentity(privateKey);
        if (knownHosts != null) jSch.setKnownHosts(knownHosts);
        session = jSch.getSession(username, fqdn, port);
        // TODO: comment for production use
        session.setConfig("StrictHostKeyChecking", "no");
    }

    String getHost() { return session.getHost(); }
    int getPort() { return session.getPort(); }
    public void setPassword(String password) { session.setPassword(password); }

    public void connect() throws JSchException { connect(60 * 1000); }
    public void connect(int timeout) throws JSchException { session.connect(timeout); }

    public Properties sendCommand(String command) throws IOException, InterruptedException, JSchException {
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.connect();
        Properties properties = new Properties();
        StringWriter writer = new StringWriter();
        IOUtils.copy(channel.getInputStream(), writer);
        properties.setProperty("stdout", writer.toString());
        try {
            IOUtils.copy(channel.getExtInputStream(), writer);
            properties.setProperty("stderr", writer.toString());
        } catch (NullPointerException ignored) {}
        new Thread(channel).join();
        properties.setProperty("exit-value", String.valueOf(channel.getExitStatus()));
        channel.disconnect();
        return properties;
    }

    public void disconnect() { session.disconnect(); }

}