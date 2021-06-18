package com.ap.menabev.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;

import com.ap.menabev.serviceimpl.FilterMultipleHeaderSearchDto;

public interface TestService {
	String test();
	
	String getCurrent();

	/*public CompletableFuture<ResponseEntity<?>> testAsyncForTaskApiCount(String  dto) throws InterruptedException;

	CompletableFuture<ResponseEntity<?>> testAsyncForTaskApiList(String dto) throws InterruptedException;

	CompletableFuture<ResponseEntity<?>> getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto);

	CompletableFuture<ResponseEntity<?>> getInboxTaskCounnt(FilterMultipleHeaderSearchDto filterDto);*/
}