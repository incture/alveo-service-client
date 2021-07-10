package com.ap.menabev.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.menabev.dto.InvoiceHeaderCheckDto;
import com.ap.menabev.dto.ItemThreeWayMatchPaylod;
import com.ap.menabev.dto.PostToERPRootDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.invoice.SchedulerCycleRepository;
import com.ap.menabev.service.AutomationService;
import com.ap.menabev.service.TestService;
import com.ap.menabev.serviceimpl.ThreeWay;
import com.ap.menabev.serviceimpl.ValidateInvoiceServiceImpl;
import com.ap.menabev.soap.service.Odata;
import com.ap.menabev.util.OdataHelperClass;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/test")
public class TestController {

	@Autowired
	SchedulerCycleRepository schedulerCycleRepository;
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	@Autowired
	AutomationService automationService;

	@Autowired
	TestService testService;

	@Autowired
	ThreeWay three;

	@GetMapping("/odata/{vendor}/{comCode}")
	public ResponseEntity<?> getPaymentTerms(@PathVariable String vendor, @PathVariable String comCode)
			throws IOException, URISyntaxException {
		InvoiceHeaderCheckDto invoiceHeaderCheckDto = new InvoiceHeaderCheckDto();
		invoiceHeaderCheckDto.setVendorID(vendor);
		invoiceHeaderCheckDto.setCompanyCode(comCode);
		return Odata.getPaymentTerms(invoiceHeaderCheckDto);
	}

	@GetMapping("/post")
	public String threeWayMatch() {
		try {
			
			
			String payload = "{\"d\":{\"InvoiceReferenceNumber\":\"INV-5600000007\",\"InvoiceInd\":\"X\",\"DocDate\":\"\\/Date(1623888000000)\\/\",\"PstngDate\":\"\\/Date(1623888000000)\\/\",\"CompCode\":\"1010\",\"Currency\":\"SAR\",\"GrossAmount\":\"20\",\"Pmnttrms\":\"V007\",\"BlineDate\":\"\\/Date(1623888000000)\\/\",\"ToItem\":{\"results\":[{\"InvoiceReferenceNumber\":\"INV-5600000007\",\"InvoiceDocItem\":\"000001\",\"PoNumber\":\"5600000007\",\"PoItem\":\"00010\",\"RefDoc\":\"5000000097\",\"RefDocYear\":\"2021\",\"RefDocIt\":\"0001\",\"TaxCode\":\"I1\",\"ItemAmount\":\"20\",\"Quantity\":\"1\",\"PoUnit\":\"KG\",\"PoUnitIso\":\"KG\",\"PoPrQnt\":\"1\",\"PoPrUom\":\"KG\",\"PoPrUomIso\":\"KG\",\"ToAccounting\":{\"results\":[]}}]},\"ToGlAccount\":{\"results\":[]},\"ToTax\":{\"results\":[]},\"ToWithholdingTax\":{\"results\":[]},\"ToReturn\":{\"results\":[]},\"ToResult\":{}}}";
			ResponseEntity<?> responseFromOData = ValidateInvoiceServiceImpl.postToERPOdataCall((payload));
			System.err.println(responseFromOData.getStatusCode());
			/*ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			PostToERPRootDto rootDto = objectMapper.readValue(responseFromOData.getBody().toString(),
					PostToERPRootDto.class);*/

			return responseFromOData.toString();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return e.toString();
		}

	}

