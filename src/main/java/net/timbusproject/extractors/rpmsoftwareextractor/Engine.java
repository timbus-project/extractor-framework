/**
 * Copyright (c) 2014, Caixa Magica Software Lda (CMS).
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
package net.timbusproject.extractors.rpmsoftwareextractor;

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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {

    enum Scope { INSTALLED_PACKAGES, UNIVERSE }

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
        commands.setProperty("rpm-qa", "rpm -qa --qf 'Id: %{nvra}\\nPackage: %{name}\\nBasename: %|basenames?{%{basenames}}:{}|\\nVersion: %{evr}\\nInstalled-Size: %{size}\\nArchitecture: %|arch?{%{arch}}:{}|\\nLicense: %{license}\\nSection: %{group}\\nVendor: %|vendor?{%{vendor}}:{}|\\nMaintainer: %|packager?{%{packager}}:{}|\\nDepends: [%{requirename} (%{requireflags:depflags} %{requireversion}), ]\\nProvides: [%{providename} (%{provideflags:depflags} %{provideversion}), ]\\nConflicts: [%{conflictname} (%{conflictflags:depflags} %{conflictversion}), ]\\nConffiles:\\n[\\{%{fileflags:fflags}\\} %{filemd5s} %{filenames}\\n]\\n' | sed -r '/^(Depend|Provide|Conflict)s: /!b;s/, $| \\( \\)//g' | grep -vE '^[a-zA-Z0-9\\-]+: ?$'");
        commands.setProperty("installers", "repoquery -a --qf 'Id: %{name}-%{version}-%{release}.%{arch}\\nLocation: %{location}\\n'");
    }


    public JSONObject run(Scope scope) throws JSchException, InterruptedException, JSONException, IOException {
        switch (scope) {
            case INSTALLED_PACKAGES: return extractInstalledPackages();
//            case UNIVERSE: return extractUniverse();
        }
        return new JSONObject();
    }

/*
    private JSONObject extractUniverse() throws JSchException, InterruptedException, JSONException, IOException {
        if (isSsh()) {
            log.info("Connecting to " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            try {
                sshManager.connect();
            } catch (JSchException e) {
                log.error("Connection failed.");
                throw e;
            }
        }
        log.info("Starting universe extraction...");

        log.info("Extracting packages...");
        JSONObject extraction = newExtraction(extractAllYumPackages(), true);

        log.info("Extraction finished.");
        if (isSsh()) {
            log.info("Closing connection from " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            sshManager.disconnect();
        }

        return extraction;
    }
*/

    private JSONObject extractInstalledPackages() throws JSchException, InterruptedException, JSONException, IOException {
        if (isSsh()) {
            log.info("Connecting to " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            try {
                sshManager.connect();
            } catch (JSchException e) {
                log.error("Connection failed.");
                throw e;
            }
        }
        log.info("Starting installed packages extraction...");

        if (((ch.qos.logback.classic.Logger) log).getLevel().equals(Level.INFO))
            log.info("Extracting...");
        log.debug("Extracting installed packages...");
        Hashtable<String, JSONObject> table = extractAllRpmPackages();
        log.debug("Extracting installers...");
        extractInstallers(table);
        JSONObject extraction = newExtraction(table.values());

        log.info("Extraction finished.");
        if (isSsh()) {
            log.info("Closing connection from " + sshManager.getHost() + ":" + sshManager.getPort() + "...");
            sshManager.disconnect();
        }

        return extraction;
    }

    private JSONObject newExtraction(Collection<JSONObject> data) throws InterruptedException, JSchException, IOException, JSONException {
        return newExtraction(data, false);
    }
    private JSONObject newExtraction(Collection<JSONObject> data, boolean isUniverse) throws InterruptedException, JSchException, IOException, JSONException {
        return new JSONObject()
                .put("isUniverse", isUniverse)
                .put("operatingSystem", isCommandAvailable("lsb_release")
                        ? doCommand("echo $(lsb_release -ircs)").getProperty("stdout").replaceAll("\\n", " ").trim()
                        : "SO could not be extracted")
                .put("architecture", doCommand("uname -i").getProperty("stdout").trim())
                .put("machineId", doCommand("echo 'xri://+machine?+hostid='`hostid`'/+hostname='`hostname`").getProperty("stdout").trim())
                .put("data", data);
    }

    private Hashtable<String, JSONObject> extractAllRpmPackages() throws InterruptedException, JSchException, IOException, JSONException {
        String rpm = doCommand(commands.getProperty("rpm-qa")).getProperty("stdout");
        Hashtable<String, JSONObject> table = new Hashtable<String, JSONObject>();
        for (String packageInfo : rpm.split("\\n\\n")) {
            JSONObject object = extractPackage(packageInfo);
            if (!object.getString("Package").equals("gpg-pubkey") && !object.getString("License").equals("pubkey") && !object.getString("Section").equals("Public Keys"))
                table.put(object.getString("Id"), object);
        }
        return table;
    }

/*
    private Collection<JSONObject> extractAllYumPackages() throws InterruptedException, JSchException, IOException, JSONException {
        String universe = doCommand(commands.getProperty("...")).getProperty("stdout");
        Collection<JSONObject> collection = new ArrayList<JSONObject>();
        for (String control : universe.split("\\n\\n"))
            collection.add(extractPackage(control));
        return collection;
    }
*/

    private JSONObject extractPackage(String control) throws JSONException {
        final Pattern fieldPattern = Pattern.compile("([^:]+):[ \\n](\\p{all}+)");
        JSONObject object = new JSONObject();
        for (String field : control.split("\\n(?=\\w)")) {
            Matcher matcher = fieldPattern.matcher(field);
            matcher.find();
            try {
                if (matcher.group(1).matches("(?i)conffiles")) {
                    object.put(matcher.group(1), extractConffiles(matcher.group(2).trim()));
                } else if (matcher.group(1).matches("(?i)depends|provides|conflicts")) {
                    object.put(matcher.group(1), extractListField(matcher.group(2)));
                } else
                    object.put(matcher.group(1), matcher.group(2));
            } catch (IllegalStateException e) {
                log.warn("\"" + field + "\" from package \"" + object.optString("Package", "") + "\" could not be extracted.");
            }
        }
        return object;
    }

    private JSONArray extractListField(String list) throws JSONException {
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
        final Pattern pattern = Pattern.compile("\\{(?<flag>\\p{Lower}*)} (?<hash>\\w*) (?<file>.+)");
        JSONArray array = new JSONArray();
        for (String conffile : list.split("\\n")) {
            Matcher matcher = pattern.matcher(conffile);
            matcher.find();
            if (matcher.group("flag").contains("c")) {
                JSONObject object = new JSONObject().put("file", matcher.group("file"));
                if (!matcher.group("hash").isEmpty())
                    object.put("hash", matcher.group("hash"));
                array.put(object);
            }
        }
        return array;
    }

    private void extractInstallers(Hashtable<String, JSONObject> packages) throws InterruptedException, JSchException, IOException, JSONException {
        if (!isCommandAvailable("repoquery")) { log.warn("Installers could not be extracted."); return; }
        String rpm = doCommand(commands.getProperty("installers")).getProperty("stdout");
        for (String installerInfo : rpm.split("\\n\\n")) {
            JSONObject object = extractPackage(installerInfo);
            if (packages.containsKey(object.getString("Id")))
                packages.get(object.getString("Id")).put("Installer", object.getString("Location"));
        }
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
