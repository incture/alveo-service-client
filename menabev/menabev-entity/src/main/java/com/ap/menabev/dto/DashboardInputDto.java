package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DashboardInputDto {

	private Long rcvdOnFrom;
	private Long rcvdOnTo;
	private String companyCode;
	private List<String> vendorId;
	private String currency;
	
}
