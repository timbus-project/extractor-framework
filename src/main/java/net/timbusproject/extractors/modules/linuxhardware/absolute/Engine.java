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
package net.timbusproject.extractors.modules.linuxhardware.absolute;

import com.jcraft.jsch.JSchException;
import net.timbusproject.extractors.core.Endpoint;
import net.timbusproject.extractors.modules.linuxhardware.local.CommandManager;
import net.timbusproject.extractors.modules.linuxhardware.remote.SSHManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;

public class Engine {

    public JSONObject run(SSHManager instance, String password) throws JSchException, IOException, JSONException {
        instance.connect();
//        JSONObject output = new JSONObject(instance.sendCommandSudo("lshw -json", password));
        instance.sendINexFile();
        JSONObject output = new JSONObject();
        output.put("lshw", new JSONObject(instance.sendCommand("/usr/sbin/lshw -json -quiet")));
        output.put("inex", new JSONObject(instance.sendCommand("cd ~/ && ./i-nex-cpuid")));
//        writeToFile(output);
        instance.deleteInexFile();
        instance.close();
        return output;
    }

    public JSONObject run(SSHManager instance, Endpoint endpoint) throws JSchException, IOException, JSONException {
        return run(instance, endpoint.getProperty("password"));
    }

    public String run() throws IOException {
        String result;
        CommandManager manager = new CommandManager();
        result = manager.doCommand(null);
        return result;
    }

    public void writeToFile(String fileName, String output) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.write(output);
        writer.close();
    }

}

