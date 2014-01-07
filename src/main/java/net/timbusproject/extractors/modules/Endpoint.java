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
package net.timbusproject.extractors.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 10/21/13
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public final class Endpoint implements Serializable {

    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    public static final int DEFAULT_SSH_PORT = 22;
    private static final long serialVersionUID = 3853824563966034317L;

    private final String fqdn;
    private final Properties properties;

    public Endpoint(String fqdn) {
        if (fqdn == null || fqdn.length() == 0) throw new IllegalArgumentException();
        this.fqdn = fqdn;
        properties = new Properties();
    }

    public static Endpoint fromFile(File file) throws FileNotFoundException { return fromFile(new Scanner(file)); }
    public static Endpoint fromFile(InputStream file) { return fromFile(new Scanner(file)); }
    private static Endpoint fromFile(Scanner s) {
        s.findInLine("fqdn:");
        Endpoint endpoint = new Endpoint(s.nextLine().trim().toLowerCase());
        while (s.hasNextLine()) {
            endpoint.setProperty(s.next().trim().replace(":", ""), s.nextLine().trim());
        }
        return endpoint;
    }

    public String setProperty(String key, String value) {
        Object o;
        if (key == null || key.length() == 0)           o = null;
        else if (value == null || value.length() == 0)  o = properties.get(key);
        else                                            o = properties.setProperty(key, value);
        return o != null ? String.valueOf(o) : null;
    }

    public boolean hasProperty(String key) {
        return key != null && properties.containsKey(key);
    }

    public String getProperty(String key) {
        return key != null ? properties.getProperty(key) : null;
    }

    public String getFQDN() {
        return fqdn;
    }

    @Override
    public String toString() {
        return fqdn + properties;
    }

}
