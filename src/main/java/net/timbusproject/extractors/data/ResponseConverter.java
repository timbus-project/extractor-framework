package net.timbusproject.extractors.data;

import net.timbusproject.extractors.core.IExtractor;
import net.timbusproject.extractors.osgi.OSGiClient;
import net.timbusproject.extractors.pojo.RequestExtraction;
import net.timbusproject.extractors.pojo.ResponseJobsList;
import net.timbusproject.extractors.pojo.ResponseRequests;
import org.osgi.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 04-12-2013
 * Time: 16:16
 * To change this template use File | Settings | File Templates.
 */
public class ResponseConverter {

    @Autowired
    private RequestHandler requestHandler;

    @Qualifier("osgiClient")
    @Autowired
    private OSGiClient osgiClient;

    @Autowired
    LogService log;

    public ResponseConverter(){}

    /*Returns List of job info concerning the results from all the extractions in record (Spring batch jobs).
    It gets the information from the database through requestHandler*/
    public Object allRequestsList() {
        ResponseRequests response = new ResponseRequests();
        for (Long id : requestHandler.getKeySet()) {
            response.add(getRequestInfo(id));
        }
        return response;
    }

    //    Returns job info concerning one extraction request
    public Object getRequestInfo(long id) {
        if (!requestHandler.databaseContainsKey(id))
            return new ResponseJobsList();
        log.log(LogService.LOG_INFO, "packing job's info. id: " + id);
        ResponseJobsList list = new ResponseJobsList();
        list.setId(id);
        for (RequestExtraction request : requestHandler.get(id).extractions) {
            list.add(request.getJob());
        }
        return list;
    }

    public Object getExtractorsAsResponse() {
        return osgiClient.getExtractorsAsResponse();
    }

    public List<IExtractor> getExtractors() {
        return osgiClient.getExtractors();
    }
}




