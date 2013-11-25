package net.timbusproject.extractors.modules.linuxhardware;

import net.timbusproject.extractors.modules.Endpoint;
import net.timbusproject.extractors.modules.OperatingSystem;
import net.timbusproject.extractors.modules.contracts.IExtractor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: luismarques
 * Date: 12-11-2013
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class LinuxHardwareExtractor implements IExtractor{
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
        SSHManager instance = new SSHManager(
                endpoint.getUser(),
                "",
                endpoint.getHostname(),
                "",
                "/home/cmdesktop/.ssh/id_rsa"
        );
        Engine engine = new Engine();
        String result = engine.run(instance);


        return result;
    }
}
