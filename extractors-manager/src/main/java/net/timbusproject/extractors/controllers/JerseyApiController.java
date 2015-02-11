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
package net.timbusproject.extractors.controllers;

import net.timbusproject.extractors.data.RequestHandler;
import net.timbusproject.extractors.data.ResponseConverter;
import net.timbusproject.extractors.osgi.OSGiClient;
import net.timbusproject.extractors.pojo.RequestExtraction;
import net.timbusproject.extractors.pojo.RequestExtractionList;
import net.timbusproject.extractors.pojo.ResponseJob;
import org.osgi.service.log.LogService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class JerseyApiController {

    private static Hashtable<Long, RequestExtractionList> extractions;
    private static final Object LOCK = new Object();

    @Autowired
    private RequestHandler requestHandler;

    @Autowired
    ResponseConverter responseConverter;

    @Qualifier("jobLauncher")
    @Autowired
    private JobLauncher launcher;

    @Qualifier("extractionJob")
    @Autowired
    private Job job;

    @Autowired
    private LogService log;

    @Autowired
    private OSGiClient osgiClient;

    public JerseyApiController() {
        if (extractions == null) {
            extractions = new Hashtable<>();
        }
    }

    @GET
    @Path("/modules")
    public Response modulesList() {
        log.log(LogService.LOG_INFO, "modules list requested: " + osgiClient.getExtractors());
        return Response.ok(osgiClient.getExtractorsAsResponse()).build();
    }

    @GET
    @Path("/extractors")
    public Response extractorsList() {
        log.log(LogService.LOG_INFO, "extractors list requested");
        return modulesList();
    }

  /*  @POST
    @Path("/extract")
    public Response extract(RequestExtractionList extractionsList) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        log.log(LogService.LOG_INFO, "extraction requested" + extractionsList);
        long key;
        synchronized (LOCK) {
            key = extractions.keySet().size();
            extractions.put(key, extractionsList);
        }
        if (extractions.get(key).extractions.length == 1 && extractions.get(key).extractions[0].user == null && extractions.get(key).extractions[0].module != null && extractions.get(key).extractions[0].module.equals("Debian Software Extractor")) {
            // temporary fallback if clause
            extractions.get(key).extractions[0] = RequestExtraction.fromFile(getClass().getResourceAsStream("/default/debiansoftware"));
            extractions.get(key).extractions[0].module = "Debian Software Extractor";
        }
        for (RequestExtraction extraction : extractions.get(key).extractions) {
            if (extraction.wrap != null)
                log.log(LogService.LOG_INFO, extraction.wrap.toString());
            extraction.setJob(launcher.run(job,
                    new JobParametersBuilder()
                            .addString("user", extraction.user)
                            .addString("password", extraction.password != null && extraction.password.length() > 0 ? "" : null)
                            .addString("fqdn", extraction.fqdn) // newMachine (fqdn = fully qualified domain name)
                            .addString("knownHosts", extraction.knownHosts)
                            .addLong("port", (long) extraction.port) // newMachinePort
                            .addString("privateKey", extraction.privateKey)
                            .addString("wrapper", extraction.wrap != null ? extraction.wrap.toString() : null)
                            .addString("module", extraction.module) // selectedModule.name
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters()));
            extraction.getJob().getExecutionContext().putString("password", extraction.password);
            extraction.unsetPassword();
        }
        return Response.status(Response.Status.ACCEPTED)
                .entity(getRequestInfo(key))
                .location(UriBuilder.fromPath("/requests/{id}").build(key))
                .build();
    }*/

    @POST
    @Path("/extract")
    public Response extract(RequestExtractionList extractionsList, @Context HttpServletRequest req) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {

//        System.out.println("REQUEST TYPE REQUESTED: " + extractionsList.getCallbackInfo().originRequestType);
        if (extractionsList.getCallbackInfo() != null && extractionsList.getCallbackInfo().endpointPath != null && extractionsList.getCallbackInfo().endpointPort != null) {
            String endpoint = req.getRemoteAddr() + ":" + extractionsList.getCallbackInfo().endpointPort;
            if (extractionsList.getCallbackInfo().endpointPath.startsWith("/"))
                endpoint += extractionsList.getCallbackInfo().endpointPath;
            else
                endpoint += "/" + extractionsList.getCallbackInfo().endpointPath;
            extractionsList.callback.setOriginEndpoint(endpoint);
            if (extractionsList.getCallbackInfo().originRequestType != null) {
                if (extractionsList.getCallbackInfo().originRequestType.trim().toLowerCase().equals("post")|| extractionsList.getCallbackInfo().originRequestType.trim().toLowerCase().equals("get")) {
                    extractionsList.getCallbackInfo().setFinalOriginRequestType(extractionsList.getCallbackInfo().originRequestType.toLowerCase());
                } else {
                    log.log(LogService.LOG_INFO, "Invalid HTTP Request type for origin host callback. Defaulting to GET type");
                    extractionsList.getCallbackInfo().setFinalOriginRequestType("get");
                }
            } else {
                log.log(LogService.LOG_INFO, "No HTTP Request type for origin host callback provided. Defaulting to GET type");
                extractionsList.getCallbackInfo().setFinalOriginRequestType("get");
            }
            if (extractionsList.getCallbackInfo().requestType != null) {
                if (extractionsList.getCallbackInfo().requestType.trim().toLowerCase().equals("post")|| extractionsList.getCallbackInfo().requestType.trim().toLowerCase().equals("get")) {
                    extractionsList.getCallbackInfo().setFinalRequestType(extractionsList.getCallbackInfo().requestType.toLowerCase());
                } else {
                    log.log(LogService.LOG_INFO, "Invalid HTTP Request type for callback. Defaulting to GET type");
                    extractionsList.getCallbackInfo().setFinalRequestType("get");
                }
            } else {
                log.log(LogService.LOG_INFO, "No HTTP Request type for callback provided. Defaulting to GET type");
                extractionsList.getCallbackInfo().setFinalRequestType("get");
            }
        }
        long key = requestHandler.requestExtraction(extractionsList);
        return Response.status(Response.Status.ACCEPTED)
                .entity(responseConverter.getRequestInfo(key))
                .location(UriBuilder.fromPath("/requests/{id}").build(key))
                .build();
    }

/*
    @POST
    @Path("/extract")
    public Response extract(@FormParam("extractions") String extractionsList) throws JSONException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        log.log(LogService.LOG_INFO, "extraction requested" + extractionsList);
        return extract(RequestExtractionList.fromJSON(new JSONArray(extractionsList)));
    }
*/

/*    @GET
    @Path("/extract/fallback")
    public Response extractFallback() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        log.log(LogService.LOG_INFO, "starting fallback extraction");
        RequestExtraction extraction = RequestExtraction.fromFile(getClass().getResourceAsStream("/default/debiansoftware"));
        extraction.module = "Debian Software Extractor";
        RequestExtractionList extractionsList = new RequestExtractionList();
        extractionsList.extractions = new RequestExtraction[]{extraction};
        return extract(extractionsList);
    }*/


    //UNCOMMENT THIS!! 5 MARÇO 2014
    /*@GET
    @Path("/extract/fallback")
    public Response extractFallback() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        log.log(LogService.LOG_INFO, "starting fallback extraction");
        long key = requestHandler.requestExtractionFallback();
        return Response.status(Response.Status.ACCEPTED)
                .entity(responseConverter.getRequestInfo(key))
                .location(UriBuilder.fromPath("/requests/{id}").build(key))
                .build();
    }*/

/*    @GET
    @Path("/requests")
    public Response allRequestsList() throws NoSuchJobException {
        log.log(LogService.LOG_INFO, "request list requested");
        List<ResponseJobsList> response = new ArrayList<>();
        for (Long id : extractions.keySet()) {
            response.add(getRequestInfo(id));
        }
        return Response.ok(response).build();
    }*/

    @GET
    @Path("/requests")
    public Response allRequestsList() throws NoSuchJobException {
        return Response.ok(responseConverter.allRequestsList()).build();
    }

    /*@GET
    @Path("/requests/{id}")
    public Response requestInfo(@PathParam("id") long id) {
        log.log(LogService.LOG_INFO, "request #" + id + " requested");
        return Response.ok(getRequestInfo(id)).build();
    }*/

    @GET
    @Path("/testcallback")
    public Response testCallbackGet() {
        log.log(LogService.LOG_INFO, "JerseyApiController: received http request from callback. extractions finished");
        return Response.ok().build();
    }

    @GET
    @Path("/requests/{id}")
    public Response requestInfo(@PathParam("id") long id) {
        log.log(LogService.LOG_INFO, MessageFormat.format("id: {0}", id));
        return Response.ok(responseConverter.getRequestInfo(id)).build();
    }

/*    private ResponseJobsList getRequestInfo(long id) {
        ResponseJobsList list = new ResponseJobsList();
        if (!extractions.containsKey(id)) {
            return list;
        }
        log.log(LogService.LOG_INFO, "packing request #" + id + " info");
        list.setId(id);
        for (RequestExtraction request : extractions.get(id).extractions) {
            list.add(request.getJob());
        }
        return list;
    }*/

    @GET
    @Path("/requests/{id}/finished")
    public Response requestInfoFinish(@PathParam("id") long id) {
        log.log(LogService.LOG_INFO, "finish polling received for extraction # " + id);
        if (!extractions.containsKey(id))
            return Response.status(Response.Status.NOT_FOUND).build();
        for (RequestExtraction extraction : extractions.get(id).extractions) {
            if (extraction.getJob() != null && extraction.getJob().getStatus().equals(BatchStatus.FAILED)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(false).build();
            } else if (extraction.getJob() == null || !extraction.getJob().getStatus().equals(BatchStatus.COMPLETED)) {
                return Response.status(Response.Status.CREATED).entity(false).build();
            }
        }
        return Response.ok(true).build();
    }

    private ResponseJob getJobInfo(JobExecution execution) {
        if (execution == null)
            return new ResponseJob();
        log.log(LogService.LOG_INFO, "packing job's info. id: " + execution.getJobId());
        return new ResponseJob(execution);
    }

}
