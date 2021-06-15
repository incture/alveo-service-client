package com.ap.menabev.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.email.Email;
import com.ap.menabev.service.TestService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	TestService testService;
	
	@GetMapping("/testService")
	public String test(){

		System.out.println("autowired = "+testService);
		return testService.test();
		
	}
	
/*	

	@GetMapping("/testAsync")
	public List<ResponseEntity<?>> testAsync() throws InterruptedException, ExecutionException{
		List<ResponseEntity<?>> responseTotal = new ArrayList<ResponseEntity<?>>();
                 String dto = "COUNT";
                 
               long time1 = new Date().getTime();
                 String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time1),
                         TimeUnit.MILLISECONDS.toMinutes(time1) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time1)),
                         TimeUnit.MILLISECONDS.toSeconds(time1) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time1)));
                 System.err.println( "Started at response1 "+new Date()+" "+hms);
                 CompletableFuture<ResponseEntity<?>>  respons1 = testService.testAsyncForTaskApiCount(dto);
		                     System.err.println( "Ended at response1 "+ new Date().getTime());
                 dto = "TASKLIST";
                 long time12 = new Date().getTime();
                 String hms2 = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time12),
                         TimeUnit.MILLISECONDS.toMinutes(time12) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time12)),
                         TimeUnit.MILLISECONDS.toSeconds(time12) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time12)));
                 
                 System.err.println( "Started at response2 "+ new Date()+" "+hms2);
		     CompletableFuture<ResponseEntity<?>> response2 =  testService.testAsyncForTaskApiList(dto);
		     System.err.println( "Ended at response2 "+ new Date().getTime());
		     CompletableFuture.allOf(respons1,response2).join();
		     System.err.println( "Ended at total  "+ new Date().getTime());
    
		     responseTotal.add(respons1.get());
		     responseTotal.add(response2.get());
		     
		return responseTotal;
	}*/
	
	
	
	
//	public static void main(String[] args) {
//
//		String message = null;
//		String emailBody = null;
//		try {
//			if (!ServiceUtil.isEmpty("{\"transactionType\":\"Credit\"}")) {
//				JSONObject json = new JSONObject("{\"transactionType\":\"Credit\"}");
//
//					if (ApplicationConstants.TRANSACTION_TYPE_INVOICE
//							.equalsIgnoreCase(json.getString("transactionType"))) {
//						emailBody = "Hi,\nAdded by User:\n" + "1.PO Number: " + json.getString("PO_Number")
//								+ "\n2.Delivery Note: " + json.getString("Delivery_Note")
//								+ "\nThanks,\nTeam AccPayable Automation";
//						message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
//								ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, File.createTempFile("sample","txt"));
//
//					} else if (ApplicationConstants.TRANSACTION_TYPE_CREDIT
//							.equalsIgnoreCase(json.getString("transactionType"))
//							|| ApplicationConstants.TRANSACTION_TYPE_DEBIT
//									.equalsIgnoreCase(json.getString("transactionType"))) {
//						emailBody = "Hi,\nFind the Attachement for " + json.getString("transactionType").toUpperCase()
//								+ ".\nThanks,\nTeam AP";
//						logger.error("Inside Else");
//						
//						message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
//								ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, File.createTempFile("sample","txt"));
//						logger.error("Printing Message:::"+message);
//					}
//				
//
//			} else {
//				emailBody = "Hi,\nFind the Attachement\nThanks,\nTeam AP";
//				message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
//						ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody, File.createTempFile("sample","txt"));
//			}
//			System.out.println(message);
//		} catch (Exception e) {
//			e.printStackTrace();
//			message = e.getMessage();
//			System.out.println(message);
//		}
//	
//	}
//	
	
	
}
