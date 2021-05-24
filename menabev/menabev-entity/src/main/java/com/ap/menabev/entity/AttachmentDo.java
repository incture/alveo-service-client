package com.ap.menabev.entity;



import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ATTACHMENT")
public @Data class AttachmentDo {
	
	@Id
	@Column(name = "ATTACHMENT_ID")
	private String attachmentId = UUID.randomUUID().toString();
	
	@Column(name = "REQUEST_ID",nullable=false)
	private String requestId;
	
	@Column(name="FILE_NAME")
	private String fileName;
	
	@Column(name="FILE_TYPE")
	private String fileType;
	
	@Lob
	@Column(name="FILEBASE64")
	private String fileBase64;
	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="CREATED_AT")
	private Long createdAt;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_AT")
	private Long updatedAt;
	
	@Column(name="MASTER")
	private Boolean master;

}
