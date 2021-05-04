package com.ap.menabev.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResponseDto {
	private String status;
	private String code;
	private String message;
}
