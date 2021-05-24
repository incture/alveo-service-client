package com.ap.menabev.soap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationBulkMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateConfirmationMessage;
import com.ap.menabev.soap.journalcreatebinding.JournalEntryCreateRequestBulkMessage;


@Service
public class JournalEntryService {

	@Autowired
	private Jaxb2Marshaller marshaller;

	private WebServiceTemplate template;

	public JournalEntryCreateConfirmationBulkMessage postJournalEntryToSap(JournalEntryCreateRequestBulkMessage requestMessage) {
		template = new WebServiceTemplate(marshaller);
		JournalEntryCreateConfirmationBulkMessage journalEntryCreateConfirmationMessage = (JournalEntryCreateConfirmationBulkMessage) template
				.marshalSendAndReceive(
						"" + "https://192.169.34.58:44300/sap/bc/srt/xip/sap/"
								+ "journalentrycreaterequestconfi/100/journalcreateservice/journalcreatebinding",
						requestMessage);
       	return journalEntryCreateConfirmationMessage;
	}
}
