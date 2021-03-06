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

package net.timbusproject.extractors.modules.linuxhardware.local;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.Scanner;

/**
 * Created by miguel on 14-01-2014.
 */
public class CommandManager {


    public String run(String command) throws IOException, JSONException, ParseException {
        return doCommand(command);
    }

    public String doCommand(String command) throws IOException {
        StringBuilder outputBuffer = new StringBuilder();
        InputStream commandOutput;
        OutputStream commandInput;
        ProcessBuilder processBuilder;
        if (command == null)
            processBuilder = new ProcessBuilder("/usr/bin/lshw", "-json");
        else
            processBuilder = new ProcessBuilder("/bin/sh", "-c", command);

        Process p = processBuilder.start();
        commandInput = p.getOutputStream();
//        BufferedWriter buffered = new BufferedWriter(new OutputStreamWriter(commandInput));
//        buffered.write(pass);
        commandOutput = p.getInputStream();

        int readByte = commandOutput.read();
        String line;
        while (readByte != 0xffffffff) {
            outputBuffer.append((char) readByte);
            readByte = commandOutput.read();
        }
        commandOutput.close();
        commandInput.close();

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        p.destroy();
        return outputBuffer.toString();
    }

    public void putInexFile() throws IOException {
        InputStream stream = this.getClass().getResourceAsStream("/i-nex-cpuid");
        File file = new File(System.getProperty("user.home") + "/i-nex-cpuid");
        file.createNewFile();
        FileOutputStream fileStream = new FileOutputStream(file);
        try {
            int c;
            while ((c = stream.read()) != -1)
                fileStream.write(c);
        } finally {
            stream.close();
            fileStream.close();
        }

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
                key = key.toLowerCase();
                String s = key.toLowerCase();
                if (s.equals("description")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNextLine() && !tmp.contains(key)) {
                        String str = scanner.nextLine();
                        if (!(Character.isUpperCase(str.charAt(0)) && str.contains(":"))) {
                            stringBuilder.append(str);
                        }
                    }
                    jsonObject.put(key, stringBuilder);

                } else if (s.equals("conffiles")) {
                    getConffiles(scanner, jsonObject, tmp, key);

                } else if (s.equals("provides") || s.equals("pre-depends") || s.equals("recommends") || s.equals("suggests") || s.equals("conflicts") || s.equals("replaces") || s.equals("depends")) {
                    JSONArray jsonArrayAnd = getDepends(scanner);
                    jsonObject.put(key, jsonArrayAnd);

                } else {
                    jsonObject.put(key, scanner.nextLine().trim());

                }
            }
        }
        return jsonObject;
    }


    private JSONArray getDepends(Scanner scanner) throws JSONException {
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
            if (jsonArrayOr.length() == 1)
                jsonArrayAnd.put(jsonArrayOr.get(0));
            else
                jsonArrayOr.put(jsonArrayOr);
        }
        return jsonArrayAnd;
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
