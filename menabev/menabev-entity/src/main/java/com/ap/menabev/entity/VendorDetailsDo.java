package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "VENDOR_DETAILS")
@ToString
@Getter
@Setter
public @Data class VendorDetailsDo
{
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;

	@Id
	@Column(name = "VENDOR_ID")
	private String vendorId;
	
	@Column(name = "VENDOR_ID_PAYLOAD")
	private String vendor_id_payload;
	
	
	@Column(name = "COMPANY_CODE")
	private String companyCode;
	
	
	@Column(name = "AUTO_POSTING")
	private Boolean autoPosting;
	
	@Column(name = "PARTIAL_POSTING")
	private Boolean partialPosting;
	
	@Column(name = "AUTO_REJECTION")
	private Boolean autoRejection;
		
	@Column(name = "CONFIGURATION_ID")
	private String configurationId;
	
}

