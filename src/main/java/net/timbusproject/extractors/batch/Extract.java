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
package net.timbusproject.extractors.batch;

import net.timbusproject.extractors.core.Endpoint;
import net.timbusproject.extractors.core.IExtractor;
import net.timbusproject.extractors.osgi.OSGiClient;
import org.codehaus.jettison.json.JSONObject;
import org.osgi.service.log.LogService;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created with IntelliJ IDEA.
 * User: lduarte
 * Date: 8/2/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class Extract implements Tasklet {

    @Autowired
    private LogService log;

    @Qualifier("osgiClient")
    @Autowired
    private OSGiClient client;

    public Extract() {}

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        JobParameters parameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
        long jobId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();

        IExtractor extractor = client.getExtractorByName(parameters.getString("module"));
        if (extractor == null) {
            throw new IllegalArgumentException("Extractor module does not exist");
        }

        Endpoint endpoint = new Endpoint(parameters.getString("fqdn"));
        endpoint.setProperty("user", parameters.getString("user"));
        endpoint.setProperty("knownHosts", parameters.getString("knownHosts"));
        endpoint.setProperty("port", String.valueOf(parameters.getLong("port")));
        endpoint.setProperty("privateKey", parameters.getString("privateKey"));
        if (parameters.getString("wrapper") != null)
            endpoint.setProperty("wrapper", parameters.getString("wrapper"));
        //noinspection StatementWithEmptyBody
        while (parameters.getString("password") != null && !chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().containsKey("password"));
        log.log(LogService.LOG_INFO, "job #" + jobId + ": " + endpoint.toString());
        if (chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("password") != null) {
            endpoint.setProperty("password", String.valueOf(chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().remove("password")));
        }

        String result = extractor.extract(endpoint, false);
        log.log(LogService.LOG_INFO, "extracted result: " + result);
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("result", result);

        return RepeatStatus.FINISHED;
    }
}