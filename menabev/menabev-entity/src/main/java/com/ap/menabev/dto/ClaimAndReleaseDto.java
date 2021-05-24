package com.ap.menabev.dto;

import lombok.Data;

@Data
public class ClaimAndReleaseDto {

	private String taskID;
	
	private String userId;
	
	private boolean  claim;
}
