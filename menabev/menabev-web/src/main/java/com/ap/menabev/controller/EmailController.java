package com.ap.menabev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.service.EmailServices;

@RestController
@RequestMapping(value="/email")
public class EmailController {
	@Autowired
	EmailServices emailService;
	@GetMapping
	public String readEmail(){
		return emailService.readEmail();
	}
}
