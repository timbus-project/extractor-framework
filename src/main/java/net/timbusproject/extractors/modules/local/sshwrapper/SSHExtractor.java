package net.timbusproject.extractors.modules.local.sshwrapper;

import net.timbusproject.extractors.modules.Endpoint;
import net.timbusproject.extractors.modules.OperatingSystem;
import net.timbusproject.extractors.modules.contracts.IExtractor;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;

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
        return EnumSet.of(OperatingSystem.LINUX);
    }

    @Override
    public String extract(Endpoint endpoint) throws Exception {
        JSONArray responseArray = new JSONArray();
        Engine engine = new Engine();

        SSHManager instance = new SSHManager(
                endpoint.getProperty("user"),
                endpoint.getProperty("password"),
                endpoint.getFQDN(),
                endpoint.getProperty("knownHosts"),
                endpoint.hasProperty("port") ? Integer.parseInt(endpoint.getProperty("port")) : Endpoint.DEFAULT_SSH_PORT,
                endpoint.getProperty("privateKey")
        );
        if (endpoint.getProperty("commands") != null) {
            JSONArray receivedCommandsArray = new JSONArray(endpoint.getProperty("commands"));
            for (int i = 0; i < receivedCommandsArray.length(); i++) {
                responseArray.put(engine.run(instance, (String)receivedCommandsArray.getJSONObject(i).get("command")));
            }
        }
        if (endpoint.getProperty("paths") != null) {
            JSONArray receivedPathsArray = new JSONArray(endpoint.getProperty("paths"));
            for (int i = 0; i < receivedPathsArray.length(); i++) {
                responseArray.put(engine.runWithPath(instance, (String)receivedPathsArray.getJSONObject(i).get("path")));
            }
        }
        return responseArray.toString();
    }
}
