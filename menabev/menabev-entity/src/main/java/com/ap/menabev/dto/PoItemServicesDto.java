package com.ap.menabev.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoItemServicesDto {

	private String itemServiceUuid;
	private String documentNumber;
	private String documentItem;
	private String pckgNo;
	private String lineNo;
	private String extLine;
	private Integer outlLevel;
	private String outlNo;
	private String outlInd;
	private String subpckgNo;
	private String service;
	private String servType;
	private String edition;
	private String sscItem;
	private String extServ;
	private Double quantity;
	private String baseUom;
	private String uomIso;
	private Double ovfTol;
	private String ovfUnlim;
	private Double priceUnit;
	private Double grPrice;
	private String fromLine;
	private String toLine;
	private String shortText;
	private String distrib;
	private String taxCode;
	private String taxjurcode;
	private Double netValue;
}
