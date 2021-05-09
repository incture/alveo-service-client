package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.CommentsMasterResponseDto;
import com.ap.menabev.dto.ResponseDto;

public interface CommentService {
	ResponseDto saveComment(CommentDto dto);

	List<CommentDto> get();

	ResponseDto delete(String id);

	CommentsMasterResponseDto getCommentsByRequestIdAndUser(String requestId);

}
