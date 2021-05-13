package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CONFIGURATION")
@ToString
@Getter
@Setter
public class ConfigurationDo 
{

	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	
	@Column(name="VERSION")
	private String version;
	
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;
	
	@Column(name = "COUNTRY_CODE")
	private String companyCode;
	
	@Column(name = "OCR_SOURCE")
	private String ocrSource;
	
	@Column(name = "DEFAULT_TAX_CODE")
	private String defaultTaxCode;
	
	@Id
	@Column(name = "CONFIGURATION_ID")
	private String configurationId;
	
	@Column(name = "MAXIMUM_NUMBER_OF_USERS")
	private Integer maximumNoofUsers;
	
}
