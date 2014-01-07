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

package net.timbusproject.extractors.modules.debiansoftwareextractor;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Engine {

    @Autowired
    private LogService log;

    public JSONArray run(SSHManager instance) throws JSchException, IOException, JSONException {
//        String prettyCmd = "dpkg -l | awk '{print $2,\"\\t\",$3;}' | column -t | sed 1d | sed 1d";
        String dpkg = "dpkg -l | grep ^ii | awk '{print $2;}'";
        String dpkgStatus = "dpkg --status ";

        // call sendCommand for each command and the output (without prompts) is returned
        log.log(LogService.LOG_INFO, "connection: " + instance.connect());
//        System.out.println(instance.sendCommand(prettyCmd));
        String pkgList = instance.sendCommand(dpkg);
        Scanner scanner = new Scanner(pkgList);

        JSONArray jsonArray = new JSONArray();
        while (scanner.hasNextLine()) {
            String pkg = scanner.nextLine();
            log.log(LogService.LOG_INFO, "extracting " + pkg);
            jsonArray.put(parser(instance.sendCommand(dpkgStatus + pkg)));
//            jsonArray.put(parser(instance.sendCommand(dpkgStatus + "bash")));

            // line for testing purposes. delete it to retrieve more packages
//            break;
        }

        // close only after all commands are sent
        instance.close();

        //uncomment this line to save to file
        writeToFile(jsonArray);
        return jsonArray;
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
                        JSONArray jsonArray = getDescription(scanner, tmp, key);
                        if (jsonArray.length() > 0)
                            jsonObject.put(key, jsonArray);
                        break;
                    case "conffiles":
                        getConffiles(scanner, jsonObject, tmp, key);
                        break;
                    case "provides":
                    case "pre-depends":
                    case "recommends":
                    case "suggests":
                    case "conflicts":
                    case "replaces":
                    case "depends":
                        String[] depends = scanner.nextLine().trim().split(",");
                        JSONArray jsonArrayAnd = new JSONArray();
                        for (String s : depends) {
                            JSONArray jsonArrayOr = new JSONArray();
                            String[] temp = s.trim().split("\\|");
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
                                jsonArrayOr.put(jsonDependency);
                            }
                            if(jsonArrayOr.length() == 1)
                                jsonArrayAnd.put(jsonArrayOr.get(0));
                            else
                                jsonArrayOr.put(jsonArrayOr);

                        }
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

    private void writeToFile(JSONArray jsonArray) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter("output.json", "UTF-8");
        writer.write(jsonArray.toString(2));
        writer.close();
    }

    /*
      Gets all the packages from the Depends field
      Returns them in a JSONArray
     */
    private JSONArray getDepends(Scanner scanner) throws JSONException {
        String[] depends = scanner.nextLine().trim().split(",");
        JSONArray jsonArrayDepends = new JSONArray();
        for (String s : depends) {
            String[] splitDepends = s.split(" \\|");
            for (String t : splitDepends) {
                String[] temp = t.split(" ");
                JSONObject jsonObjectDepends = parseString(temp);
                jsonArrayDepends.put(jsonObjectDepends);
            }
        }
        return jsonArrayDepends;
    }

    private JSONArray getDepends2(Scanner scanner) throws JSONException {
        String[] depends = scanner.nextLine().trim().split(",");
        JSONArray jsonArray1 = new JSONArray();
        for (String s : depends) {
            JSONArray jsonArrayDepends = new JSONArray();
            if (s.contains("|")) {
                // removes the disjunction (pipe)
                String[] temp = s.trim().split("\\|");
                for (String aTemp : temp) {
//                                    System.out.println("TEMP:" + temp[i]);
                    JSONObject jsonObject2 = new JSONObject();
                    String[] split = aTemp.split(" ");
                    for (String str : split) {
                        if (str.contains("(") && !str.equals("") ) {
                            jsonObject2.put("Comparator", str.replace("(", ""));
//                                            System.out.println("Comparator " + str);
                        } else if (str.contains(")") && !str.equals("")) {  //version
                            jsonObject2.put("", str.replace(")", ""));
//                                            System.out.println("Version " + str);
                        } else if (str.length() > 0 && !str.equals("")) {
                            jsonObject2.put("Package", str);
//                                            System.out.println("Package " + str);
                        }
                    }
                    jsonArrayDepends.put(jsonObject2);
//                    System.out.println("JSONDEPENDS " + jsonArrayDepends.toString(2));
                }
                jsonArray1.put(jsonArrayDepends);
//                jsonObject.put(key, jsonArray1);
            } else {
                for (String strng : depends) {
                    String[] splitDepends = strng.split(" ");
                    for (String t : splitDepends) {
                        String[] temp = t.split(" ");
                        JSONObject jsonObjectDepends = new JSONObject();
                        for (String str : temp) {
                            if (str.contains("(") && !str.equals("")) {
                                jsonObjectDepends.put("Comparator", str.replace("(", ""));
                            } else if (str.contains(")") && !str.equals("")) {
                                jsonObjectDepends.put("Version", str.replace(")", ""));
                            } else if (!str.equals("")) {
                                jsonObjectDepends.put("Package", str);
                            }
                        }
                        jsonArrayDepends.put(jsonObjectDepends);
                    }
                }
                jsonArray1.put(jsonArrayDepends);
            }
        }
        return jsonArray1;
    }

    /*
     Auxiliary function to parse dependencies packages
     */
    private JSONObject parseString(String[] arrayString) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (String str : arrayString) {
            if (str.contains("(") && !str.equals("")) {
                jsonObject.put("Comparator", str.replace("(", ""));
            } else if (str.contains(")") && !str.equals("")) {
                jsonObject.put("Version", str.replace(")", ""));
            } else if (!str.equals("") && str.length() > 0) {
                jsonObject.put("Package", str);
            }
        }
        return jsonObject;
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
