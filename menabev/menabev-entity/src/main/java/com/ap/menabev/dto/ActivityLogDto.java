package com.ap.menabev.dto;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ActivityLogDto {

	private String guid = UUID.randomUUID().toString();

	private String workflowTaskId;
	private String requestId;
	private String taskOwner;
	private String taskStatus;
	private String workflowId;
	private String workflowStatus;
	private Long workflowCreatedAt;
	private String workflowCreateBy;
	private String taskId;
	private Long taskCreatedAt;
	private Long completedAt;
	private String processor;// 255
	private String activityId;// 20
	private String actionCode;// 2
	private String actionCodeText;
	private String invoiceStatusCode;// 2
	private String invoiceStatusText;// 50
	private List<String> comments;
	private List<String> attachments;
	private Long createdAt;
	private String createdBy;
	private Long updatedAt;
	private String updatedBy;

}
