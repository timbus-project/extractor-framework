package net.timbusproject.extractors.modules.linuxhardware;

import com.jcraft.jsch.*;
import org.codehaus.jettison.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: cmdesktop
 * Date: 16-10-2013
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class SSHManager {
    private JSch jschSSHChannel;
    private String strUserName;
    private String strConnectionIP;
    private int intConnectionPort;
    private String strPassword;
    private Session sesConnection;
    private int intTimeOut;
    private String privKey;

    private void doCommonConstructorActions(String userName, String password, String connectionIP,
                                            String knownHostsFileName, String privateKey) throws JSchException {
        jschSSHChannel = new JSch();
        try {
            jschSSHChannel.setKnownHosts(knownHostsFileName);
        } catch (JSchException jschX) {
            logError(jschX.getMessage());
        }

        strUserName = userName;
        strPassword = password;
        strConnectionIP = connectionIP;
        privKey = privateKey;
        if(!privateKey.equals(""))
            jschSSHChannel.addIdentity(privKey);
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
            channel.connect();
            InputStream commandOutput = channel.getInputStream();
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

    public String sendCommandSudo(String command, String sudoPass) throws IOException,JSchException{
        Channel channel = sesConnection.openChannel("exec");
        ((ChannelExec)channel).setCommand("sudo -S -p '' "+command);

        StringBuilder stringBuilder = new StringBuilder();

        InputStream in=channel.getInputStream();
        OutputStream out=channel.getOutputStream();
        ((ChannelExec)channel).setErrStream(System.err);

        channel.connect();

        out.write((sudoPass + "\n").getBytes());
        out.flush();

        byte[] tmp=new byte[1024];
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
                String str = new String(tmp,0,i);
                stringBuilder.append(str.trim());
//                System.out.println(str);
            }
            if(channel.isClosed()){
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            try{Thread.sleep(1000);}catch(Exception ee){}
        }

        channel.disconnect();
        close();
//        System.out.println("STR " + stringBuilder.toString());
        return stringBuilder.toString();
    }


    public void close() {
        sesConnection.disconnect();
    }

}