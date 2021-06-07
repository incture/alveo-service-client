package com.ap.menabev.dto;

import java.util.UUID;

import lombok.Data;


public @Data class CommentDto {
	private String commentId;
	private String requestId;
	private String comment;
	private String createdBy;
	private Long createdAt;
	private String updatedBy;
	private Long updatedAt;
	private String user;
}
