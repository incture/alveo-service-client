package com.ap.menabev.dto;

import java.util.UUID;

import com.sap.db.jdbc.packet.LOB;

import lombok.Data;

@Data
public class ActivityLogDto {
	
	
	private String id;
	private String workflowTaskId;
	private String requestId;
	private String exceptionCode;
	private String priority;
	private String taskOwner;
	private String statusOfException;
	private String taskStatus;
	private String taskUserId;
	private LOB taskComments;

}
