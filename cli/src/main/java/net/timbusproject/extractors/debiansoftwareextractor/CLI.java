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
package net.timbusproject.extractors.debiansoftwareextractor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.OutputStreamAppender;
import com.fasterxml.uuid.Generators;
import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLI {

    private static final String formatUUID = "d17250e8-af6e-5b84-8fab-404d5ecee47f";
    private static final Logger log = LoggerFactory.getLogger(CLI.class);
    private final Options options = new Options();
    private CommandLine cmd;

    @SuppressWarnings("AccessStaticViaInstance")
    public CLI() {
        addOptions(
                OptionBuilder.withLongOpt("help").withDescription("Shows help").create('h'),
                createOptionGroup(
                        OptionBuilder.withLongOpt("local").withDescription("Extracts locally").create('l'),
                        OptionBuilder.withLongOpt("remote").withDescription("Extracts remotely").hasArgs().withArgName("user@host:port").create('r')
                ),
                OptionBuilder.withLongOpt("output").withDescription("Outputs results to file").hasArg().withArgName("file").withType(File.class).create('o'),
                OptionBuilder.withLongOpt("pretty").withDescription("Returns a formatted result").create('p'),
                createOptionGroup(
                        OptionBuilder.withLongOpt("debug").withDescription("Provides more details").create(),
                        OptionBuilder.withLongOpt("quiet").withDescription("Provides less details").create('q')
                )
            );
    }

    private void addOptions(Object... options) {
        for (Object option : options)
            if (option != null)
                if (option instanceof Option)
                    this.options.addOption((Option) option);
                else if (option instanceof OptionGroup)
                    this.options.addOptionGroup((OptionGroup) option);
    }

    private OptionGroup createOptionGroup(Option... options) { return createOptionGroup(false, options); }
    private OptionGroup createOptionGroup(boolean required, Option... options) {
        OptionGroup group = new OptionGroup();
        for (Option option : options) group.addOption(option);
        group.setRequired(required);
        return group;
    }

    public void parse(String... args) throws ParseException {
        try {
            cmd = new PosixParser().parse(options, args);
        } catch (UnrecognizedOptionException e) { log.error(e.getLocalizedMessage()); throw e;
        } catch (MissingOptionException e) { log.error(e.getLocalizedMessage()); throw e;
        } catch (MissingArgumentException e) { log.error(e.getLocalizedMessage()); throw e;
        } catch (AlreadySelectedException e) { log.error(e.getLocalizedMessage()); throw e;
        } catch (ParseException e) { log.error(e.getLocalizedMessage()); e.printStackTrace(); throw e;
        }
    }

    public void printUsage() { printUsage(null, null); }
    public void printUsage(String header, String footer) {
        new HelpFormatter().printHelp(new PrintWriter(getLoggerStdOut(), true), 80, "extraction", header, options, 2, 1, footer, true);
    }

    public void process() throws InterruptedException, JSchException, JSONException, IOException, ParseException {
        Pattern remotePattern = Pattern.compile("(\\w[\\w\\-\\.]+\\$?){1,32}@([\\p{Alnum}\\.]+)(?::(22$|[0-9]{4,5}))?");
        if (cmd.hasOption("debug")) ((ch.qos.logback.classic.Logger) log).setLevel(Level.DEBUG);
        if (cmd.hasOption('l')) {
            printResult(finalizeResult(new Engine(cmd.hasOption('q') ? Level.WARN : cmd.hasOption("--debug") ? Level.DEBUG : Level.INFO).run()));
        } else if (cmd.hasOption('r')) {
            boolean nl = false;
            for (String remote : cmd.getOptionValues('r')) {
                if (nl) log.info("");
                if (!remote.matches(remotePattern.pattern())) {
                    log.warn("Invalid remote (skipping): " + remote);
                    nl = true;
                    continue;
                }
                Matcher matcher = remotePattern.matcher(remote);
                matcher.find();
                SSHManager sshManager;
                if (matcher.group(3) != null)
                    sshManager = new SSHManager(matcher.group(1), matcher.group(2), Integer.parseInt(matcher.group(3)));
                else
                    sshManager = new SSHManager(matcher.group(1), matcher.group(2));
                getLoggerStdOut().write(("Enter password for " + matcher.group(1) + "@" + matcher.group(2) + ": ").getBytes());
                sshManager.setPassword(new String(System.console().readPassword()));
                printResult(
                        finalizeResult(new Engine(
                                sshManager,
                                cmd.hasOption('q') ? Level.WARN : cmd.hasOption("--debug") ? Level.DEBUG : Level.INFO
                        ).run()),
                        cmd.getOptionValues('r').length > 1
                                ? matcher.group(2) + (matcher.group(3) != null ? '-' + matcher.group(3) : "")
                                : ""
                );
                nl = true;
            }
        }
    }

    private void printResult(JSONObject result) throws ParseException, IOException, JSONException {
        printResult(result, null);
    }
    private void printResult(JSONObject result, String append) throws ParseException, IOException, JSONException {
        if (cmd.hasOption('o')) {
            File file = (File) cmd.getParsedOptionValue("o");
            if (append != null && !append.isEmpty())
                file = new File(file.getName().contains(".") ? file.getAbsolutePath().replaceAll("(\\.[^\\.]+)$", '-' + append + "$1") : file.getAbsolutePath() + append);
            overwrite: if (file.exists()) {
                getLoggerStdOut().write("File already exists. Overwrite? [y/N] ".getBytes());
                String s = System.console().readLine().trim();
                if (s.equalsIgnoreCase("y")) break overwrite;
                return;
            }
            FileWriter writer = new FileWriter(file);
            if (cmd.hasOption('p')) writer.write(result.toString(2));
            else writer.write(result.toString());
            writer.flush();
            writer.close();
            log.info("Saved to file: " + file.getAbsolutePath());
        } else {
            if (cmd.hasOption('p')) getLoggerStdOut().write(result.toString(2).getBytes());
            else getLoggerStdOut().write(result.toString().getBytes());
            getLoggerStdOut().write('\n');
            getLoggerStdOut().flush();
        }
    }

    private JSONObject finalizeResult(Object result) throws JSONException {
        return new JSONObject()
                .put("extractor", "Debian Software Extractor")
                .put("format", new JSONObject().put("id", formatUUID).put("multiple", false))
                .put("uuid", Generators.timeBasedGenerator().generate())
                .put("result", result);

    }

    public boolean isHelp() { return isValid() && cmd.hasOption('h'); }
    public boolean isValid() { return cmd.getOptions() != null && cmd.getOptions().length > 0; }

    private OutputStream getLoggerStdOut() {
        return ((OutputStreamAppender)
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("root")).getAppender("STDOUT")
        ).getOutputStream();
    }
    private OutputStream getLoggerStdErr() {
        return ((OutputStreamAppender)
                ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("root")).getAppender("STDERR")
        ).getOutputStream();
    }

    public static void writeToFile(String fileName, JSONObject output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.write(output.toString(2));
        writer.close();
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        try {
            cli.parse(args);
            if (!cli.isValid() || cli.isHelp()) { cli.printUsage(); return; }
            cli.process();
        } catch (JSchException ignored) {
        } catch (Exception ignored) { ignored.printStackTrace(); log.info(""); cli.printUsage(); }
    }

}
