package net.timbusproject.extractors.debiansoftwareextractor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.jcraft.jsch.JSchException;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {

    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Properties commands = new Properties();
    private final SSHManager sshManager;

    public Engine() throws IOException { this((SSHManager) null); }
    public Engine(SSHManager ssh) throws IOException { this(ssh, Level.INFO); }
    public Engine(Level logLevel) throws IOException { this(null, logLevel); }
    public Engine(SSHManager ssh, Level logLevel) throws IOException {
        ((ch.qos.logback.classic.Logger) log).setLevel(logLevel);
        log.info("Initializing engine...");
        sshManager = ssh;
        commands.setProperty("is-command-available", "command -v %s");
        commands.setProperty("dpkg-status", "cat /var/lib/dpkg/status");
        commands.setProperty("licensecheck", "licensecheck -r -copyright %s 2>/dev/null");
    }

    public JSONArray run() throws JSchException, IOException, InterruptedException, JSONException {
        if (isSsh()) {
            log.info("Connecting...");
            try {
                sshManager.connect();
            } catch (JSchException e) {
                log.info("Connection failed.");
                throw e;
            }
        }
        log.info("Starting extraction...");

        log.info("Extracting installed packages...");
        Hashtable<String, JSONObject> table = extractPackages();
        log.info("Extracting licenses...");
        extractLicenses(table);

        log.info("Extraction finished.");
        log.info("Closing connection...");
        if (isSsh()) sshManager.disconnect();

        return new JSONArray(table.values());
    }

    private Hashtable<String, JSONObject> extractPackages() throws JSONException, InterruptedException, JSchException, IOException {
        String dpkg = doCommand(commands.getProperty("dpkg-status")).getProperty("stdout");
        Hashtable<String, JSONObject> table = new Hashtable<String, JSONObject>();
        for (String control : dpkg.split("\\n\\n")) {
            JSONObject object = extractPackage(control);
            log.debug("Extracted package: " + object.getString("Package"));
            table.put(object.getString("Package"), object);
        }
        return table;
    }

    private JSONObject extractPackage(String control) throws JSONException {
        final Pattern fieldPattern = Pattern.compile("([^:]+):[ \\n](\\p{all}+)");
        JSONObject object = new JSONObject();
        for (String field : control.split("\\n(?=\\w)")) {
            Matcher matcher = fieldPattern.matcher(field);
            matcher.find();
            if (matcher.group(1).matches("(?i)conffiles")) {
                object.put(matcher.group(1), extractConffiles(matcher.group(2).trim()));
            } else if (matcher.group(1).matches("(?i)breaks|conflicts|enhances|provides|replaces") // list fields
                    || matcher.group(1).matches("(?i)depends|pre-depends|recommends|suggests")) { // formula fields
                object.put(matcher.group(1), extractListOrFormulaField(matcher.group(2)));
            } else
                object.put(matcher.group(1), matcher.group(2));
        }
        return object;
    }

    private JSONArray extractListOrFormulaField(String list) throws JSONException {
        JSONArray array = new JSONArray();
        for (String element : list.trim().split(", "))
            if (element.contains(" | ")) {
                JSONArray formula = new JSONArray();
                for (String sentence : element.split(" \\| "))
                    formula.put(extractInlinePackage(sentence));
                array.put(formula);
            } else
                array.put(extractInlinePackage(element));
        return array;
    }

    private JSONObject extractInlinePackage(String element) throws JSONException {
        final Pattern pattern = Pattern.compile("(\\S+)(?: \\((\\S+) (.+)\\))?");
        Matcher matcher = pattern.matcher(element);
        matcher.find();
        JSONObject object = new JSONObject().put("Package", matcher.group(1));
        if (matcher.groupCount() > 1)
            object.put("Comparator", matcher.group(2)).put("Version", matcher.group(3));
        return object;
    }

    private JSONArray extractConffiles(String list) throws JSONException {
        JSONArray array = new JSONArray();
        for (String conffile : list.split("\\n ")) {
            String[] values = conffile.split(" ");
            array.put(new JSONObject().put("file", values[0]).put("hash", values[1]));
        }
        return array;
    }

    private void extractLicenses(Hashtable<String, JSONObject> packages) throws InterruptedException, JSchException, IOException, JSONException {
        if (!isCommandAvailable("licensecheck")) { log.info("Licenses could not be extracted."); return; }
        final String path = "/usr/share/doc/";
        final Pattern licensePattern = Pattern.compile("([^/]+)[^:]+: (.+)");
        for (String line : doCommand(String.format(commands.getProperty("licensecheck"), path)).getProperty("stdout").split("\\n")) {
            Matcher matcher = licensePattern.matcher(line.replace(path, ""));
            matcher.find();
            String license = matcher.group(2).trim();
            if (license.matches("(\\*No copyright\\* .+)"))
                license = license.replaceFirst("\\*No copyright\\* ", "").trim();
            if (license.equals("UNKNOWN"))
                continue;
            packages.get(matcher.group(1)).put("License", license);
        }
    }

    private Properties doCommand(String command) throws IOException, JSchException, InterruptedException {
        if (isSsh())
            return sshManager.sendCommand(command);
        Properties properties = new Properties();
        Process process = new ProcessBuilder("/bin/sh", "-c", command).start();
        StringWriter writer = new StringWriter();
        IOUtils.copy(process.getInputStream(), writer);
        properties.setProperty("stdout", writer.toString());
        IOUtils.copy(process.getErrorStream(), writer);
        properties.setProperty("stderr", writer.toString());
        properties.setProperty("exit-value", String.valueOf(process.waitFor()));
        process.destroy();
        if (Integer.parseInt(properties.getProperty("exit-value")) != 0)
            log.warn("command \"" + command.split(" ")[0] + "\" failed with status: " + properties.getProperty("exit-value"));
        return properties;
    }

    private boolean isCommandAvailable(String command) throws InterruptedException, JSchException, IOException {
        log.debug("Testing command: " + command);
        Properties commandProperties = doCommand(String.format(commands.getProperty("is-command-available"), command));
        return Integer.parseInt(commandProperties.getProperty("exit-value")) == 0;
    }

    private boolean isSsh() { return sshManager != null; }

}
