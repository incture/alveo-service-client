package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Item{
    @JsonProperty("TypeID") 
    public String typeID;
    @JsonProperty("SeverityCode") 
    public int severityCode;
    @JsonProperty("Note") 
    public String note;
    @JsonProperty("WebURI") 
    public String webURI;
}