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

package net.timbusproject.extractors.modules.linuxhardware.remote;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;

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

    public void deleteInexFile() {
        try {
            sendCommand("cd ~/ && rm i-nex-cpuid");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public String sendCommand(String command) throws IOException, JSchException {
        Channel channel = sesConnection.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        InputStream normalInputStream = channel.getInputStream();
        InputStream errInputStream = channel.getExtInputStream();

        channel.connect();

        Properties properties = new Properties();
        StringWriter writer = new StringWriter();
        IOUtils.copy(normalInputStream, writer);
        properties.setProperty("result", writer.toString());
        try {
            IOUtils.copy(errInputStream, writer);
            properties.setProperty("err", writer.toString());
        } catch (NullPointerException ignored) {
        }
        try {
            new Thread(channel).join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        properties.setProperty("exit-value", String.valueOf(channel.getExitStatus()));


        channel.disconnect();
//        System.out.println("STR " + stringBuilder.toString());
        return properties.getProperty("result");
    }

    public Properties sendCommandSudo(String command, String sudoPass) throws IOException, JSchException, InterruptedException {
        Channel channel = sesConnection.openChannel("exec");
        ((ChannelExec) channel).setCommand("sudo -S -p '' " + command);

        InputStream normalInputStream = channel.getInputStream();
        InputStream errInputStream = channel.getExtInputStream();
        OutputStream out = channel.getOutputStream();

        channel.connect();

        out.write((sudoPass + "\n").getBytes());
        out.flush();

        Properties properties = new Properties();
        StringWriter writer = new StringWriter();
        IOUtils.copy(normalInputStream, writer);
        properties.setProperty("result", writer.toString());
        try {
            IOUtils.copy(errInputStream, writer);
            properties.setProperty("err", writer.toString());
        } catch (NullPointerException ignored) {
        }
        new Thread(channel).join();
        properties.setProperty("exit-value", String.valueOf(channel.getExitStatus()));


        channel.disconnect();
//        System.out.println("STR " + stringBuilder.toString());
        return properties;
    }

    public boolean sendINexFile() throws IOException, JSchException {

        InputStream stream = this.getClass().getResourceAsStream("/i-nex-cpuid");
        boolean success = writeFileToLinux("i-nex-cpuid", stream);
        if (success)
            sendCommand("chmod u+x ~/i-nex-cpuid");
        return success;

    }

    public boolean writeFileToLinux(String fileName, InputStream stream) {
        try {
            Channel obj_Channel = sesConnection.openChannel("sftp");
            obj_Channel.connect();
            ChannelSftp obj_SFTPChannel = (ChannelSftp) obj_Channel;
            obj_SFTPChannel.put(stream, fileName, ChannelSftp.OVERWRITE);
            obj_SFTPChannel.exit();
            stream.close();
            obj_Channel.disconnect();
            return true;
        } catch (Exception ex) {
            System.out.println("Problem occurred with remote sftp. No information from i-nex module will be available");
            return false;
        }
    }


    public void close() {
        sesConnection.disconnect();
    }


}