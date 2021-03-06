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

import com.fasterxml.uuid.Generators;
import net.timbusproject.extractors.core.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by miguel on 16-01-2014.
 */
public class SSHExtractor implements IExtractor {


    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private LogService log;

    @Override
    public String getName() {
        return bundleContext.getBundle().getHeaders().get("Bundle-Name");
    }

    @Override
    public String getSymbolicName() {
        return bundleContext.getBundle().getSymbolicName();
    }

    @Override
    public Version getVersion() {
        return bundleContext.getBundle().getVersion();
    }

    @Override
    public EnumSet<OperatingSystem> getSupportedOperatingSystems() {
        return EnumSet.allOf(OperatingSystem.class);
    }

    @Override
    public HashMap<String, Parameter> getParameters() {
        HashMap<String, Parameter> parameters = new HashMap<>();
        parameters.put("user", new Parameter(false));
        parameters.put("password", new Parameter(true));
        parameters.put("port", new Parameter(false, ParameterType.NUMBER));
        parameters.put("fqdn", new Parameter(false));
        parameters.put("commands", new Parameter(false, ParameterType.ARRAY));
        return parameters;
    }

    @Override
    public String extract(Endpoint endpoint, boolean useCache) throws Exception {
        Engine engine = new Engine();

        SSHManager instance = new SSHManager(
                endpoint.getProperty("user"),
                endpoint.getProperty("password"),
                endpoint.getProperty("fqdn"),
                endpoint.getProperty("knownHosts"),
                endpoint.hasProperty("port") ? Integer.parseInt(endpoint.getProperty("port")) : Endpoint.DEFAULT_SSH_PORT,
                endpoint.getProperty("privateKey")
        );

        JSONArray commandsJsonObject = null;
        try {
            commandsJsonObject = new JSONArray(endpoint.getProperty("commands"));
        } catch (JSONException e) {
            System.out.println("Commands JSON List sent is not valid");
        }

        if (commandsJsonObject != null) {
            if (commandsJsonObject.length() != 0) {
                JSONArray responseArray = new JSONArray();
                for (int i = 0; i < commandsJsonObject.length(); i++) {
                    JSONObject toPut = engine.run(instance, (String) commandsJsonObject.getString(i));
                    responseArray.put(toPut);
                }
                return new JSONObject().put("extractor", getName())
                        .put("format", new JSONObject().put("multiple", true))
                        .put("result", responseArray)
                        .put("uuid", Generators.timeBasedGenerator().generate()).toString();
            } else
                throw new IllegalArgumentException("No commands provided");

        } else
            throw new IllegalArgumentException("No commands provided");
    }
}
