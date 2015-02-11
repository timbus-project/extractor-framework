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
package net.timbusproject.extractors.pojo;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestWrapper {

    @XmlElement
    public String[] commands;
    @XmlElement
    public HashMap<String, String>[] urls;

    public String toString() {
        if (commands != null) {
            for (int i = 0; i < commands.length; i++) {
                commands[i] = JSONObject.quote(commands[i]);
            }
        }
        JSONArray list = null;
        if (urls != null) {
            list = new JSONArray();
            for (HashMap<String, String> url : urls) {
                JSONObject json = new JSONObject();
                for (Map.Entry<String, String> entry : url.entrySet()) {
                    if (entry == null || entry.getKey() == null || entry.getValue() == null)
                        continue;
                    try {
                        json.put(
                                entry.getKey(),
                                JSONObject.quote(entry.getValue())
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                list.put(json);
            }
        }
        return "{commands:" + (commands != null ? Arrays.asList(commands) : "[]")
                + ",urls:" + (list != null ? list : "[]") + "}";
    }
}
