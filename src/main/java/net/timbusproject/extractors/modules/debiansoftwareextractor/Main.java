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

import org.apache.commons.cli.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;

public class Main {
    public static void main(String... args) throws ParseException, IOException, JSONException {
        Options options = new Options();
        addOptions(options, args);
        //TODO Testing
        ReadJSON json = new ReadJSON("input.json");
        JSONArray userJSON = json.getJsonArray();
        String user = userJSON.getJSONObject(0).getString("user");
        String pass = userJSON.getJSONObject(0).getString("password");
        String fqdn = userJSON.getJSONObject(0).getString("fqdn");

    }

    private static void addOptions(Options options,String... args) throws ParseException {
        options.addOption("e", "extract", true, "extracts information");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options,args);

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("debian-software-extractor",options);


//        Endpoint endpoint = new Endpoint();

        if(cmd.hasOption("e") || cmd.hasOption("extract")){
             DebianSoftwareExtractor softwareExtractor = new DebianSoftwareExtractor();
//             softwareExtractor.extract();
            System.out.println("Extract things ");
        }

    }
}
