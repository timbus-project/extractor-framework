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

import net.timbusproject.extractors.core.Parameter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.core.JobExecution;

import javax.xml.bind.annotation.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: lduarte
 * Date: 8/22/13
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlSeeAlso(Parameter.class)
@XmlAccessorType(XmlAccessType.NONE)
public class RequestExtraction {

   /* @XmlElement
    public String user;
    @XmlElement
    public String password;
    @XmlElement
    public String fqdn;
    @XmlElement
    public String knownHosts;
    @XmlElement
    public int port;
    @XmlElement
    public String privateKey;*/
    @XmlElement
    public String module;
//    @XmlAnyAttribute
    @XmlElement
    public HashMap<String, String> parameters;
    @XmlElement
    public RequestWrapper wrap;

    private JobExecution job;

    public RequestExtraction() {}

    /*public static RequestExtraction fromJSON(JSONObject jsonObject) throws JSONException {
        RequestExtraction extraction = new RequestExtraction();
        while (jsonObject.keys().hasNext()) {
            String key = jsonObject.keys().next().toString();
            switch (key) {
                case "user":        extraction.user = jsonObject.getString(key);        break;
                case "password":    extraction.password = jsonObject.getString(key);    break;
                case "fqdn":        extraction.fqdn = jsonObject.getString(key);        break;
                case "knownHosts":  extraction.knownHosts = jsonObject.getString(key);  break;
                case "port":        extraction.port = jsonObject.getInt(key);           break;
                case "privateKey":  extraction.privateKey = jsonObject.getString(key);  break;
                case "module":      extraction.module = jsonObject.getString(key);      break;
                default: break;
            }
        }
        return extraction;
    }*/

   /* public static RequestExtraction fromFile(InputStream file) {
        RequestExtraction extraction = new RequestExtraction();
        Scanner s = new Scanner(file);
        s.findInLine("fqdn:");
        extraction.fqdn = s.nextLine().trim().toLowerCase();
        while (s.hasNextLine()) {
            switch (s.next().trim().replace(":", "")) {
                case "user":        extraction.user = s.nextLine().trim();                          break;
                case "password":    extraction.password = s.nextLine().trim();                      break;
                case "knownHosts":  extraction.knownHosts = s.nextLine().trim();                    break;
                case "port":        extraction.port = Integer.parseInt(0 + s.nextLine().trim());    break;
                case "privateKey":  extraction.privateKey = s.nextLine().trim();                    break;
                default:            s.nextLine();                                                   break;
            }
        }
        extraction.user = "timbus-cms";
        extraction.port = 22;
        return extraction;
    }*/

    public void setJob(JobExecution job) {
        this.job = job;
    }

    public JobExecution getJob() {
        return job;
    }

    /*public void unsetPassword() {
        this.password = null;
    }*/

    /*@Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RequestExtraction other = (RequestExtraction) o;
        return fqdn != null && other.fqdn != null && fqdn.equals(other.fqdn) && port == other.port
                && user != null && other.user != null && user.equals(other.user);
    }*/

    @Override
    public String toString() {
//        return "RequestExtraction { " + user + "@" + fqdn + ":" + port + ", module: '" + module + "' }";
        return "RequestExtraction { module:" + module + ", " + parameters;
    }
}
