package com.ap.menabev.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class ApChartUIDto {

	private String rcvdOnFrom;
	private String rcvdOnTo;
	private List<String> vendorId;
	private String companyCode;
	private String currency;

}