	@GetMapping
	public List<Integer> getInt() {
		List<Integer> list = new ArrayList<Integer>();
		try {
			List<Object[]> oldCycleEntity = schedulerCycleRepository
					.getMaxResultsBySchedulerRunId("dec96fb9-1ada-4703-b586-c6806b660eb2");

			for (Object[] objects : oldCycleEntity) {
				int savedNoOfEmailspicked = 0;
				int saveNoOfAttachements = 0;
				int saveNoOfEmailsReadSuccessfully = 0;
				int saveNoOfPDFs = 0;
				int savenoOfJSONFiles = 0;
				savedNoOfEmailspicked = (int) objects[0];
				saveNoOfEmailsReadSuccessfully = (int) objects[1];
				saveNoOfAttachements = (int) objects[2];
				saveNoOfPDFs = (int) objects[3];
				savenoOfJSONFiles = (int) objects[4];
				list.addAll(Arrays.asList(savedNoOfEmailspicked, saveNoOfEmailsReadSuccessfully, saveNoOfAttachements,
						saveNoOfPDFs, savenoOfJSONFiles));
				logger.error("savedNoOfEmailspicked " + savedNoOfEmailspicked + "saveNoOfEmailsReadSuccessfully"
						+ saveNoOfEmailsReadSuccessfully + "saveNoOfAttachements" + saveNoOfAttachements
						+ "saveNoOfPDFs" + saveNoOfPDFs + "saveNoOfPDFs" + saveNoOfPDFs + "savenoOfJSONFiles"
						+ savenoOfJSONFiles);
			}

		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
		return list;

	}

	@GetMapping("/t")
	public List<String> getCurrent() {
		return automationService.getNames();
	}

	/*
	 * 
	 * @GetMapping("/testAsync") public List<ResponseEntity<?>> testAsync()
	 * throws InterruptedException, ExecutionException{ List<ResponseEntity<?>>
	 * responseTotal = new ArrayList<ResponseEntity<?>>(); String dto = "COUNT";
	 * 
	 * long time1 = new Date().getTime(); String hms =
	 * String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time1),
	 * TimeUnit.MILLISECONDS.toMinutes(time1) -
	 * TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time1)),
	 * TimeUnit.MILLISECONDS.toSeconds(time1) -
	 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time1)));
	 * System.err.println( "Started at response1 "+new Date()+" "+hms);
	 * CompletableFuture<ResponseEntity<?>> respons1 =
	 * testService.testAsyncForTaskApiCount(dto); System.err.println(
	 * "Ended at response1 "+ new Date().getTime()); dto = "TASKLIST"; long
	 * time12 = new Date().getTime(); String hms2 =
	 * String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time12),
	 * TimeUnit.MILLISECONDS.toMinutes(time12) -
	 * TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time12)),
	 * TimeUnit.MILLISECONDS.toSeconds(time12) -
	 * TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time12)));
	 * 
	 * System.err.println( "Started at response2 "+ new Date()+" "+hms2);
	 * CompletableFuture<ResponseEntity<?>> response2 =
	 * testService.testAsyncForTaskApiList(dto); System.err.println(
	 * "Ended at response2 "+ new Date().getTime());
	 * CompletableFuture.allOf(respons1,response2).join(); System.err.println(
	 * "Ended at total  "+ new Date().getTime());
	 * 
	 * responseTotal.add(respons1.get()); responseTotal.add(response2.get());
	 * 
	 * return responseTotal; }
	 */

	// public static void main(String[] args) {
	//
	// String message = null;
	// String emailBody = null;
	// try {
	// if (!ServiceUtil.isEmpty("{\"transactionType\":\"Credit\"}")) {
	// JSONObject json = new JSONObject("{\"transactionType\":\"Credit\"}");
	//
	// if (ApplicationConstants.TRANSACTION_TYPE_INVOICE
	// .equalsIgnoreCase(json.getString("transactionType"))) {
	// emailBody = "Hi,\nAdded by User:\n" + "1.PO Number: " +
	// json.getString("PO_Number")
	// + "\n2.Delivery Note: " + json.getString("Delivery_Note")
	// + "\nThanks,\nTeam AccPayable Automation";
	// message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
	// ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody,
	// File.createTempFile("sample","txt"));
	//
	// } else if (ApplicationConstants.TRANSACTION_TYPE_CREDIT
	// .equalsIgnoreCase(json.getString("transactionType"))
	// || ApplicationConstants.TRANSACTION_TYPE_DEBIT
	// .equalsIgnoreCase(json.getString("transactionType"))) {
	// emailBody = "Hi,\nFind the Attachement for " +
	// json.getString("transactionType").toUpperCase()
	// + ".\nThanks,\nTeam AP";
	// logger.error("Inside Else");
	//
	// message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
	// ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody,
	// File.createTempFile("sample","txt"));
	// logger.error("Printing Message:::"+message);
	// }
	//
	//
	// } else {
	// emailBody = "Hi,\nFind the Attachement\nThanks,\nTeam AP";
	// message = Email.sendmailTOCSU(ApplicationConstants.CSU_EMAIL,
	// ApplicationConstants.UPLOAD_INVOICE_TO_CSU_SUBJECT, emailBody,
	// File.createTempFile("sample","txt"));
	// }
	// System.out.println(message);
	// } catch (Exception e) {
	// e.printStackTrace();
	// message = e.getMessage();
	// System.out.println(message);
	// }
	//
	// }
	//

}
