package com.ap.menabev.serviceimpl;

import org.springframework.stereotype.Service;

import com.ap.menabev.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	@Override
	public String test() {
		return "Hello World";
	}

}
