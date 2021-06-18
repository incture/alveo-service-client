package com.ap.menabev.service;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.FilterHeaderDto;

public interface InboxService {
	
	
	public ResponseEntity<?>  getInboxTask(FilterHeaderDto filterDto);

}
