package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToResult {
	@JsonProperty("ReferenceInvoiceNumber")
	private String referenceInvoiceNumber;
	@JsonProperty("InvDocNo")
	private String invDocNo;
	@JsonProperty("FiscYear")
	private String fiscYear;
	@JsonProperty("PstngDate")
	private String pstngDate;
	@JsonProperty("ReasonRev")
	private String reasonRev;
	@JsonProperty("DiscShift")
	private Boolean discShift;
	@JsonProperty("RefDocCategory")
	private String refDocCategory;

	

}
