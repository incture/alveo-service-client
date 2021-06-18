package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InboxResponseOutputDto {
	
	private long top;
	private long skip;
	private long draftCount;
	private long totalCount;
	private List<InboxOutputDto> taskList;
	private List<InboxOutputDto>  draftList;
	private String Message;
	private int statusCodeValue;
	

}
