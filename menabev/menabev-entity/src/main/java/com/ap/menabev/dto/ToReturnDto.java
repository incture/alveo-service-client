package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToReturnDto {
	@JsonProperty("__metadata")
	private Object metadata;
	@JsonProperty("InvoiceReferenceNumber")
	private String invoiceReferenceNumber;
	@JsonProperty("type")
	private String Type;
	@JsonProperty("Id")
	private String id;
	@JsonProperty("Number")
	private String number;
	@JsonProperty("Message")
	private String message;
	@JsonProperty("LogNo")
	private String logNo;
	@JsonProperty("LogMsgNo")
	private String logMsgNo;
	@JsonProperty("MessageV1")
	private String messageV1;
	@JsonProperty("MessageV2")
	private String messageV2;
	@JsonProperty("MessageV3")
	private String messageV3;
	@JsonProperty("MessageV4")
	private String messageV4;
	@JsonProperty("Parameter")
	private String parameter;
	@JsonProperty("Row")
	private Integer row;
	@JsonProperty("Field")
	private String field;
	@JsonProperty("System")
	private String system;

}
