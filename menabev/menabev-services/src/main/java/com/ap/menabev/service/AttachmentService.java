package com.ap.menabev.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.AttamentMasterDto;
import com.ap.menabev.dto.ResponseDto;

public interface AttachmentService {

	
	AttamentMasterDto saveOrUpdateAllAttachments(List<AttachmentDto> dtoList,String callerType);

	AttamentMasterDto getAllAttachments();

	ResponseDto deleteAttachment(String attachmentId);

	AttamentMasterDto getAllAttachmentsByReqId(String requestId);

	ResponseEntity<?> downloadPdf(String requestId);
}
