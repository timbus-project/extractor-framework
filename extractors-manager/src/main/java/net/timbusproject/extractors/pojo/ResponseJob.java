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

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.NONE)
public class ResponseJob {

    @XmlElement
    protected String id;
    @XmlElement
    protected String status;
    @XmlElement
    protected String startTime;
    @XmlElement
    protected String endTime;
    @XmlElement
    protected HashMap<String, String> input;
    @XmlElement
    protected String result;

    public ResponseJob() {}

    public ResponseJob(JobExecution job) {
        id = job.getJobId().toString();
        status = job.getStatus().toString();

        if (job.getStartTime() != null) startTime = job.getStartTime().toString();
        if (job.getEndTime() != null)   endTime = job.getEndTime().toString();
        input = new HashMap<>();
        for (Map.Entry<String, JobParameter> parameter : job.getJobParameters().getParameters().entrySet()) {
            switch (parameter.getKey()) {
                case "user": case "password": case "knownHosts": case "privateKey": case "timestamp": break;
                default: input.put(parameter.getKey(), String.valueOf(parameter.getValue())); break;
            }
        }
        if (job.getExecutionContext().containsKey("result"))
            result = job.getExecutionContext().getString("result");
    }

}
