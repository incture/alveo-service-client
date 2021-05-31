package com.ap.menabev.soap.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationBulkMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;
import com.ap.menabev.soap.service.JournalEntryService;

@RestController
@RequestMapping("/soap")
public class JournalEntryController {
	@Autowired
	JournalEntryService journalEntryService;
	
	@PostMapping("/postJournalEntry")
	public JournalEntryCreateConfirmationBulkMessage postJournalEntryToSap(@RequestBody JournalEntryCreateRequestBulkMessage requestMessage) throws URISyntaxException, IOException{
		System.out.println("ReQUEST PAyloaD sOAP::::::"+requestMessage);
		return journalEntryService.postJournalEntryToSap(requestMessage);
	}
	
	

}
