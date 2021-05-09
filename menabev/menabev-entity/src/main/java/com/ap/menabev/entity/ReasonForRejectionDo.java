package com.ap.menabev.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "REASON_FOR_REJECTION")
public @Data class ReasonForRejectionDo {

	@Id
	@Column(name="REJ_REASON_ID")//UUID
	private String rejReasonId;
	
	@Column(name="LANGUAGE_ID")
	private String languageId;
	
	@Column(name="TEXT")
	private String rejectionText;
	
	@Column(name="REASON_FOR_ID")//R1,R2
	private String reasonforRej;
}
