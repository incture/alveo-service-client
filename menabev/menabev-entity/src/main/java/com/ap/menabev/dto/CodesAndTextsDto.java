package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CodesAndTextsDto {

	private String uuId ;
	private String type;
	private String statusCode;
	private String language;
	private String shortText;
	private String longText;
}
