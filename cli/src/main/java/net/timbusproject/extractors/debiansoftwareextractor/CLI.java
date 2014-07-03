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

package net.timbusproject.extractors.debiansoftwareextractor;

import ch.qos.logback.classic.joran.JoranConfigurator;
import com.fasterxml.uuid.Generators;
import com.jcraft.jsch.JSchException;
import org.apache.commons.cli.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class CLI {

    private static final String formatUUID = "d17250e8-af6e-5b84-8fab-404d5ecee47f";

    public static void main(String... args) throws ParseException, IOException, JSONException, JSchException, java.text.ParseException, InterruptedException {
        new JoranConfigurator();
        final Options options = new Options();
        final CommandLineParser parser = new PosixParser();
        //Available options
        options.addOption("r", "remote", true, "[user@]hostname]");
        options.addOption("l", "local", false, "Extract locally. Prints to screen by default");
        options.addOption("f", "output-file", true, "[filename]");
        options.addOption("h", "help", false, "Prints this help message");
        final CommandLine line = parser.parse(options, args);

        //Print help if there are no arguments
        checkOptions(options, line);

        if (line.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }

        JSONObject finalResult;
        JSONArray result = null;
        String user;
        String fqdn;
        if (line.hasOption("r")) {
            String extract = getOption("r", line);
            if (extract.isEmpty()) {
                System.exit(0);
            } else {
                String[] split = extract.split("@");
                //user@hostname
                if (split.length == 2) {
                    user = split[0];
                    fqdn = split[1];
                    SSHManager manager = new SSHManager(user, fqdn);
                    System.out.print("Enter password: ");
                    manager.setPassword(new String(System.console().readPassword()));

                    result = new Engine(manager).run();
//                    System.out.println("Extracting...");
                    //    System.out.print(jsonArray.toString(2)); //TODO save to file
                }
            }
        } else if (line.hasOption("l")) {
            result = new Engine().run();
        }
        finalResult = new JSONObject().put("extractor", "debian-software-extractor")
                .put("format", new JSONObject().put("id", formatUUID).put("multiple", false))
                .put("uuid", Generators.timeBasedGenerator().generate())
                .put("result", result);
        if (line.hasOption("f")) {
            String fileName = getOption("f", line);
            if (result != null) {
                if (!fileName.isEmpty())
                    writeToFile(fileName, finalResult);
                else
                    writeToFile("output_local_extraction.json", finalResult);
            }
        } else {
            if (result != null)
                System.out.println(finalResult.toString());
        }
    }

    private final static String getOption(String option, CommandLine line) {
        if (line.hasOption(option)) {
            return line.getOptionValue(option);
        }

        return "";
    }

    private final static void checkOptions(Options options, CommandLine line) {
        HelpFormatter formatter = new HelpFormatter();
        if (line.getOptions().length == 0)
            formatter.printHelp("launch", options);

    }

    private final static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("launch", options);
        System.exit(0);
    }

    public static void writeToFile(String fileName, JSONObject output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.write(output.toString(2));
        writer.close();
    }

}
