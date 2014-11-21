package net.timbusproject.extractors.data;

import net.timbusproject.extractors.core.IExtractor;
import net.timbusproject.extractors.core.Parameter;
import net.timbusproject.extractors.core.ParameterType;
import net.timbusproject.extractors.osgi.OSGiClient;
import net.timbusproject.extractors.pojo.CallBack;
import net.timbusproject.extractors.pojo.RequestExtraction;
import net.timbusproject.extractors.pojo.RequestExtractionList;
import org.codehaus.jettison.json.JSONArray;
import org.osgi.service.log.LogService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 02-12-2013
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class RequestHandler {

    private Database database;

    @Qualifier("jobExplorer")
    @Autowired
    private JobExplorer explorer;

    @Qualifier("jobLauncher")
    @Autowired
    private JobLauncher launcher;

    @Qualifier("extractionJob")
    @Autowired
    private Job job;

    @Qualifier("osgiClient")
    @Autowired
    private OSGiClient client;


    @Autowired
    private LogService log;

    public RequestHandler() {
        database = new Database();
    }

    //Request extraction method. It creates new jobs according to the information provided through "extractionList"
    public long requestExtraction(RequestExtractionList extractionsList) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        long key = database.add(extractionsList);
        database.get(key).setSemaphore();

        /*if (database.get(key).extractions.length == 1 && database.get(key).extractions[0].user == null && database.get(key).extractions[0].module != null && database.get(key).extractions[0].module.equals("Debian Software Extractor")) {
            // temporary fallback if clause
            database.get(key).extractions[0] = RequestExtraction.fromFile(getClass().getResourceAsStream("/default/debiansoftware"));
            database.get(key).extractions[0].module = "Debian Software Extractor";
        }*/

        for (RequestExtraction req : extractionsList.extractions) {
            IExtractor extractor = client.getExtractorByName(req.module);
            if (extractor == null) {
                throw new IllegalArgumentException("Extractor module does not exist");
            }
            HashMap<String, Parameter> extractorParams = extractor.getParameters();
            if (req.parameters != null) {
                for (String s : extractorParams.keySet()) {
                    if (extractorParams.get(s).isMandatory() && !req.parameters.containsKey(s))
                        throw new IllegalArgumentException("The parameter " + s + " is mandatory");
                    if(req.parameters.containsKey(s)){
                        if (extractorParams.get(s).getParameterType() == ParameterType.ARRAY && !req.parameters.get(s).startsWith("["))
                            throw new IllegalArgumentException("Parameters " + s + " is supposed to be a JSON Array");
                        if (extractorParams.get(s).getParameterType() == ParameterType.OBJECT && !req.parameters.get(s).startsWith("{"))
                            throw new IllegalArgumentException("Parameters " + s + " is supposed to be a JSON Object");
                    }
                }
            } else
                throw new IllegalArgumentException("No parameters were sent");
            JobParametersBuilder parametersBuilder = new JobParametersBuilder()
                    .addString("module", req.module) // selectedModule.name
                    .addLong("timestamp", System.currentTimeMillis())
                    .addLong("requestId", key);
//                    .addString("user", req.user) // newMachine (fqdn = fully qualified domain name)
//                    .addString("password", req.password != null ? "" : null)
//                    .addString("fqdn", req.fqdn) // newMachine (fqdn = fully qualified domain name)
//                    .addString("knownHosts", req.knownHosts)       //THIS
//                    .addLong("port", (long) req.port) // newMachinePort
//                    .addString("privateKey", req.privateKey) //THIS

            ArrayList<String> hiddenParameters = new ArrayList<>();
            for (String s : req.parameters.keySet()) {
                if (extractorParams.containsKey(s) && extractorParams.get(s).isHidden()) {
                    hiddenParameters.add(s);
                    continue;
                }
                if (extractorParams.containsKey(s) && extractorParams.get(s).getParameterType() == ParameterType.NUMBER)
                    parametersBuilder.addLong(s, Long.valueOf(req.parameters.get(s)));
                else
                    parametersBuilder.addString(s, req.parameters.get(s));
            }

            if (hiddenParameters.size() != 0)
                parametersBuilder.addString("hiddenFields", new JSONArray(hiddenParameters).toString());

            req.setJob(launcher.run(job, parametersBuilder.toJobParameters()));

            for (String s : hiddenParameters) {
                if (extractorParams.get(s).getParameterType() == ParameterType.NUMBER)
                    req.getJob().getExecutionContext().putLong(s, Long.valueOf(req.parameters.get(s)));
                else
                    req.getJob().getExecutionContext().putString(s, req.parameters.get(s));
                req.parameters.remove(s);
            }
//            req.getJob().getExecutionContext().putString("password", req.password);
//            req.unsetPassword();
        }
        return key;
    }

    //Request Extraction test with CaixaMagica local machine
    /*public long requestExtractionFallback() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        RequestExtraction extraction = new RequestExtraction();
        extraction.user = "timbus-cms";
        extraction.fqdn = "testbed.timbusproject.net";
        extraction.port = 22;
        extraction.module = "Debian Software Extractor";
        RequestExtractionList extractionsList = new RequestExtractionList();
        extractionsList.extractions = new RequestExtraction[]{extraction};
        return requestExtraction(extractionsList);
    }*/

    /*method invoked by Spring batch job listener's "afterStep" method. Whenever a job finishes, this method checks
    wether the request is also finished in order to perform callback*/
    public void finish(long key, boolean success) {
        if (database.get(key).getSemaphoreAvailablePermits() > 1)
            try {
                database.get(key).acquireSemaphore();
            } catch (InterruptedException e) {
            }
        else {
            try {
                new CallBack().doCallBack(key, database.get(key), success);
            } catch (URISyntaxException e) {
            } catch (IOException e) {
            }
        }
    }

    //self explanatory
    public boolean databaseContainsKey(long key) {
        return database.containsKey(key);
    }

    //self explanatory
    public Set<Long> getKeySet() {
        return database.getKeySet();
    }

    //self explanatory
    public RequestExtractionList get(long id) {
        return database.get(id);
    }
}

