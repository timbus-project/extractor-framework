/**
 * Copyright (c) 2013, Caixa Magica Software Lda (CMS).
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

package net.timbusproject.extractors.modules.local.sshwrapper;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

public class SSHManager {
    private JSch jschSSHChannel;
    private String strUserName;
    private String strConnectionIP;
    private int intConnectionPort;
    private String strPassword;
    private Session sesConnection;
    private int intTimeOut;

    private void doCommonConstructorActions(String userName, String password, String connectionIP,
                                            String knownHostsFileName, String privateKey) throws JSchException {
        jschSSHChannel = new JSch();
        if (knownHostsFileName != null && knownHostsFileName.length() > 0)
            jschSSHChannel.setKnownHosts(knownHostsFileName);

        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
        if (privateKey != null && privateKey.length() > 0)
            jschSSHChannel.addIdentity(privateKey);
    }

    public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
                      String privateKey) throws JSchException {
        doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName, privateKey);
        intConnectionPort = 22;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
                      int connectionPort, String privateKey) throws JSchException {
        doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName, privateKey);
        intConnectionPort = connectionPort;
        intTimeOut = 60000;
    }

    public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
                      String privateKey, int connectionPort, int timeOutMilliseconds) throws JSchException {
        doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName, privateKey);
        intConnectionPort = connectionPort;
        intTimeOut = timeOutMilliseconds;
    }

    public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName,
                      int connectionPort, int timeOutMilliseconds, String privateKey) {
        try {
            doCommonConstructorActions(userName, password, connectionIP, knownHostsFileName, privateKey);
        } catch (JSchException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        intConnectionPort = connectionPort;
        intTimeOut = timeOutMilliseconds;
    }

    public String connect() throws JSchException {
        String errorMessage = null;
        sesConnection = jschSSHChannel.getSession(strUserName, strConnectionIP, intConnectionPort);
        sesConnection.setPassword(strPassword);
        // UNCOMMENT THIS FOR TESTING PURPOSES, BUT DO NOT USE IN PRODUCTION
        sesConnection.setConfig("StrictHostKeyChecking", "no");
        sesConnection.connect(intTimeOut);
        return errorMessage;
    }

    private String logError(String errorMessage) {
        return errorMessage;
    }

    private String logWarning(String warnMessage) {
        return warnMessage;
    }

    public String sendCommand(String command) throws IOException, JSchException {
        StringBuilder outputBuffer = new StringBuilder();
        try {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();

            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                readByte = commandOutput.read();
            }
            channel.disconnect();
        } catch (IOException ioX) {
            logWarning(ioX.getMessage());
            return null;
        }
        return outputBuffer.toString();
    }

    public void close() {
        sesConnection.disconnect();
    }

}