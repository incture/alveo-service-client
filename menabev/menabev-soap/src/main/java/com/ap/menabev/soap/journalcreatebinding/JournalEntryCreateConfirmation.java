package com.ap.menabev.soap.journalcreatebinding;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JournalEntryCreateConfirmation {
	
	
	  @JsonProperty("FiscalYear") 
	    public String fiscalYear;
	    @JsonProperty("AccountingDocument") 
	    public String accountingDocument;
	    @JsonProperty("CompanyCode") 
	    public String companyCode;
	    @JsonProperty("Log") 
	    public SLog log;
	    @JsonProperty("MessageHeader") 
	    public MessageHeader messageHeader;
	    @JsonProperty("JournalEntryCreateConfirmation") 
	    public JournalEntryCreateConfirmation journalEntryCreateConfirmation;

}
