package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ChangeIndicator;
import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InvoiceHeaderCheckDto;
import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.dto.VendorCheckDto;
import com.ap.menabev.service.DuplicateCheckService;
import com.ap.menabev.service.ValidateInvoiceService;
import com.ap.menabev.soap.service.Odata;
import com.ap.menabev.util.ServiceUtil;

@Service
public class ValidateInvoiceServiceImpl implements ValidateInvoiceService {
	private static final Logger logger = LoggerFactory.getLogger(ValidateInvoiceServiceImpl.class);
	@Autowired
	DuplicateCheckService duplicateCheckService;

	@Override
	public InvoiceHeaderCheckDto invoiceHeaderCheck(InvoiceHeaderCheckDto invoiceHeaderCheckDto) {
		// TODO Auto-generated method stub
		ChangeIndicator changeIndicator = invoiceHeaderCheckDto.getChangeIndicator();
		List<HeaderMessageDto> messagesList = new ArrayList<>();
		InvoiceHeaderObjectDto invoiceHeaderObjectDto = new InvoiceHeaderObjectDto();
		invoiceHeaderObjectDto.setCompanyCode(invoiceHeaderCheckDto.getCompanyCode());
		invoiceHeaderObjectDto.setInvoiceAmount(String.valueOf(invoiceHeaderCheckDto.getInvoiceAmount()));
		invoiceHeaderObjectDto.setInvoiceDate(invoiceHeaderCheckDto.getInvoiceDate());
		invoiceHeaderObjectDto.setInvoiceReference(invoiceHeaderCheckDto.getInvoiceReference());
		invoiceHeaderObjectDto.setInvoiceStatus(invoiceHeaderCheckDto.getInvoiceStatus());
		invoiceHeaderObjectDto.setRequestId(invoiceHeaderCheckDto.getRequestID());
		invoiceHeaderObjectDto.setVendorId(invoiceHeaderCheckDto.getVendorID());

		try {
			if (!ServiceUtil.isEmpty(changeIndicator)) {
				if (!ServiceUtil.isEmpty(changeIndicator.getIsVendoIdChanged()) && changeIndicator.getIsVendoIdChanged()) {
					logger.error("Inside getIsVendoIdChanged:::"+changeIndicator.getIsVendoIdChanged());
					// 1. Call VendorCheckAPI

					InvoiceHeaderObjectDto vendorCheckDto = duplicateCheckService.vendorCheck(invoiceHeaderObjectDto);

					if (!ServiceUtil.isEmpty(vendorCheckDto) && !ServiceUtil.isEmpty(vendorCheckDto.getMessages())) {
						messagesList.add(vendorCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					}
					// a. If success, then move on
					//

					// b. If error, stop processing and send response to UI
					//
					// 2. Call duplicateCheckAPI

					InvoiceHeaderObjectDto duplicateCheckDto = duplicateCheckService
							.duplicateCheck(invoiceHeaderObjectDto);
					if (duplicateCheckDto.getIsDuplicate()) {
						messagesList.add(duplicateCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					}

					// a. If success, and move on
					//
					// b. If error stop processing and send response to UI.
					//
					// 3. Determine Payment Term and dependent data
					//
					// a. Call determinePaymentTermsOdata service and
					// setPaymentTerms
					
					ResponseEntity<?> qwe = Odata.getPaymentTerms(invoiceHeaderCheckDto);
					logger.error("BODY:::"+qwe.getBody());
					logger.error("BODY:::"+qwe.getStatusCode());
					if(qwe.getStatusCode()==HttpStatus.OK){
						String jsonString = (String) qwe.getBody();
						JSONObject json = new JSONObject(jsonString);
						logger.error("JSON:::"+json);
						if(json.has("d")){
							logger.error("JSON:::"+json.has("d"));
						JSONObject dObject=	json.getJSONObject("d");
						logger.error("dObject"+dObject);
						JSONArray resultsArray = dObject.getJSONArray("results");
						for (int i = 0; i < resultsArray.length(); i++) {
							JSONObject resultObject = (JSONObject) resultsArray.get(i);
							if(resultObject.has("PaymentTerms")){
								String paymentTerms = resultObject.getString("PaymentTerms");
								invoiceHeaderCheckDto.setPaymentTerms(paymentTerms);
							}
							
						}
						
						
						}
					}
					
					
					// b. Determine baseline date configuration and
					// SetBaselineDate
					//
					// i. BaseLineDateConfig=Default
					//
					// 1. DO NOT DO anything, baseline date will be selected by
					// user on UI.
					//
					// ii. BaselineDateConfig=Postingdate
					//
					// 1. Then set baseline date = postingDate
					//
					// iii. BaselinDateConfig=DocumentDate
					//
					// 1. Set baselineDate=documentDate
					//
					// 4. Determine Due dates
					//
					// a. Call CalculateDueDateOdata and set the due dates in
					// the invoiceHeader
					//
					// 5. ResetChangeIndicator
					//
					// a. ResetChagneIndicator-VendorId
					
					ChangeIndicator resetChangeIndicator = resetChangeIndicator(
							invoiceHeaderCheckDto.getChangeIndicator());
					invoiceHeaderCheckDto.setChangeIndicator(resetChangeIndicator);
					return invoiceHeaderCheckDto;

				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceRefChanged()) && changeIndicator.getIsInvoiceRefChanged()) {
					// 1. Call duplicateCheckAPI
					//

					InvoiceHeaderObjectDto duplicateCheckDto = duplicateCheckService
							.duplicateCheck(invoiceHeaderObjectDto);
					if (duplicateCheckDto.getIsDuplicate()) {
						messagesList.add(duplicateCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					} else {
						ChangeIndicator resetChangeIndicator = resetChangeIndicator(
								invoiceHeaderCheckDto.getChangeIndicator());
						invoiceHeaderCheckDto.setChangeIndicator(resetChangeIndicator);
						return invoiceHeaderCheckDto;
					}
					// a. If success, then move on
					//
					// b. If error, stop processing and send response to UI
					//
					// 2. ResetChangeIndicator
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceDateChanged()) && changeIndicator.getIsInvoiceDateChanged()) {
					// 1. Call duplicateCheckAPI
					//
					// a. If success, then move on
					InvoiceHeaderObjectDto duplicateCheckDto = duplicateCheckService
							.duplicateCheck(invoiceHeaderObjectDto);
					if (duplicateCheckDto.getIsDuplicate()) {
						messagesList.add(duplicateCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					}
					// b. If error, stop processing and send response to UI
					//
					// 2. Determine Due dates
					//
					// a. Call CalculateDueDateOdata and set the due dates
					//
					// 3. ResetChangeIndicator

					ChangeIndicator resetChangeIndicator = resetChangeIndicator(
							invoiceHeaderCheckDto.getChangeIndicator());
					invoiceHeaderCheckDto.setChangeIndicator(resetChangeIndicator);
					return invoiceHeaderCheckDto;

					// a. ResetChagneIndicator-InvoiceDate
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceAmountChanged()) && changeIndicator.getIsInvoiceAmountChanged()) {
					// SetBalanceAmount = InvoiceAmount - GrossAmount
					Double balanceAmount = invoiceHeaderCheckDto.getInvoiceAmount()
							- invoiceHeaderCheckDto.getGrossAmount();
					invoiceHeaderCheckDto.setBalanceAmount(balanceAmount);
					// If Balance>0.0 and If Header Status NE Duplicate or PO
					// Missing or …. any other status till Ready to Post.
					// then set status = Balance Mismatch
					String statusCode = invoiceHeaderCheckDto.getInvoiceStatus();
					if (balanceAmount > 0.0 && !(statusCode.equals("1") || statusCode.equals("2")
							|| statusCode.equals("7") || statusCode.equals("8") || statusCode.equals("9"))) {
						invoiceHeaderCheckDto.setInvoiceStatus("10");
					}

					InvoiceHeaderObjectDto duplicateCheckDto = duplicateCheckService
							.duplicateCheck(invoiceHeaderObjectDto);
					if (duplicateCheckDto.getIsDuplicate()) {
						messagesList.add(duplicateCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					}
					return invoiceHeaderCheckDto;

				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsBaselineDateChanged()) && changeIndicator.getIsBaselineDateChanged()) {
					// 1. Determine Due dates
					//
					// a. Call CalculateDueDateOdata and set all the due dates.
					//
					// 2. ResetChangeIndicator
					ChangeIndicator resetChangeIndicator = resetChangeIndicator(
							invoiceHeaderCheckDto.getChangeIndicator());
					invoiceHeaderCheckDto.setChangeIndicator(resetChangeIndicator);
					return invoiceHeaderCheckDto;
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsPaymentTermsChanged()) && changeIndicator.getIsPaymentTermsChanged()) {
					// 1. Get the baseline date from the payment terms data.
					//
					// 2. Call CalcaulateDueDateOdata
					return invoiceHeaderCheckDto;
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsTaxCodeChanged()) && changeIndicator.getIsTaxCodeChanged()) {
					// 1. Calculate systemSuggestedTaxAmount.
					// 2. If InvTaxAmount > SystemSuggested tax amount
					if (invoiceHeaderCheckDto.getTaxAmount() > invoiceHeaderCheckDto.getSystemSuggestedtaxAmount()) {
						HeaderMessageDto messageDto = new HeaderMessageDto();
						messageDto.setMsgClass("VendorCheck");
						messageDto.setMessageId(21);
						Double diff = invoiceHeaderCheckDto.getTaxAmount()
								- invoiceHeaderCheckDto.getSystemSuggestedtaxAmount();
						messageDto.setMessageNumber(invoiceHeaderCheckDto.getTaxAmount() + "-"
								+ invoiceHeaderCheckDto.getSystemSuggestedtaxAmount() + "=" + diff
								+ invoiceHeaderCheckDto.getCurrency());
						messageDto.setMessageType("W");
						messageDto.setMessageText("Tax entered is more than calculated Tax.");
						messagesList.add(messageDto);
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					} else {
						return invoiceHeaderCheckDto;

					}
					//
					// a. SetHeaderMessages
					//
					// i. MsgClass: VendorCheck

					// ii. MessageID: 21
					//
					// iii. MessageNumber: <TaxAmount> - <suggestedTax> = 60 $
					//
					// iv. MessageType: W
					//
					// v. MessageText: Tax entered is more than calculated Tax.
				} else if (changeIndicator.getIsTaxAmountChanged()) {
					// 1. If InvTaxAmount > SystemSuggestedTaxAmount//it will
					// come as UI input(never change) // what is
					// SystemSuggestedTaxAmount???
					if (invoiceHeaderCheckDto.getTaxAmount() > invoiceHeaderCheckDto.getSystemSuggestedtaxAmount()) {
						HeaderMessageDto messageDto = new HeaderMessageDto();
						messageDto.setMsgClass("VendorCheck");
						messageDto.setMessageId(21);
						Double diff = invoiceHeaderCheckDto.getTaxAmount()
								- invoiceHeaderCheckDto.getSystemSuggestedtaxAmount();
						messageDto.setMessageNumber(invoiceHeaderCheckDto.getTaxAmount() + "-"
								+ invoiceHeaderCheckDto.getSystemSuggestedtaxAmount() + "=" + diff
								+ invoiceHeaderCheckDto.getCurrency());
						messageDto.setMessageType("W");
						messageDto.setMessageText("Tax entered is more than calculated Tax.");
						messagesList.add(messageDto);
						invoiceHeaderCheckDto.setMessages(messagesList);
					}

					// a. SetHeaderMessages
					//
					// 2. Gross Amount = Gross Amount – currentTaxAmount from
					// UI.
					//

					Double grossAmount = invoiceHeaderCheckDto.getGrossAmount() - invoiceHeaderCheckDto.getTaxAmount();
					Double balanceAmount = invoiceHeaderCheckDto.getInvoiceAmount() - grossAmount;

					// 3. Balance = Credit – Debits
					//
					// 4. If Header Status NE Duplicate or PO Missing or …. any
					// other status then set status = Balance Mismatch
					String statusCode = invoiceHeaderCheckDto.getInvoiceStatus();
					if (balanceAmount > 0.0 && !(statusCode.equals("1") || statusCode.equals("2")
							|| statusCode.equals("7") || statusCode.equals("8") || statusCode.equals("9"))) {
						invoiceHeaderCheckDto.setInvoiceStatus("10");
					}
					return invoiceHeaderCheckDto;
				}
			} else {
				return invoiceHeaderCheckDto;

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			HeaderMessageDto hmdto = new HeaderMessageDto();
			hmdto.setMessageId(0);
			hmdto.setMessageNumber("0");
			hmdto.setMessageText("Exception In HeaderCheckAPI:: " + e.getMessage());
			hmdto.setMsgClass(e.getClass() + "");
			messagesList.add(hmdto);
			invoiceHeaderCheckDto.setMessages(messagesList);
			return invoiceHeaderCheckDto;
		}
		return invoiceHeaderCheckDto;
	}

