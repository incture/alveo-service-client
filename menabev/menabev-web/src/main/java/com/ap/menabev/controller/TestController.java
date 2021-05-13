package com.ap.menabev.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.service.TestService;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	@Autowired
	TestService testService;
	
	@GetMapping("/testService")
	public String test(){

		System.out.println("autowired = "+testService);
		return testService.test();
		
	}
	
	
	
}
