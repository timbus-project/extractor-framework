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

package net.timbusproject.extractors.modules.debiansoftwareextractor.cli;

import com.jcraft.jsch.JSchException;
import net.timbusproject.extractors.modules.debiansoftwareextractor.Engine;
import net.timbusproject.extractors.modules.debiansoftwareextractor.ReadJSON;
import net.timbusproject.extractors.modules.debiansoftwareextractor.SSHManager;
import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.text.*;
import java.util.Scanner;

public class CLI {
    public static void main(String... args) throws ParseException, IOException, JSONException, JSchException, java.text.ParseException {
        final Options options = new Options();
        final CommandLineParser parser = new PosixParser();
        //Available options
        options.addOption("e", "extract", true, "[user@]hostname]");
        options.addOption("h", "help", false, "Prints this help message");
        final CommandLine line = parser.parse(options, args);

        //Print help if there are no arguments
        checkOptions(options, line);

        if (line.hasOption("h")) {
            printHelp(options);
        }

        String extract = getOption("e", line);
        if (extract.isEmpty()) {
//            printHelp(options);
        } else {
            String[] split = extract.split("@");
            String user = "", fqdn = "";
            //user@hostname
            if (split.length == 2) {
                user = split[0];
                fqdn = split[1];
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please write your pass: ");
                String pass = scanner.nextLine();
                SSHManager manager = new SSHManager(user, pass, fqdn, "", "");

                Engine engine = new Engine();
                JSONArray jsonArray = engine.run(manager);
                System.out.print(jsonArray.toString(2));
            }
            System.exit(0);
        }
    }

    public static String getOption(String option, CommandLine line) {
        if (line.hasOption(option)) {
            return line.getOptionValue(option);
        }

        return "";
    }

    public static void checkOptions(Options options, CommandLine line) {
        HelpFormatter formatter = new HelpFormatter();
        if (line.getOptions().length == 0)
            formatter.printHelp("launch", options);

    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("launch", options);
        System.exit(0);
    }
}
