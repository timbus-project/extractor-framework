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
package net.timbusproject.extractors.modules.local.temp;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 1/21/14
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestWrapper {

    @XmlElement
    public String[] commands;
    @XmlElement
    public String[] paths;
    @XmlElement
    public HashMap<String, String>[] urls;

    /*
    * [
    *   {
    *       "url":"jlhsoid:1435?hdushi",
    *       "method":"get",
    *       "port":"45",
    *       "params":"hdeoi=hud&gudha=hud",
    *       "accept":"application/json"
    *   },
    *   {
    *       "url":"dhlsahdoia",
    *       "method":"post",
    *       "body":".iuhoid",
    *       "content-type":"application/json",
    *       "accept":"application/json"
    *   }
    * ]
    * */

    @Override
    public String toString() {
        try {
            return new JSONObject("{commands:" + Arrays.asList(commands) + ",urls:" + Arrays.asList(urls) + "}").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject().toString();
    }

}
