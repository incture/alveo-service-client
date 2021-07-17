package com.ap.menabev.dms.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DmsResponseDto {

	
	private ResponseDto response;
	private String documentId;
	private String documentName;

}
