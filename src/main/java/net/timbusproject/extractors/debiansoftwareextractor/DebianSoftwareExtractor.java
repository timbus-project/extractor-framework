/**
 * Copyright (c) 2014, Caixa Magica Software Lda (CMS).
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

import com.fasterxml.uuid.Generators;
import net.timbusproject.extractors.core.*;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;

public class DebianSoftwareExtractor implements IExtractor {

    static final String formatUUID = "d17250e8-af6e-5b84-8fab-404d5ecee47f";

    private BundleContext bundleContext;
    private final Logger log = LoggerFactory.getLogger(DebianSoftwareExtractor.class);

    public DebianSoftwareExtractor(BundleContext bundleContext) { this.bundleContext = bundleContext; }

    @Override
    public String getName() {
        return bundleContext != null ? bundleContext.getBundle().getHeaders().get("Bundle-Name") : getClass().getSimpleName();
    }

    @Override
    public String getSymbolicName() {
        return bundleContext != null ? bundleContext.getBundle().getSymbolicName() : getClass().getCanonicalName();
    }

    @Override
    public Version getVersion() {
        return bundleContext != null ? bundleContext.getBundle().getVersion() : Version.emptyVersion;
    }

    @Override
    public EnumSet<OperatingSystem> getSupportedOperatingSystems() {
        return EnumSet.of(OperatingSystem.LINUX);
    }

    @Override
    public HashMap<String, Parameter> getParameters() {
        HashMap<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("fqdn", new Parameter(false, true));
        parameters.put("port", new Parameter(false, false, ParameterType.NUMBER));
        parameters.put("user", new Parameter(false, true));
        parameters.put("password", new Parameter(true, false));
        try {
            parameters.put("privateKey", new Parameter(true, false, ParameterType.valueOf("FILE")));
        } catch (IllegalArgumentException ignored) {}
        return parameters;
    }

    @Override
    public String extract(Endpoint endpoint, boolean b) throws Exception {
        SSHManager sshManager = new SSHManager(
                endpoint.getProperty("user"),
                endpoint.getProperty("fqdn"),
                endpoint.hasProperty("port") ? Integer.parseInt(endpoint.getProperty("port")) : Endpoint.DEFAULT_SSH_PORT,
                endpoint.getProperty("privateKey")
        );
        sshManager.setPassword(endpoint.getProperty("password"));
        Engine engine = new Engine(sshManager);
        JSONObject extraction = engine.run(Engine.Scope.INSTALLED_PACKAGES);
        return new JSONObject().put("extractor", getName())
                .put("format", new JSONObject().put("id", formatUUID).put("multiple", false))
                .put("uuid", Generators.timeBasedGenerator().generate())
                .put("result", extraction).toString(2);
    }

}

