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
