package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class N0JournalEntryBulkCreateConfirmation{
    @JsonProperty("xmlns:n0") 
    public String xmlnsN0;
    @JsonProperty("xmlns:prx") 
    public String xmlnsPrx;
    @JsonProperty("Log") 
    public String log;
    @JsonProperty("MessageHeader") 
    public MessageHeader messageHeader;
    @JsonProperty("JournalEntryCreateConfirmation") 
    public JournalEntryCreateConfirmation journalEntryCreateConfirmation;
}