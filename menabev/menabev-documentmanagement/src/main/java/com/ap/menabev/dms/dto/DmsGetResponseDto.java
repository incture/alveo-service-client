package com.ap.menabev.dms.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DmsGetResponseDto {

	private String base64;
	private String mimeType;
	private Boolean fileAvailability;
}
