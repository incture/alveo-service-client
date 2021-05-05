package com.ap.menabev.dto;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;
//@Embeddable
public @Data class NonPoTemplateItemsPKId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3875994822103092792L;

//	@Column(name = "ITEM_ID")
//	private String itemId;
//
//	@Column(name = "TEMPLATE_ID")
//	private String templateId;
//	
//
	private String itemId;
	private String templateId;
}