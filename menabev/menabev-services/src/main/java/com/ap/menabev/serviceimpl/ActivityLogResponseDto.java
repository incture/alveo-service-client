package com.ap.menabev.serviceimpl;

import java.util.List;

import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.WorkflowTaskOutputDto;

import lombok.Data;

@Data
public class ActivityLogResponseDto {

	private InvoiceReceivedDto invoiceReceived;
	private List<WorkflowTaskOutputDto> validation;
	private  List<WorkflowTaskOutputDto> approval;
	private String message;
	private String statusCode;
	private List<AttachmentDto> attachment;
}
