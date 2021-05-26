package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

public @Data class WorkflowTaskOutputDto {

	private String activityId;
	private String claimedAt;
	private String completedAt;
	private String createdAt;
	private String description;
	private String id;
	private String processor;
	private List<String> recipientUsers;
	private List<String> recipientGroups;
	private String status;
	private String subject;
	private String workflowDefinitionId;
	private String workflowInstanceId;
	private String priority;
	private String dueDate;
	private String createdBy;
	private String definitionId;
	private String lastChangedAt;
	private String requestIds;
	private String startedAt;
	


}