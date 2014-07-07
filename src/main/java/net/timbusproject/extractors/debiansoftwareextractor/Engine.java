package net.timbusproject.extractors.debiansoftwareextractor;

import ch.qos.logback.classic.Level;
import com.jcraft.jsch.JSchException;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {

    private final Logger log = LoggerFactory.getLogger(Engine.class);
    private final Properties commands = new Properties();
    private final SSHManager sshManager;

    public Engine() { this((SSHManager) null); }
    public Engine(SSHManager ssh) { this(ssh, Level.INFO); }
    public Engine(Level logLevel) { this(null, logLevel); }
    public Engine(SSHManager ssh, Level logLevel) {
        ((ch.qos.logback.classic.Logger) log).setLevel(logLevel);
        log.debug("Initializing...");
        sshManager = ssh;
        commands.setProperty("is-command-available", "command -v %s");
        commands.setProperty("dpkg-status", "cat /var/lib/dpkg/status");
//        commands.setProperty("dpkg-status", "dpkg --status firefox dpkg google-chrome-stable");
        commands.setProperty("dpkg-packages", "dpkg -l | sed 1,5d | awk '{print $2;}'");
        commands.setProperty("dpkg-packages-versions", "dpkg -l | sed 1,5d | awk '{print $2\"=\"$3;}'");
        commands.setProperty("licensecheck", "licensecheck -r -copyright %s 2>/dev/null");
        commands.setProperty("ppa", "apt-cache madison %s | grep ' Packages$' | awk '{print $1,$3,$5;}' | uniq");
        commands.setProperty("filename", "apt-cache show %s | grep -E '^((Package|Version|Filename): |$)'");
    }

    public JSONArray run() throws JSchException, InterruptedException, JSONException, IOException {
        if (isSsh()) {
            log.info("Connecting to " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            try {
                sshManager.connect();
            } catch (JSchException e) {
                log.error("Connection failed.");
                throw e;
            }
        }
        log.info("Starting extraction...");

        if (((ch.qos.logback.classic.Logger) log).getLevel().equals(Level.INFO))
            log.info("Extracting...");
        log.debug("Extracting installed packages...");
        Hashtable<String, JSONObject> table = extractPackages();
        log.debug("Extracting licenses...");
        extractLicenses(table);
        log.debug("Extracting installers...");
        extractInstallers(table);

        log.info("Extraction finished.");
        if (isSsh()) {
            log.info("Closing connection from " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            sshManager.disconnect();
        }

        return new JSONArray(table.values());
    }

    private Hashtable<String, JSONObject> extractPackages() throws InterruptedException, JSchException, IOException, JSONException {
        String dpkg = doCommand(commands.getProperty("dpkg-status")).getProperty("stdout");
        Hashtable<String, JSONObject> table = new Hashtable<String, JSONObject>();
        for (String control : dpkg.split("\\n\\n")) {
            JSONObject object = extractPackage(control);
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
        if (!isCommandAvailable("licensecheck")) { log.warn("Licenses could not be extracted."); return; }
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

    private void extractInstallers(Hashtable<String, JSONObject> packages) throws InterruptedException, JSchException, IOException, JSONException {
        if (!isCommandAvailable("apt-cache")) { log.warn("Installers could not be extracted."); return; }
        final Pattern ppaPattern = Pattern.compile("(\\S+) (\\S+) (.+)");
        final Pattern filenamePattern = Pattern.compile("([^:]+): (.+)");
        String packagesCommand = "$(echo $(" + commands.getProperty("dpkg-packages") + "))";
        Hashtable<String, Properties> map = new Hashtable<String, Properties>();
        for (String pkg : doCommand(String.format(commands.getProperty("filename"), packagesCommand)).getProperty("stdout").split("\\n\\n")) {
            Properties properties = new Properties();
            for (String field : pkg.split("\\n")) {
                Matcher matcher = filenamePattern.matcher(field);
                matcher.find();
                properties.setProperty(matcher.group(1), matcher.group(2));
            }
            map.put(properties.getProperty("Package"), properties);
        }
        for (String line : doCommand(String.format(commands.getProperty("ppa"), packagesCommand)).getProperty("stdout").split("\\n")) {
            Matcher matcher = ppaPattern.matcher(line);
            matcher.find();
            if (map.containsKey(matcher.group(1)) && map.get(matcher.group(1)).getProperty("Version").equals(matcher.group(2)) && !map.get(matcher.group(1)).containsKey("ppa"))
                map.get(matcher.group(1)).setProperty("ppa", matcher.group(3));
        }
        for (Map.Entry<String, Properties> entry : map.entrySet())
            if (packages.containsKey(entry.getKey()) && entry.getValue().containsKey("Filename") && entry.getValue().containsKey("ppa"))
                packages.get(entry.getKey()).put("Installer", entry.getValue().getProperty("ppa") + entry.getValue().getProperty("Filename"));
    }

    private Properties doCommand(String command) throws InterruptedException, JSchException, IOException {
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
        log.trace("Testing command: " + command);
        Properties commandProperties = doCommand(String.format(commands.getProperty("is-command-available"), command));
        return Integer.parseInt(commandProperties.getProperty("exit-value")) == 0;
    }

    private boolean isSsh() { return sshManager != null; }

}
