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
public class InvoiceHeaderPk implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String guid;
	private String requestId; //0

}
