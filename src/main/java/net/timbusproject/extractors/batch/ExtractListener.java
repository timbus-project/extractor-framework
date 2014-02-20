package net.timbusproject.extractors.batch;

import net.timbusproject.extractors.data.RequestHandler;
import org.osgi.service.log.LogService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 22-11-2013
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public class ExtractListener implements StepExecutionListener {

    @Autowired
    private LogService log;
    @Autowired
    RequestHandler requestHandler;

    @Override
    public void beforeStep(StepExecution stepExecution) {}

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long key = stepExecution.getJobParameters().getLong("requestId");
        requestHandler.finish(key);
        return ExitStatus.COMPLETED;
    }

}
