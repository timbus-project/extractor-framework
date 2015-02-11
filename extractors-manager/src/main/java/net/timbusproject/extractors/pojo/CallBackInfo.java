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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class CallBackInfo {

    @XmlElement
    public String[] mail;
/*    @XmlElement
    public String requestType;*/
    @XmlElement
    public String[] endpoint;
    @XmlElement
    public String requestType;

    @XmlElement
    public Integer endpointPort;
    @XmlElement
    public String endpointPath;
    @XmlElement
    public String originRequestType;

    private String originEndpoint;
    private String finalOriginRequestType;
    private String finalRequestType;



    public String[] getMails(){
        return mail;
    }

    public String[] getEndPoints(){
        return endpoint;
    }

    public void setOriginEndpoint(String originEndpoint){
        this.originEndpoint = originEndpoint;
    }

    public String getOriginEndpoint() {
        return originEndpoint;
    }

    public String getFinalOriginRequestType() {
        return finalOriginRequestType;
    }

    public void setFinalOriginRequestType(String finalOriginRequestType) {
        this.finalOriginRequestType = finalOriginRequestType;
    }

    public String getFinalRequestType() {
        return finalRequestType;
    }

    public void setFinalRequestType(String finalRequestType) {
        this.finalRequestType = finalRequestType;
    }
}
