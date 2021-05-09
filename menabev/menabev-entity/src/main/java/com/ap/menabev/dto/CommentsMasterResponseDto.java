package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

public @Data class CommentsMasterResponseDto {
	
	private List<CommentDto> commentDtos;
	private ResponseDto response;

}
