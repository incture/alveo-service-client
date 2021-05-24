package com.ap.menabev.util;

public interface WorkflowConstants {
	String WORKFLOW_CLOSE_TASK_DESTINATION = "DKSHWorkflowInstanceToCloseTask";

	// Default Attribute Keys
	String STATUS_OF_APPROVAL_TASKS_KEY = "status";
	String STATUS_OF_APPROVAL_TASKS_KEY_RESERVED = "status";
	String WORKFLOW_DEFINATION_ID_KEY = "workflowDefinitionId";
	String RECIPIENT_USER_KEY = "recipientUsers";
	String PROCESSOR_KEY = "processor";
	String SORT_TASKS_KEY = "$orderby";
	String CREATED_AT_FROM_KEY = "createdFrom";
	String CREATED_AT_TO_KEY = "createdUpTo";
	String WORKFLOW_INSTANCE_ID_KEY = "workflowInstanceId";

	// Custom Attribute Keys
	String RETURN_TYPE = "attributes.returnType";
	String SALES_ORG = "attributes.salesOrg";
	String DISTRIBUTION_CHANNEL = "attributes.distributionChannel";
	String DIVISION = "attributes.division";
	String ORDER_TYPE = "attributes.orderType";
	String SOLD_TO_PARTY = "attributes.soldToParty";
	String SHIP_TO_PARTY = "attributes.shipToParty";
	String RETURN_REASON = "attributes.returnReason";
	String CUSTOMER_PO = "attributes.customerPo";
	String HEADER_DELIVERY_BLOCK = "attributes.headerDeliveryBlock";
	String ORDER_NUM = "attributes.orderNum";
	String TOP_KEY = "$top";
	String PAGE_NUM_KEY = "$skip";
	String FIND_COUNT_OF_TASKS_KEY = "$inlinecount";

	String FIND_COUNT_OF_TASKS_VALUE = "allpages";
	String TOP_VALUE = "1000";
	String RETURN_TYPE_VALUE = "05";
	String STATUS_OF_APPROVAL_TASKS_VALUE = "READY";
	String STATUS_OF_APPROVAL_TASKS_RESERVED_VALUE = "RESERVED";
	String WORKFLOW_DEFINATION_ID_VALUE = "triggerresolutionworkflow.triggerresolutionworkflow";
	String USER_TASK_SORTING_CREATED_AT_DESC_VALUE = "createdAt%20desc";

}