package net.timbusproject.extractors.modules.linuxhardware;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import net.timbusproject.extractors.modules.Endpoint;
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
    public void run(SSHManager instance) throws JSchException, IOException, JSONException {
        //uncomment these for testing proposes
        //SSHManager instance = sshManager;
//        instance = new SSHManager(
//                "jorge",
//                "",
//                "10.10.96.59",
//                "",
//                "/home/cmdesktop/.ssh/id_rsa"
//        );

        instance.connect();
        //Get CPU
//        String result = instance.sendCommand("lscpu");
//        System.out.println("RESULT " + result);
//        JSONObject jsonObject = parseCPU(result);
        instance.sendCommandSudo("dmidecode --type baseboard", "cmdesktop");

        instance.close();
//        System.out.println(result);
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(jsonObject);
//        writeToFile(jsonArray);
    }

    private void writeToFile(JSONArray jsonArray) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter("output_2.json", "UTF-8");
        writer.write(jsonArray.toString(2));
        writer.close();
    }

    private JSONObject parseCPU(String result) throws JSONException {
        Scanner scanner = new Scanner(result);
        String[] tmp;
        JSONObject jsonObject = new JSONObject();
        while (scanner.hasNextLine()) {
            tmp = scanner.nextLine().split(":");
            for (int i = 0; i < tmp.length; i++) {
//                System.out.println("KEY :" + tmp[i]);
                switch (tmp[i].toLowerCase()) {
                    case "cpu mhz":
                    case "cpu family":
                    case "vendor id":
                    case "on-line cpu(s) list":
                    case "cpu(s)":
                    case "byte order":
                    case "cpu op-mode(s)":
                    case "architecture":
                        jsonObject.put(tmp[i], tmp[i + 1].trim());
                        break;
                }
            }
        }
//        System.out.println("JSON OBJECT: " + jsonObject.toString(2));
        return jsonObject;
    }

    private String parseMotherBoard(String result){
        Scanner scanner = new Scanner(result);
        String [] tmp;
        while(scanner.hasNextLine()){
            System.out.println(scanner.nextLine());

        }
        return null;
    }

    private String parseGPU(String result){
        return null;
    }

}

