package com.ap.menabev.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InvoiceItemAcctAssignmentPkDo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accountAssgnGuid;
	private String requestId;
	private String itemId; // invoiceItemId
	private String serialNo;
	
	

	
}