	private ChangeIndicator resetChangeIndicator(ChangeIndicator changeIndicator) {

		if (!ServiceUtil.isEmpty(changeIndicator)) {
			ChangeIndicator resetChangeIndicator = new ChangeIndicator();
			resetChangeIndicator.setIsBaselineDateChanged(Boolean.FALSE);
			resetChangeIndicator.setIsCompanyCodeChanged(Boolean.FALSE);
			resetChangeIndicator.setIsCurrencyChanged(Boolean.FALSE);
			resetChangeIndicator.setIsHeaderChanged(Boolean.FALSE);
			resetChangeIndicator.setIsInvoiceAmountChanged(Boolean.FALSE);
			resetChangeIndicator.setIsInvoiceDateChanged(Boolean.FALSE);
			resetChangeIndicator.setIsInvoiceRefChanged(Boolean.FALSE);
			resetChangeIndicator.setIsInvoiceTypeChanged(Boolean.FALSE);
			resetChangeIndicator.setIsPaymentTermsChanged(Boolean.FALSE);
			resetChangeIndicator.setIsTaxAmountChanged(Boolean.FALSE);
			resetChangeIndicator.setIsTaxCodeChanged(Boolean.FALSE);
			resetChangeIndicator.setIsVendoIdChanged(Boolean.FALSE);
			return resetChangeIndicator;
		} else {
			return changeIndicator;
		}

	}

	@Override
	public VendorCheckDto vendorCheck(VendorCheckDto vendorCheckDto) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		ValidateInvoiceServiceImpl impl = new ValidateInvoiceServiceImpl();
		
		InvoiceHeaderCheckDto dto =  new InvoiceHeaderCheckDto();
		ChangeIndicator changeIndicator = new ChangeIndicator();
		changeIndicator.setIsVendoIdChanged(Boolean.TRUE);
		dto.setChangeIndicator(changeIndicator);
		dto.setBalanceAmount(new Double("200"));
		dto.setCurrency("$");
		dto.setGrossAmount(500.00);
		
		
		
		
		impl.invoiceHeaderCheck(dto );
	}

}
