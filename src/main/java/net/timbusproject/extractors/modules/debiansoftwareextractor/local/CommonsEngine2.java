package net.timbusproject.extractors.modules.debiansoftwareextractor.local;

import com.jcraft.jsch.JSchException;
import net.timbusproject.extractors.core.Endpoint;
import net.timbusproject.extractors.modules.debiansoftwareextractor.absolute.Engine;
import net.timbusproject.extractors.modules.debiansoftwareextractor.absolute.SSHManager;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 12/17/13
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonsEngine2 {

    private final CLI cmd = new CLI();
    private final JSONObject request = new JSONObject();
    private JSONObject response;
    //    private final OutputStream out;
    private final Log log;
    Engine engine = new Engine();

    public CommonsEngine2(String... args) throws IOException, ParseException, JSONException, JSchException, java.text.ParseException {
        this(args, null);
    }

    public CommonsEngine2(String[] args, OutputStream out) throws IOException, ParseException, JSONException, JSchException, java.text.ParseException {
        response = new JSONObject();
        setOptions();
        OutputStream o = null;
//        cmd.parse(args);
        try {
            o = parse(args);
        } catch (ParseException e) {
            Log.out(System.out, true, true, e.getMessage());
        }
        log = new Log(o != null ? o : out);
        printHelp();
        execute();
    }

    private void execute() throws IOException, JSONException, JSchException, java.text.ParseException {
        String result = "";
        if (cmd.hasOption("l")) {
            boolean show = false;
            if (cmd.hasOption("s"))
                show = true;
            result = engine.run(show).toString();
        }
        if (cmd.hasOption("j")) {
            StringBuilder request = new StringBuilder();
            String currentLine;
            BufferedReader fileReader = new BufferedReader(new FileReader(new File(cmd.getOptionValue("j"))));
            while ((currentLine = fileReader.readLine()) != null)
                request.append(currentLine);
            fileReader.close();
            JSONObject jsonObject = new JSONObject(request.toString());
            SSHManager manager = new SSHManager(
                    jsonObject.optString("user"),
                    jsonObject.optString("password"),
                    jsonObject.optString("fqdn"),
                    jsonObject.optString("knownHosts"),
                    jsonObject.has("port") ? Integer.parseInt(jsonObject.getString("port")) : Endpoint.DEFAULT_SSH_PORT,
                    jsonObject.optString("privateKey"));
            result = engine.run(manager).toString();
            if(cmd.hasOption("s"))
                System.out.println(result);
        }
        if (cmd.hasOption("o"))
            if (result != "") {
                engine.writeToFile(cmd.getOptionValue("o"), result);
            }
    }

    public boolean printHelp() throws IOException {
        if (cmd.getParsedOptions() == null)
            printUsage(System.out);
        else if (cmd.hasOption("h"))
            printUsage(System.out);
        else
            return false;
        return true;
    }

    public boolean confirmInput() throws IOException, ParseException {
        Hashtable<String, List<Object>> table = new Hashtable<>();
        for (Option option : cmd.getParsedOptions()) {
            String title = cmd.getOptionTitle(option);
            if (!table.containsKey(title))
                table.put(title, new ArrayList<>());
            table.get(title).add(cmd.getParsedValue(option));
        }
/*
        String[] values = inputToArray(cmd.getParsedValues());
        Log.out(System.out, true, "Your input was:");
        Log.out(System.out, true, values);
*/
        List<String> list = new ArrayList<>();
        list.add("Your input was:");
        for (Map.Entry<String, List<Object>> entry : table.entrySet()) {
            list.add(formatString(4, cmd.getOptionsTab(), ':', entry.getKey(), String.valueOf(entry.getValue()).replaceAll("\\[|\\]", "")));
        }
        Log.out(System.out, true, list.toArray(new String[list.size()]));
        Log.out(System.out, "Proceed using this information? [y/n] ");
        switch (new Scanner(System.in).nextLine().trim().toLowerCase()) {
            case "y":
            case "":
                list.add("");
                log.out(true, list.toArray(new String[list.size()]));
                Log.out(System.out, true, "");
                return true;
        }
        return false;
    }

/*
    private String[] inputToArray(Hashtable<String, List<Object>> table) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<Object>> entry : table.entrySet()) {
            list.add(formatString(4, cmd.getOptionsTab(), ':', entry.getKey(), String.valueOf(entry.getValue()).replaceAll("\\[|\\]", "")));
        }
        return list.toArray(new String[list.size()]);
    }
*/

    public boolean convert() throws JSONException, IOException, ParseException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : cmd.getAllParsedValues("f")) {
            if (!(o instanceof File) || !((File) o).isFile()) {
                Log.out(System.out, true, true, "Invalid file" + (o != null ? ": " + o : "."));
                return false;
            }
            Scanner scanner = new Scanner((File) o);
            JSONObject jsonObject = new JSONObject();
            while (scanner.hasNextLine()) {
                Map.Entry<String, String> entry = new AbstractMap.SimpleImmutableEntry<>(
                        scanner.findInLine(Pattern.compile("[\\p{L}|\\p{N}]+")),
                        scanner.nextLine().substring(1).trim()
                );
                if (entry.getValue() != null && entry.getValue().length() > 0)
                    jsonObject.put(entry.getKey(), entry.getValue());
            }
            jsonArray.put(jsonObject);
        }
        request.put("extractions", jsonArray);
        return true;
    }

    public void printRequest() throws JSONException, IOException {
        log.out(true, request.toString(2));
        Log.out(System.out, true, "Input generated.");
    }

    /*public StatusLine submit() throws IOException, JSONException {
        HttpPost post = new HttpPost("/api/extract");
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(request.toString()));
        HttpResponse httpResponse = new DefaultHttpClient().execute(post);

        if (httpResponse.getFirstHeader("Content-Type").getValue().equals("application/json")) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(stream);
            response = new JSONObject(new String(stream.toByteArray()));
        }
        return httpResponse.getStatusLine();
    }*/

    public void printResponse() throws JSONException, IOException {
        log.out(true, response.toString(2));
    }

    private void setOptions() {
        cmd.addOption(
                CLOption.buildOption(new Option("h", "help", false, "print help")),
                CLOption.buildOption(new Option("l", "local-extraction", false, "Do local extraction")),
                CLOption.buildOption(new Option("s", "show-extraction", false, "Show extraction in stdout")),
                CLOption.buildOption(new Option("f", "text-file", true, "load request from text file"), "path-to-file", File.class).setTitle("Text files"),
                CLOption.buildOption(new Option("j", "json-file", true, "load request from json file"), "path-to-file", File.class).setTitle("JSON files"),
                CLOption.buildOption(new Option("o", "out", true, "file to save output to"), "path-to-file", File.class).setTitle("Output file")
        );
    }

    private OutputStream parse(String[] args) throws ParseException, FileNotFoundException {
        try {
            cmd.parse(args);
            if (cmd.hasOption("o"))
                return new FileOutputStream((File) cmd.getParsedValue("o"));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n");
        }
        return null;
    }

    private Properties readManifest() throws IOException {
        Properties manifest = new Properties();
        Scanner scan = new Scanner(getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
        manifest.setProperty("title",
                scan.findWithinHorizon(Pattern.compile("Implementation-Title:", Pattern.CASE_INSENSITIVE), 0) != null
                        ? scan.nextLine().trim()
                        : "Context Population CLI");
        scan.reset();
        manifest.setProperty("version",
                scan.findWithinHorizon(Pattern.compile("Implementation-Version:", Pattern.CASE_INSENSITIVE), 0) != null
                        ? scan.nextLine().trim()
                        : "0.0.1-SNAPSHOT");
        return manifest;
    }

    private void printUsage(OutputStream out) throws IOException {
        Properties mf = readManifest();
        new HelpFormatter().printHelp(new PrintWriter(out, true), 120, "extraction", "", cmd.getOptions(), 2, 4,
                "\nRunning version " + mf.getProperty("version") + " of the " + mf.getProperty("title") + ".", true);
    }

    private String formatString(int indentation, int tabSize, char separator, String string1, String string2) {
        return (indentation > 0 ? String.format("%" + indentation + "s", ' ') : "") + string1 +
                (separator != '\0' ? separator : "")
                + (tabSize > 0 ? String.format("%" + Math.max(tabSize - string1.length(), 1) + "s", ' ') : "") + string2;
    }

}
