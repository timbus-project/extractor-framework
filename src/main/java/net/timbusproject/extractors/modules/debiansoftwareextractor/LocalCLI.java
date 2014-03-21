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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

public class LocalCLI {

    public static void main(String[] args) throws IOException, JSONException {
        try {
            CommandManager commandManager = new CommandManager();
            JSONArray array;

            array = commandManager.run(true);
            if (args.length > 0) {
                File f;
                if (args[0].equals("y"))
                    f = new File("extraction.json");
                else
                    f = new File(args[0]);
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(f));
                fileWriter.write(array.toString(2));
                fileWriter.close();
                if (args.length > 1) {
                    String command;
                    if (args[1].equals("y"))
                        command = "java -jar local-deb-software-converter-1.0-SNAPSHOT.jar";
                    else
                        command = "java -jar " + args[1];
                    if (args.length > 2){
                        command = command + " " + args[2];
                        if(args.length > 3)
                            command = command + " " + args[3];
                    }
                    commandManager.doCommand(command);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

