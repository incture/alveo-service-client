package com.ap.menabev.serviceimpl;

import lombok.Data;

@Data
public class ProductTaxDto {
	
	
	private String taxCode;
	private String currency;
	private Double sumOfTax;
	private Double sumOfBaseRate;
	private String conditionType;

}
