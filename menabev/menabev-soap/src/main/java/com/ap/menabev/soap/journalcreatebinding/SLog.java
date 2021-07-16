package com.ap.menabev.soap.journalcreatebinding;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SLog {
	
	    @JsonProperty("Item") 
	    public List<Item> item;
	    @JsonProperty("MaximumLogItemSeverityCode") 
	    public int maximumLogItemSeverityCode;

}



