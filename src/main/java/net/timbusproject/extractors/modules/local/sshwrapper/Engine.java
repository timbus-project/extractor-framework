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

package net.timbusproject.extractors.modules.local.sshwrapper;

import com.jcraft.jsch.JSchException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;

public class Engine {

    public JSONObject runWithPath(SSHManager instance, String path) {
        String command;
        path.trim();
        if (path.endsWith(".jar"))
            command = "Test -jar " + path;
        else if (path.endsWith(".pl"))
            command = "perl " + path;
        else if (path.endsWith(".py"))
            command = "python " + path;
        else
            command = path;
        return run(instance, command);
    }

    public JSONObject run(SSHManager instance, String command) {
        try {
            instance.connect();
//            command = command.substring(1);
//            command = command.substring(0, command.length() - 1);
            command = command.replaceAll("\\\\", "");
            System.out.println("IN ENGINE: SENDING COMMAND " + command);
            String output = instance.sendCommand(command);
            System.out.println("IN ENGINE. OUTPUT:" + output);
            return new JSONObject(output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            instance.close();
        }
        return null;
    }
}
