package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SoapEnvEnvelope{
    @JsonProperty("soap-env:Body") 
    public SoapEnvBody soapEnvBody;
    @JsonProperty("soap-env:Header") 
    public String soapEnvHeader;
    @JsonProperty("xmlns:soap-env") 
    public String xmlnsSoapEnv;
}