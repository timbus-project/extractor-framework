package net.timbusproject.extractors.modules.debiansoftwareextractor;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Engine2 {

    public JSONArray run(SSHManager instance) throws JSchException, IOException, JSONException {
        String dpkgStatus = "less /var/lib/dpkg/status";
        instance.connect();
        String result = instance.sendCommand(dpkgStatus);

        StringBuilder stringBuilder = new StringBuilder(result);
        Scanner scanner = new Scanner(stringBuilder.toString());
        LinkedList<String> packgeList = buildList(scanner);
        JSONArray array = parseList(packgeList);

        // printList(packgeList);

        // writeToFile(array.toString(2));

        instance.close();
        return array;
    }

    private void printList(LinkedList<String> packgeList) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : packgeList)
            stringBuilder.append(str);

        writeToFile(stringBuilder.toString());

    }

    private JSONArray parseList(LinkedList<String> packgeList) throws JSONException {

        JSONArray array = new JSONArray();
        for (String str : packgeList) {
            array.put(parser(str));
        }
        return array;
    }

    private void writeToFile(String result) throws IOException {
        File file = new File("output_local_extraction.json");
        if (file.exists())
            file.createNewFile();

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bufWriter = new BufferedWriter(fileWriter);
        bufWriter.write(result);
        bufWriter.close();

    }

    private LinkedList<String> buildList(Scanner scanner) {
        LinkedList<String> linkedList = new LinkedList<>();
        StringBuilder stringBuilder = new StringBuilder();
        String tmp;
        while (scanner.hasNextLine()) {
            tmp = scanner.nextLine();
            stringBuilder.append(tmp + "\n");

            if (tmp.isEmpty()) {
                linkedList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }
        return linkedList;
    }


    public JSONObject parser(String pkg) throws JSONException {
        Scanner scanner = new Scanner(pkg);
        JSONObject jsonObject = new JSONObject();
        String key = "";
        while (scanner.hasNextLine()) {
            String tmp = "";

            if (scanner.hasNext())
                tmp = scanner.next();

            if (tmp.endsWith(":")) {
                key = tmp.replace(':', ' ').trim();
            }

            switch (key.toLowerCase()) {
                    case "description":
                        String line = "";
                        while(scanner.hasNext()){

                            line  = scanner.next();
                            System.out.println(line);
                        }

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
                    if (scanner.hasNextLine())
                        jsonObject.put(key, scanner.nextLine().trim());
                    break;
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
                JSONObject jsonDependency = new JSONObject();
                for (String pkgOr : temp) {
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
                }
                jsonArrayOr.put(jsonDependency);
            }
            if (jsonArrayOr.length() == 1)
                jsonArrayAnd.put(jsonArrayOr.get(0));
            else
                jsonArrayAnd.put(jsonArrayOr);
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


    private void getConffiles(Scanner scanner, JSONObject jsonObject, String tmp, String key) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        String line = "";
        if (scanner.hasNextLine())
            line = scanner.nextLine().trim();

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

    //    public JSONObject parserTwo(String pkg) throws JSONException {
//        Scanner scanner = new Scanner(pkg);
//        JSONObject jsonObject = new JSONObject();
//        String key = "";
//        while (scanner.hasNextLine()) {
//            String tmp = scanner.next();
//            if (tmp.endsWith(":")) {
//                key = tmp.replace(':', ' ').trim();
//            }
//
//            switch (key.toLowerCase()) {
//                case "description":
//                    StringBuilder stringBuilder = new StringBuilder();
//                    while (scanner.hasNextLine() && !tmp.contains(key)) {
//                        String str = scanner.nextLine();
//                        if (str.length() == 0) {
//                            // string is a blank line
//                        } else if (!(Character.isUpperCase(str.charAt(0)) && str.contains(":"))) {
//                            stringBuilder.append(str);
//                        }
//                    }
//                    jsonObject.put(key, stringBuilder);
//                    break;
//                case "depends":
//                    JSONArray jsonArrayAnd = getDepends(scanner);
//                    jsonObject.put(key, jsonArrayAnd);
//                    break;
//                case "package":
//                    jsonObject.put(key, scanner.nextLine().trim());
//                    break;
//                default:
//                    if (scanner.hasNext())
////                    jsonObject.put(key, scanner.nextLine().trim());
//                        break;
//            }
//        }
//        return jsonObject;
//    }

}
