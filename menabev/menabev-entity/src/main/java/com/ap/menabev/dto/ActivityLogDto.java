package com.ap.menabev.dto;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;

import com.sap.db.jdbc.packet.LOB;

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
	private String invoiceStatusCode;// 2
	private String invoiceStatusText;// 50
	private String commentsGuid;
	private String attachmentGuid;
	private Long createdAt;
	private String createdBy;
	private Long updatedAt;
	private String updatedBy;

}
