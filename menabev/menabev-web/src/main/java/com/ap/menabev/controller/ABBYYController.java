package com.ap.menabev.controller;

import javax.mail.Message;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.email.Email;
import com.ap.menabev.entity.SchedulerConfigurationDo;
import com.ap.menabev.service.AutomationService;

@RestController
@RequestMapping(value = "/abbyy")
public class ABBYYController {
	@Autowired
	AutomationService automationService;
	
	@Autowired
	Email email;

//	@GetMapping("/inbox")
//	public ResponseDto putFileInSFTPServer() {
//		try {
//
//			return automationService.extractInvoiceFromEmail();
//		} catch (Exception e) {
//			e.printStackTrace();
//			// TODO: handle exception
//			return new ResponseDto("Error","500", e.getMessage(),null);
//		}
//	}
	@GetMapping("/shared")
	public void putFileInSFTPServerFromSharedEmailBox() {
		SchedulerConfigurationDo entity = new SchedulerConfigurationDo();
//		entity.setConfigurationId("3856f277-6bc4-44a8-a415-32d13010d207");
		automationService.extractInvoiceFromSharedEmailBoxInScheduler(entity);
	}
	@GetMapping("/check")
	public Message[] getEmail(){
		return email.readEmail("outlook.office365.com", "accpay@menabev.com", "MenaBev@123", "INBOX", "UNSEEN");
	}
	
}
