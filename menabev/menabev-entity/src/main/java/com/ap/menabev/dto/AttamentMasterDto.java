package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

public @Data class AttamentMasterDto {
	private List<AttachmentDto> attachmentList;
	private ResponseDto response;

}
