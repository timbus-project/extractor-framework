package net.timbusproject.extractors.modules.linuxhardware;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: cmdesktop
 * Date: 12-11-2013
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
public class Engine {
    public String run(SSHManager instance) throws JSchException, IOException, JSONException {
        instance.connect();
        String output = instance.sendCommandSudo("lshw -json", "cmdesktop");
        writeToFile(output);
        instance.close();
        return output;
    }

    private void writeToFile(String output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter("output_2.json", "UTF-8");
        writer.write(output);
        writer.close();
    }

}

