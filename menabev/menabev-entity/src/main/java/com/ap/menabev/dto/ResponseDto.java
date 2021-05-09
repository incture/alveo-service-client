package com.ap.menabev.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public @Data class ResponseDto {
	private String status;
	private String code;
	private String message;

}
