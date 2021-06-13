package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InboxResponseOutputDto {
	
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
	private List<InboxOutputDto> listOfTasks;

}
