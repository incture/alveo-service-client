package com.ap.menabev.entity;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "NON_PO_TEMPLATE")

public @Data class NonPoTemplateDo {

	@Id
	@Column(name="UUID")
	private String uuid;
	
	@Column(name="REQUEST_ID")
	private String requestId;
	
	
	@Column(name = "TEMPLATE_ID",unique=true,nullable=false)
	private String templateId;
 
	@Column(name = "ACC_CLERK_ID")
	private String accClerkId;

	@Column(name = "BASECODER_ID")
	private String basecoderId;

	@Column(name = "VENDOR_ID")
	private String vendorId;

	@Column(name = "TEMPLATE_NAME",unique=true,nullable=false)
	private String templateName;

	@Column(name = "CREATED_BY")
	private String createdBy;
  
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="createdAt")
	private Date createdAt;
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)

	@Column(name="updatedAt")
	private Date updatedAt;

}
