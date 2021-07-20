package com.ap.menabev.entity;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sap.db.jdbc.packet.LOB;

import lombok.Data;


@Entity
@Table(name = "ACTIVITY_LOGS")
@Data
public class ActivityLogDo {

@Id
@Column(name = "GUID")
private String guid = UUID.randomUUID().toString();

@Column(name = "REQUEST_ID")
private String requestId;

@Column(name = "TASK_OWNER")
private String taskOwner;


@Column(name = "TASK_STATUS")
private String taskStatus;

@Column(name = "WORKFLOW_ID")
 private String workflowId;

@Column(name = "WORKFLOW_STATUS")
private String workflowStatus;

@Column(name = "WORKFLOW_CREATED_AT")
private Long workflowCreatedAt;

@Column(name = "WORKFLOW_CREATED_BY")
private String workflowCreateBy;

@Column(name = "TASK_ID")
private String taskId;

@Column(name = "TASK_CREATED_AT")
private Long taskCreatedAt;

@Column(name = "COMPLETED_AT")
private Long completedAt;

@Column(name = "PROCESSOR",length=255)
private String processor;//255

@Column(name = "ACTIVITY_ID",length=20)
private String activityId;//20

@Column(name = "ACTION_CODE",length=20)
private String actionCode;//2

@Column(name = "INVOICE_STATUS_CODE",length=2)
private String invoiceStatusCode;//2

@Column(name = "INVOICE_STATUS_TEXT",length=50)
private String invoiceStatusText;//50


@Column(name = "CREATED_AT")
private Long createdAt;

@Column(name = "CREATED_BY")
private String createdBy;

@Column(name = "UPDATED_AT")
private Long updatedAt;

@Column(name = "UPDATED_BY")
private String updatedBy;

//test 
@Column(name = "DECI_TEST")
private Double deciCheck;

@Column(name = "DEC_TEST2",precision=2)
private Double deciTest2;

@Column(name = "DEC_TEST3",scale = 2)
private Double deciTest3;

@Column(name = "DEC_TEST4",scale = 2)
private BigDecimal deciTest4;


}
