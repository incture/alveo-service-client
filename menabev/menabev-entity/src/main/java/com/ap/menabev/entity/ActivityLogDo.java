package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "ATTACHMENT")
@Data
public class ActivityLogDo {

@Id
@Column(name = "ID")
private String id = UUID.randomUUID().toString();


@Column(name = "WORKFLOW_TASK_ID")
private String workflowTaskId;

@Column(name = "REQUEST_ID")
private String requestId;

@Column(name = "EXCEPTION_CODE")
private String exceptionCode;

@Column(name = "PRIORITY")
private String priority;

@Column(name = "TASK_OWNER")
private String taskOwner;

@Column(name = "STATUS_OF_EXCEPTION")
private String statusOfException;

@Column(name = "TASK_STATUS")
private String taskStatus;

}
