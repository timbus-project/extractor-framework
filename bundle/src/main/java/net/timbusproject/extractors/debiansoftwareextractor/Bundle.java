package net.timbusproject.extractors.debiansoftwareextractor;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 6/30/14
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bundle extends DebianSoftwareExtractor {

    @Autowired
    public Bundle(BundleContext bundleContext) {
        super(bundleContext);
    }

}
