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

package net.timbusproject.extractors.modules.debiansoftwareextractor.absolute;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Scanner;

public class Engine {

    @Autowired
    private LogService log;

    public JSONArray run(SSHManager instance) throws JSchException, IOException, JSONException, ParseException {
        String dpkg = "dpkg -l | grep ^ii | awk '{print $2;}'";
        String dpkgStatus = "dpkg --status ";

        String pkgList;
        if (instance != null) {
            instance.connect();
            // call sendCommand for each command and the output (without prompts) is returned
            pkgList = instance.sendCommand(dpkg);
        } else
            pkgList = doLocalCommand(dpkg);

        Scanner scanner = new Scanner(pkgList);
        JSONArray jsonArray = new JSONArray();
        while (scanner.hasNextLine()) {
            String pkg = scanner.nextLine();
            if (instance != null)
                jsonArray.put(parser(instance.sendCommand(dpkgStatus + pkg)));
            else
                jsonArray.put(parser(doLocalCommand(dpkgStatus + pkg)));
            //jsonArray.put(parser(instance.sendCommand(dpkgStatus + "chromium-browser")));

            // line for testing purposes. delete it to retrieve more packages
            //break;
        }

        // close only after all commands are sent
        if (instance != null)
            instance.close();

//        writeToFile(jsonArray);
        return jsonArray;
    }

    public JSONArray run() throws ParseException, JSchException, JSONException, IOException {
        return run(null);
    }

    public String doLocalCommand(String command) throws IOException {
        StringBuilder outputBuffer = new StringBuilder();
        InputStream commandOutput;
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        Process p = processBuilder.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commandOutput = p.getInputStream();

        int readByte = commandOutput.read();
        while (readByte != 0xffffffff) {
            outputBuffer.append((char) readByte);
            readByte = commandOutput.read();
        }
        commandOutput.close();
        p.destroy();
        return outputBuffer.toString();
    }

    public JSONObject parser(String pkg) throws JSONException {
        Scanner scanner = new Scanner(pkg);
        JSONObject jsonObject = new JSONObject();
        String key = "";
        while (scanner.hasNextLine()) {
            if (scanner.hasNext()) {
                String tmp = scanner.next();
                if (tmp.endsWith(":")) {
                    key = tmp.replace(':', ' ').trim();
                }
                switch (key.toLowerCase()) {
                    case "description":
                        StringBuilder stringBuilder = new StringBuilder();
                        while (scanner.hasNextLine() && !tmp.contains(key)) {
                            String str = scanner.nextLine();
                            if (str.length() == 0) {
                                // string is a blank line
                            } else if (!(Character.isUpperCase(str.charAt(0)) && str.contains(":"))) {
                                stringBuilder.append(str);
                            }
                        }
                        jsonObject.put(key, stringBuilder);
                        break;
                    case "conffiles":
                        getConffiles(scanner, jsonObject, tmp, key);
                        break;
                    // fields with |
                    case "pre-depends":
                    case "recommends":
                    case "suggests":
                    case "depends":
                        // fields without |
                    case "provides":
                    case "conflicts":
                    case "replaces":
                        JSONArray jsonArrayAnd = getDepends(scanner);
                        jsonObject.put(key, jsonArrayAnd);
                        break;
                    default:
                        jsonObject.put(key, scanner.nextLine().trim());
                        break;
                }
            }
        }
        return jsonObject;
    }

    private JSONArray getDepends(Scanner scanner) throws JSONException {
        String[] depends = scanner.nextLine().trim().split(",");
        // main array
        JSONArray jsonArrayAnd = new JSONArray();
        for (String s : depends) {
            JSONArray jsonArrayOr = new JSONArray();

            if (s.contains("|")) {
                JSONArray result = orDependency(s);
                jsonArrayOr.put(result);
//                System.out.println("Result " + result.toString());
            } else {
                String[] temp = s.trim().split(" ");
                for (String pkgOr : temp) {
                    JSONObject jsonDependency = new JSONObject();
                    String[] split = pkgOr.trim().split(" ");
                    for (String string : split) {
                        if (string.contains("(")) {
                            jsonDependency.put("Comparator", string.replace("(", ""));
                        } else if (string.contains(")")) {
                            jsonDependency.put("Version", string.replace(")", ""));
                        } else if (string.length() > 0) {
                            jsonDependency.put("Package", string.trim());
                        }
//                    System.out.println("STRING" + string);
                    }
                    jsonArrayOr.put(jsonDependency);
                }
            }
            if (jsonArrayOr.length() == 1)
                jsonArrayAnd.put(jsonArrayOr.get(0));
            else
                jsonArrayOr.put(jsonArrayOr);
        }
        return jsonArrayAnd;
    }

    private JSONArray orDependency(String s) throws JSONException {
        String[] temp = s.trim().split("\\|");
        JSONArray orDependencies = new JSONArray();
        for (String pkgOr : temp) {
            JSONObject jsonDependency = new JSONObject();
            String[] split = pkgOr.trim().split(" ");
            for (String string : split) {
                if (string.contains("(")) {
                    jsonDependency.put("Comparator", string.replace("(", ""));
                } else if (string.contains(")")) {
                    jsonDependency.put("Version", string.replace(")", ""));
                } else if (string.length() > 0) {
                    jsonDependency.put("Package", string.trim());
                }
            }
            orDependencies.put(jsonDependency);
        }
        return orDependencies;
    }

    private JSONArray andDependency(String s) {
        return new JSONArray();
    }


    private void getConffiles(Scanner scanner, JSONObject jsonObject, String tmp, String key) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        String line = scanner.nextLine().trim();
        if (!tmp.contains(key)) {
            jsonObject1.put("file", tmp);
            jsonObject1.put("hash", line);
            jsonArray.put(jsonObject1);
            if (jsonObject.has(key)) {
                jsonObject.getJSONArray(key).put(jsonObject1);
            } else {
                jsonObject.put(key, jsonArray);
            }
        }
    }

    /*
       Gets all the packages from the Description field
       Returns them in a JSONArray
      */
    private JSONArray getDescription(Scanner scanner, String tmp, String key) {
        JSONArray array = new JSONArray();
        if (!tmp.contains(key)) {
            while (scanner.hasNextLine()) {
                array.put(scanner.nextLine());
            }
        }
        return array;
    }
}
