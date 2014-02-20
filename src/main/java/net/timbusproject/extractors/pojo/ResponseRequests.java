package net.timbusproject.extractors.pojo;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jorge
 * Date: 10/15/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "requests")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(ResponseJobsList.class)
public class ResponseRequests {

    @XmlElement
    protected String id;

    @XmlElement
    protected List<Object> jobs = new ArrayList<>();

    public void setId(long id) {
        this.id = String.valueOf(id);
    }

    public void add(Object jobsList) {
        if (jobsList instanceof ResponseJobsList)
            jobs.add(jobsList);
    }

}
