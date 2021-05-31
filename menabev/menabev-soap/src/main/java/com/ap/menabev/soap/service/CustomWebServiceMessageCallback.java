package com.ap.menabev.soap.service;

import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringSource;

public class CustomWebServiceMessageCallback implements WebServiceMessageCallback {

	@Override
	public void doWithMessage(WebServiceMessage webServiceMessage) throws IOException, TransformerException {
		SoapMessage soapMessage = (SoapMessage) webServiceMessage;
		SoapHeader header = soapMessage.getSoapHeader();
		StringSource headerSource = new StringSource(
				"<additional-header-element>HEADER_TEXT</additional-header-element>");
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(headerSource, header.getResult());

		
	}

}
