package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Root{
    @JsonProperty("soap-env:Envelope") 
    public SoapEnvEnvelope soapEnvEnvelope;
}