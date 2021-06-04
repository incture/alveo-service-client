package com.ap.menabev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CODES_AND_TEXTS")
public class CodesAndTextsDo {

	@Id
	@Column(name ="UUID")
	private String uuId ;
	
	@Column(name ="TYPE")
	private String type;
	
	@Column(name ="STATUS_CODE")
	private String statusCode;
	
	@Column(name ="LANGUAGE")
	private String language;
	
	@Column(name ="SHORT_TEXT")
	private String shortText;
	
	@Column(name ="LONG_TEXT")
	private String longText;
}
