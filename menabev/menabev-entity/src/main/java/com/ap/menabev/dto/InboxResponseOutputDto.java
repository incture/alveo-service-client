package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class InboxResponseOutputDto {
	
	private int count;
	
	private List<InboxOutputDto> listOfTasks;

}
