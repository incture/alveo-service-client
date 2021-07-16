package com.ap.menabev.soap.journalcreatebinding;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MessageHeader {
	
	@JsonProperty("CreationDateTime") 
    public Date creationDateTime;
    @JsonProperty("UUID") 
    public String uUID;

}
