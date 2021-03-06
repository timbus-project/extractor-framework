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
package net.timbusproject.extractors.core;

import java.util.HashMap;

import org.osgi.framework.Version;

/**
 * Interface for extractors to implement.
 *
 * @author Luís Duarte
 * @author Jorge Simões
 * @since 0.0.1-RELEASE
 */
public interface IExtractor {

    /**
     * Returns bundle's name.
     *
     * @return name
     */
    public java.lang.String getName();

    /**
     * Returns bundle's symbolic name.
     *
     * @return symbolic name
     */
    public java.lang.String getSymbolicName();

    /**
     * Returns bundle's version.
     *
     * @return version
     * @see <a href="http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/Version.html">Version</a>
     */
    public Version getVersion();

    /**
     * Returns the list of supported operating systems.
     *
     * @return list of supported {@link OperatingSystem}s
     */
    public java.util.EnumSet<OperatingSystem> getSupportedOperatingSystems();

    public HashMap<String, Parameter> getParameters();
    /**
     * Extraction request.
     *
     * @param endpoint    the endpoint to which the extractor will connect to
     * @return extraction result
     * @throws java.lang.Exception
     */
    public java.lang.String extract(Endpoint endpoint, boolean useCache) throws java.lang.Exception;

}
