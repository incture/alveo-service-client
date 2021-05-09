package com.ap.menabev.dto;



import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;
@Embeddable
public @Data class PurchaseDocumentItemPkDto implements Serializable {

	private static final long serialVersionUID = 3875994822103092792L;

	private String documentNumber;
	private String documentItem;
}
