package com.ap.menabev.dto;

import lombok.Data;

@Data
public class InboxCountReponseDto {

	private long index;
	private long pageCount;
	private String tabType;
	private long countOpenTask;
	private long countMyTask;
	private long countDraft;
	private String countMessage;
	private String ListMessage;
	private String statusCode;
	private int statusCodeValue;
	
	
}
