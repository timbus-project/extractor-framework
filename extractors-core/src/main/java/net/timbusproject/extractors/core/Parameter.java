package net.timbusproject.extractors.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by miguel on 04-03-2014.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Parameter{

    @XmlElement
    public String value;
    @XmlElement
    public boolean hidden;
    @XmlElement
    public ParameterType parameterType;
    @XmlElement
    public boolean mandatory;

    public Parameter(){
        this.hidden = false;
        this.mandatory = true;
        this.parameterType = ParameterType.STRING;
    }

    public Parameter(boolean hidden){
        this.hidden = hidden;
        this.mandatory = true;
        this.parameterType = ParameterType.STRING;
    }

    public Parameter(boolean hidden, boolean mandatory){
        this.hidden = hidden;
        this.mandatory = mandatory;
        this.parameterType = ParameterType.STRING;
    }

    public Parameter(boolean hidden, ParameterType parameterType){
        this.hidden = hidden;
        this.mandatory = true;
        this.parameterType = parameterType;
    }

    public Parameter(boolean hidden, boolean mandatory, ParameterType parameterType){
        this.hidden = hidden;
        this.mandatory = mandatory;
        this.parameterType = parameterType;
    }



    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public void setObjectType(){
        this.parameterType = ParameterType.OBJECT;
    }

    public void setStringType(){
        this.parameterType = ParameterType.STRING;
    }

    public void setNumberType(){
        this.parameterType = ParameterType.NUMBER;
    }


    public boolean isHidden(){
        return hidden;
    }

    public boolean isMandatory(){
        return mandatory;
    }

    public ParameterType getParameterType(){
        return parameterType;
    }

    public void setMandatory(boolean is){
        mandatory = is;
    }
}
