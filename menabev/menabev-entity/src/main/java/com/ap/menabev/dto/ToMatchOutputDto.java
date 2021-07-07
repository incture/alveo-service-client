package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToMatchOutputDto {
	@JsonProperty("Vendor")
	private String vendor;
	@JsonProperty("InvoiceItem")
	private String invoiceItem;
	@JsonProperty("MessageType")
	private String messageType;
	@JsonProperty("MessageClass")
	private String messageClass;
	@JsonProperty("MessageNumber")
	private String messageNumber;
	@JsonProperty("MessageText")
	private String messageText;
	@JsonProperty("RequestId")
	private String requestId;
	@JsonProperty("ReferenceInvoiceItem")
	private String referenceInvoiceItem;
}
