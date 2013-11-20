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
    public void run(SSHManager instance) throws JSchException, IOException, JSONException {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        instance.connect();
        //get Motherboard
//        String result = ;
//        jsonObject.put("CPU", parse(result));
//        jsonObject.put("MotherBoard",parse(result));
//        System.out.println("JSON OBJECT " + jsonObject.toString(2));

//        jsonObject.put("Output",instance.sendCommandSudo("lshw -json", "cmdesktop").trim());
//        System.out.println("OUTPUT " + instance.sendCommandSudo("lshw -json", "cmdesktop"));
//        jsonArray.put(jsonObject.toString(2));
//        jsonArray.put(getComponent("dmidecode --type baseboard","Motherboard",instance));
//        jsonArray.put(getComponent("lshw -C display","Graphic Card",instance));
        writeToFile(instance.sendCommandSudo("lshw -json", "cmdesktop"));
        instance.close();
//        System.out.println(result);
    }

    private JSONObject getComponent(String command, String component, SSHManager instance) throws IOException, JSchException, JSONException {
        instance.connect();
        String result = instance.sendCommandSudo(command, "cmdesktop");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(component, parse(result));
        instance.close();
        return jsonObject;
    }

    private void writeToFile(String output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter("output_2.json", "UTF-8");
        writer.write(output);
        writer.close();
    }

    private JSONObject parse(String result) throws JSONException {
        Scanner scanner = new Scanner(result);
        JSONObject jsonObject = new JSONObject();
//        cleanHeader(scanner);
//        String info = scanner.nextLine();
        while (scanner.hasNextLine()) {
            String[] tmp = scanner.nextLine().trim().split(":");
//            jsonObject.put(scanner.next(),scanner.next());
            String key = "", value = "";
            for (int i = 0, tmpLength = tmp.length; i < tmpLength; i++) {
//                String str = tmp[i];
                if (i % 2 != 0) {
                    value = tmp[i].trim();
                } else if (i % 2 == 0) {
//                    System.out.println(tmp[i]);
                    key = tmp[i].trim();
                }
                jsonObject.put(key, value);
            }
        }
        System.out.println(jsonObject.toString(2));
        return jsonObject;
    }

}

