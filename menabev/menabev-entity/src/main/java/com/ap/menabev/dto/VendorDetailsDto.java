package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class VendorDetailsDto 
{
	private String createdBy;
	
	private Long createdAt;
	
	private String updatedBy;
	
	private Long updatedAt;

	private String vendorId;
	
	private String companyCode;
	
	private Boolean autoPosting;
	
	private Boolean partialPosting;
	
	private Boolean autoRejection;
		
	private String configurationId;
	
	private String vendor_id_payload;

	
	
}
