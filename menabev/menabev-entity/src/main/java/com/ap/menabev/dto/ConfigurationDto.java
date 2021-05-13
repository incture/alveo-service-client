package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ConfigurationDto 
{

	private String createdBy;
	
	private Long createdAt;
	
	private String updatedBy;
	
	private Long updatedAt;
	
	private String ocrSource;

	private String defaultTaxCode;
	
	private String companyCode;
	
	private String configurationId;
	
	private Integer maximumNoofUsers;
	
	private String version;
	
	
}
