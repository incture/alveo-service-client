package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SoapEnvBody{
    @JsonProperty("n0:JournalEntryBulkCreateConfirmation") 
    public N0JournalEntryBulkCreateConfirmation n0JournalEntryBulkCreateConfirmation;
}
