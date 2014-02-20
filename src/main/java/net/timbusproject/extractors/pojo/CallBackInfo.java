package net.timbusproject.extractors.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: miguel
 * Date: 22-11-2013
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class CallBackInfo {

    @XmlElement
    public String[] mail;
/*    @XmlElement
    public String requestType;*/
    @XmlElement
    public String[] endpoint;


    public String[] getMails(){
        return mail;
    }

    public String[] getEndPoints(){
        return endpoint;
    }

}
