package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ChangeIndicator;
import com.ap.menabev.dto.ConsumtionLogicOutputDto;
import com.ap.menabev.dto.GRDto;
import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InvoiceHeaderCheckDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.ItemMessageDto;
import com.ap.menabev.dto.ItemThreeWayAccAssgnPaylod;
import com.ap.menabev.dto.ItemThreeWayMatchPaylod;
import com.ap.menabev.dto.PoHistoryDto;
import com.ap.menabev.dto.PoHistoryTotalsDto;
import com.ap.menabev.dto.PostToERPDto;
import com.ap.menabev.dto.PostToERPRootDto;
import com.ap.menabev.dto.ThreeWayInvoiceItemDto;
import com.ap.menabev.dto.ThreeWayMatchOdataDto;
import com.ap.menabev.dto.ThreeWayMatchOutputDto;
import com.ap.menabev.dto.ThreeWayMatchingRootNode;
import com.ap.menabev.dto.ToAccountingAccAssgn;
import com.ap.menabev.dto.ToAccountingAccAssgnResultDto;
import com.ap.menabev.dto.ToGlAccount;
import com.ap.menabev.dto.ToItem;
import com.ap.menabev.dto.ToMatchInput;
import com.ap.menabev.dto.ToMatchOutput;
import com.ap.menabev.dto.ToMatchOutputDto;
import com.ap.menabev.dto.ToResult;
import com.ap.menabev.dto.ToReturn;
import com.ap.menabev.dto.ToReturnDto;
import com.ap.menabev.dto.ToTax;
import com.ap.menabev.dto.ToTaxDto;
import com.ap.menabev.dto.ToWithholdingTax;
import com.ap.menabev.dto.VendorCheckDto;
import com.ap.menabev.entity.PoHistoryDo;
import com.ap.menabev.entity.PoHistoryTotalsDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.PoHistoryRepository;
import com.ap.menabev.invoice.PoHistoryTotalsRepository;
import com.ap.menabev.invoice.PurchaseDocumentHeaderRepository;
import com.ap.menabev.service.DuplicateCheckService;
import com.ap.menabev.service.ValidateInvoiceService;
import com.ap.menabev.soap.service.Odata;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.OdataHelperClass;
import com.ap.menabev.util.ServiceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ValidateInvoiceServiceImpl implements ValidateInvoiceService {
	static String itemId = null;
	private static final Logger logger = LoggerFactory.getLogger(ValidateInvoiceServiceImpl.class);
	@Autowired
	DuplicateCheckService duplicateCheckService;
	@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;

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
				if (!ServiceUtil.isEmpty(changeIndicator.getIsVendoIdChanged())
						&& changeIndicator.getIsVendoIdChanged()) {
					logger.error("Inside getIsVendoIdChanged:::" + changeIndicator.getIsVendoIdChanged());
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
					if(!duplicateCheckDto.getIsDuplicate()){
						invoiceHeaderCheckDto.setInvoiceStatus(ApplicationConstants.DUPLICATE_CHECK_PASSED);
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

					
					//copy from here -----
					ResponseEntity<?> qwe = Odata.getPaymentTerms(invoiceHeaderCheckDto);
					logger.error("BODY:::" + qwe.getBody());
					logger.error("BODY:::" + qwe.getStatusCode());
					if (qwe.getStatusCode() == HttpStatus.OK) {
						String jsonString = (String) qwe.getBody();
						JSONObject json = new JSONObject(jsonString);
						logger.error("JSON:::" + json);
						if (json.has("d")) {
							logger.error("JSON:::" + json.has("d"));
							JSONObject dObject = json.getJSONObject("d");
							logger.error("dObject" + dObject);
							JSONArray resultsArray = dObject.getJSONArray("results");
							for (int i = 0; i < resultsArray.length(); i++) {
								JSONObject resultObject = (JSONObject) resultsArray.get(i);
								if (resultObject.has("PaymentTerms")) {
									String paymentTerms = resultObject.getString("PaymentTerms");
									invoiceHeaderCheckDto.setPaymentTerms(paymentTerms);
								}
								if (resultObject.has("PaymentMethodsList")) {
									String paymentMethods = resultObject.getString("PaymentMethodsList");
									invoiceHeaderCheckDto.setPaymentMethod(paymentMethods);
								}
								if (resultObject.has("PaymentBlockingReason")) {
									String paymentReason = resultObject.getString("PaymentBlockingReason");
									invoiceHeaderCheckDto.setPaymentBlock(paymentReason);
								}

							}
							//get payment terms details
							ResponseEntity<?> response = Odata.getPaymentTermsDetails(invoiceHeaderCheckDto);
							
							
							
							
							if (response.getStatusCode() == HttpStatus.OK) {
								String jsonString2 = (String) response.getBody();
								invoiceHeaderCheckDto = calculateBaseDueDates(jsonString2,invoiceHeaderCheckDto);
							}
						}
					}
					//---to this line
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

				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceRefChanged())
						&& changeIndicator.getIsInvoiceRefChanged()) {
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
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceDateChanged())
						&& changeIndicator.getIsInvoiceDateChanged()) {
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
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsInvoiceAmountChanged())
						&& changeIndicator.getIsInvoiceAmountChanged()) {
					// SetBalanceAmount = InvoiceAmount - GrossAmount
					Double balanceAmount = invoiceHeaderCheckDto.getInvoiceAmount()
							- invoiceHeaderCheckDto.getGrossAmount();
					invoiceHeaderCheckDto.setBalanceAmount(balanceAmount);
					// If Balance>0.0 and If Header Status NE Duplicate or PO
					// Missing or …. any other status till Ready to Post.
					// then set status = Balance Mismatch
					String statusCode = invoiceHeaderCheckDto.getInvoiceStatus();
					if (balanceAmount > 0.0 && !(statusCode.equals(ApplicationConstants.DUPLICATE_INVOICE)
							|| statusCode.equals(ApplicationConstants.PO_MISSING_OR_INVALID)
							|| statusCode.equals(ApplicationConstants.NO_GRN)
							|| statusCode.equals(ApplicationConstants.PARTIAL_GRN)
							|| statusCode.equals(ApplicationConstants.UOM_MISMATCH)
							|| statusCode.equals(ApplicationConstants.ITEM_MISMATCH)
							|| statusCode.equals(ApplicationConstants.QTY_MISMATCH)
							|| statusCode.equals(ApplicationConstants.PRICE_MISMATCH)
							|| statusCode.equals(ApplicationConstants.PRICE_OR_QTY_MISMATCH))) {
						invoiceHeaderCheckDto.setInvoiceStatus(ApplicationConstants.BALANCE_MISMATCH);
					}

					InvoiceHeaderObjectDto duplicateCheckDto = duplicateCheckService
							.duplicateCheck(invoiceHeaderObjectDto);
					if (duplicateCheckDto.getIsDuplicate()) {
						messagesList.add(duplicateCheckDto.getMessages());
						invoiceHeaderCheckDto.setMessages(messagesList);
						return invoiceHeaderCheckDto;
					}
					return invoiceHeaderCheckDto;

				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsBaselineDateChanged())
						&& changeIndicator.getIsBaselineDateChanged()) {
					// 1. Determine Due dates
					//
					// a. Call CalculateDueDateOdata and set all the due dates.
					//
					// 2. ResetChangeIndicator
					ChangeIndicator resetChangeIndicator = resetChangeIndicator(
							invoiceHeaderCheckDto.getChangeIndicator());
					invoiceHeaderCheckDto.setChangeIndicator(resetChangeIndicator);
					return invoiceHeaderCheckDto;
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsPaymentTermsChanged())
						&& changeIndicator.getIsPaymentTermsChanged()) {
					// 1. Get the baseline date from the payment terms data.
					//
					// 2. Call CalcaulateDueDateOdata
					return invoiceHeaderCheckDto;
				} else if (!ServiceUtil.isEmpty(changeIndicator.getIsTaxCodeChanged())
						&& changeIndicator.getIsTaxCodeChanged()) {
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
					if (balanceAmount > 0.0 && !(statusCode.equals(ApplicationConstants.DUPLICATE_INVOICE)
							|| statusCode.equals(ApplicationConstants.PO_MISSING_OR_INVALID)
							|| statusCode.equals(ApplicationConstants.NO_GRN)
							|| statusCode.equals(ApplicationConstants.PARTIAL_GRN)
							|| statusCode.equals(ApplicationConstants.UOM_MISMATCH)
							|| statusCode.equals(ApplicationConstants.ITEM_MISMATCH)
							|| statusCode.equals(ApplicationConstants.QTY_MISMATCH)
							|| statusCode.equals(ApplicationConstants.PRICE_MISMATCH)
							|| statusCode.equals(ApplicationConstants.PRICE_OR_QTY_MISMATCH))) {
						invoiceHeaderCheckDto.setInvoiceStatus(ApplicationConstants.BALANCE_MISMATCH);
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

	private InvoiceHeaderCheckDto calculateBaseDueDates(String string, InvoiceHeaderCheckDto invoiceHeaderCheckDto) {

		
		JSONObject json2 = new JSONObject(string);
		logger.error("JSON:::" + json2);
		if (json2.has("d")) {
			logger.error("JSON:::" + json2.has("d"));
			JSONObject dObject2 = json2.getJSONObject("d");
			logger.error("dObject" + dObject2);
			JSONArray resultsArray2 = dObject2.getJSONArray("results");
			for (int i = 0; i < resultsArray2.length(); i++) {
				String ZBD1T = null;
				String ZBD2T = null;
				String ZBD3T = null;
				JSONObject resultObject2 = (JSONObject) resultsArray2.get(i);
				if(resultObject2.has("BaseLineDateType")){
					String BaseLineDateType = resultObject2.getString("BaseLineDateType");
					if(BaseLineDateType==null || BaseLineDateType=="")
						invoiceHeaderCheckDto.setBaselineDate(invoiceHeaderCheckDto.getBaselineDate());
					else if("D".equalsIgnoreCase(BaseLineDateType))
						invoiceHeaderCheckDto.setBaselineDate(invoiceHeaderCheckDto.getPostingDate());
					else if("B".equalsIgnoreCase(BaseLineDateType))
						invoiceHeaderCheckDto.setBaselineDate(invoiceHeaderCheckDto.getInvoiceDate());
				}
				if(resultObject2.has("CashDiscDays1")){
					ZBD1T = resultObject2.getString("CashDiscDays1");
				}
				if(resultObject2.has("CashDiscDays2")){
					ZBD2T = resultObject2.getString("CashDiscDays2");
				}
				if(resultObject2.has("NetPmntTermPeriod")){
					ZBD3T =  resultObject2.getString("NetPmntTermPeriod");
				}
				Long period = 0L;
				// 1st condition
				if(!ZBD3T.equalsIgnoreCase("0"))
					period = Long.valueOf(ZBD3T);
				else{
					if(!ZBD2T.equalsIgnoreCase("0"))
						period = Long.valueOf(ZBD2T);
					else
						period = Long.valueOf(ZBD1T);
				}
				
					invoiceHeaderCheckDto.setDueDate(invoiceHeaderCheckDto.getBaselineDate()+period);
				//2nd cond
				
				if(!ZBD2T.equalsIgnoreCase("0")){
					invoiceHeaderCheckDto.setDiscountedDueDate2(invoiceHeaderCheckDto.getBaselineDate()+Long.valueOf(ZBD2T));
				}
				else{
					invoiceHeaderCheckDto.setDiscountedDueDate2(invoiceHeaderCheckDto.getDueDate());
				}
				if(!ZBD1T.equalsIgnoreCase("0") || !ZBD2T.equalsIgnoreCase("0")){
					invoiceHeaderCheckDto.setDiscountedDueDate1(invoiceHeaderCheckDto.getBaselineDate()+Long.valueOf(ZBD1T));
					
				}else{
					invoiceHeaderCheckDto.setDiscountedDueDate1(invoiceHeaderCheckDto.getDueDate());
				}
			}
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

	@Autowired
	PurchaseDocumentHeaderRepository poHeaderRepository;
	@Autowired
	PoHistoryRepository poHistoryRepository;
	@Autowired
	PoHistoryTotalsRepository poHistoryTotalsRepository;

	@Override
	public InvoiceHeaderDto postToERP(InvoiceHeaderDto invoiceHeaderDto) {

		String itemId = null;
		// here from invoice header need to get the reqid and fetch details from
		// database
		try {
			ThreeWayMatchOutputDto threeWayMatchOutputDto = ObjectMapperUtils.map(invoiceHeaderDto,
					ThreeWayMatchOutputDto.class);
			List<ThreeWayInvoiceItemDto> itemDtoList = threeWayMatchOutputDto.getInvoiceItems();
			List<ItemThreeWayAccAssgnPaylod> itemAccAssgnDtoList = new ArrayList<>();
			Map<String, ToTaxDto> toTaxDtoMap = new HashMap<>();
			for (ThreeWayInvoiceItemDto threeWayInvoiceItemDto : itemDtoList) {
				String matchedItem = threeWayInvoiceItemDto.getMatchDocItem();// po
				// item
				// no
				String matchedPo = String.valueOf(threeWayInvoiceItemDto.getMatchDocNum());// po
				// no

				List<PoHistoryDo> poHistoryDoList = poHistoryRepository.getByItemAndPO(matchedItem, matchedPo);
				List<PoHistoryDto> poHistoryDtoList = ObjectMapperUtils.mapAll(poHistoryDoList, PoHistoryDto.class);

				// pass serial_no if required in poHistoryTotal
				PoHistoryTotalsDo poHistoryTotalsDo = poHistoryTotalsRepository.getHistoryTotals(matchedItem,
						matchedPo);
				PoHistoryTotalsDto poHistoryTotalsDto = ObjectMapperUtils.map(poHistoryTotalsDo,
						PoHistoryTotalsDto.class);
				ConsumtionLogicOutputDto consumtionLogicOutputDto = cosumptionLogic(invoiceHeaderDto,
						threeWayInvoiceItemDto, poHistoryDtoList, poHistoryTotalsDto);
				itemAccAssgnDtoList.addAll(consumtionLogicOutputDto.getItemThreeWayAccAssgnPaylod());
				// creating tax map
				// Start - creating tax map
				Double itemTaxAmount = threeWayInvoiceItemDto.getTaxValue();
				Double itemGrossPrice = threeWayInvoiceItemDto.getGrossPrice();
				String itemTaxCode = threeWayInvoiceItemDto.getTaxCode();
				if (!ServiceUtil.isEmpty(itemTaxCode) || itemTaxCode != null) {
					if (toTaxDtoMap.containsKey(itemTaxCode)) {
						ToTaxDto valueDto = toTaxDtoMap.get(itemTaxCode);
						valueDto.setTaxAmount(String.valueOf(Double.valueOf(valueDto.getTaxAmount()) + itemTaxAmount));
						valueDto.setTaxBaseAmount(
								String.valueOf(Double.valueOf(valueDto.getTaxBaseAmount()) + itemGrossPrice));
						toTaxDtoMap.put(itemTaxCode, valueDto);
					} else {

						ToTaxDto valueDto = new ToTaxDto();
						valueDto.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());
						valueDto.setTaxCode(itemTaxCode);
						valueDto.setTaxAmount(String.valueOf(itemTaxAmount));
						valueDto.setTaxBaseAmount(String.valueOf(itemGrossPrice));
						toTaxDtoMap.put(itemTaxCode, valueDto);
					}
				}
				// END - creating tax map
			}

			PostToERPRootDto rootDto = new PostToERPRootDto();
			PostToERPDto d = new PostToERPDto();
			ToResult toResult = new ToResult();

			ToReturn toReturn = new ToReturn();
			ToWithholdingTax toWithholdingTax = new ToWithholdingTax();
			toWithholdingTax.setResults(new ArrayList<>());
			d.setToWithholdingTax(toWithholdingTax);
			ToGlAccount toGlAccount = new ToGlAccount();
			toGlAccount.setResults(new ArrayList<>());
			d.setToGlAccount(toGlAccount);
			d.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());

			if (invoiceHeaderDto.getTransactionType().contains("Invoice")
					|| invoiceHeaderDto.getTransactionType().equalsIgnoreCase("invoice"))
				d.setInvoiceInd("X");
			else if (invoiceHeaderDto.getTransactionType().contains("Credit")
					|| invoiceHeaderDto.getTransactionType().equalsIgnoreCase("Credit"))
				d.setInvoiceInd("");
			else
				d.setInvoiceInd("");

			String docDate = "\\/Date(" + invoiceHeaderDto.getInvoiceDate() + ")\\/";
			d.setDocDate(docDate);
			String pstngDate = "\\/Date(" + invoiceHeaderDto.getPostingDate() + ")\\/";
			d.setPstngDate(pstngDate);
			String blineDate = "\\/Date(" + invoiceHeaderDto.getBaseLineDate() + ")\\/";
			d.setBlineDate(blineDate);
			d.setCompCode(invoiceHeaderDto.getCompCode());
			d.setCurrency(invoiceHeaderDto.getCurrency());
			invoiceHeaderDto.getInvoiceGross();
			d.setGrossAmount(String.valueOf(invoiceHeaderDto.getInvoiceTotal())); // checked
																					// with
																					// PK
																					// and
																					// setting
																					// invoice
																					// total
			d.setPmnttrms(invoiceHeaderDto.getPaymentTerms() == null ? "" : invoiceHeaderDto.getPaymentTerms());
			d.setPmntBlock(invoiceHeaderDto.getPaymentBlock() == null ? "" : invoiceHeaderDto.getPaymentBlock());
			d.setDelCosts(String.valueOf(invoiceHeaderDto.getUnplannedCost()));
			d.setPymtMeth(invoiceHeaderDto.getPaymentMethod() == null ? "" : invoiceHeaderDto.getPaymentBlock());
			ToItem toItem = new ToItem();
			toItem.setResults(itemAccAssgnDtoList);
			d.setToItem(toItem);
			ToTax toTax = new ToTax();

			List<ToTaxDto> results = new ArrayList<>();
			if (!ServiceUtil.isEmpty(toTaxDtoMap)) {
				for (Map.Entry<String, ToTaxDto> entry : toTaxDtoMap.entrySet()) {
					results.add(entry.getValue());
				}
			}
			toTax.setResults(results);
			d.setToTax(toTax);
			toResult.setDiscShift(false);
			toResult.setFiscYear("");
			toResult.setInvDocNo("");
			toResult.setReferenceInvoiceNumber("");
			toResult.setRefDocCategory("");
			toResult.setReasonRev("");
			d.setToResult(toResult);
			toReturn.setResults(new ArrayList<>());
			d.setToReturn(toReturn);
			rootDto.setD(d);
			// post to erp
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			String payload = objectMapper.writeValueAsString(rootDto);
			logger.error("STRINGgggggggggggggggg" + payload);
			ResponseEntity<?> responseFromOdta = postToERPOdataCall(payload);
			logger.error("responseFromOdta responseFromOdta" + responseFromOdta);
			rootDto = objectMapper.readValue(responseFromOdta.getBody().toString(), PostToERPRootDto.class);
			logger.error("rootDto rootDto" + rootDto);

			ToResult resultObject = rootDto.getD().getToResult();

			List<HeaderMessageDto> messageList = new ArrayList<>();
			if (!ServiceUtil.isEmpty(resultObject)) {
				String sapInvoiceNumber = resultObject.getInvDocNo();
				String fiscalYear = resultObject.getFiscYear();
				// update in database
				int isUpated = invoiceHeaderRepository.updateHeader(fiscalYear, sapInvoiceNumber,
						invoiceHeaderDto.getRequestId());
				if (isUpated > 0) {
					System.err.println("inside is updated");
					HeaderMessageDto messageDto = new HeaderMessageDto();
					messageDto.setMessageText("Posted to SAP " + resultObject.getInvDocNo());
					messageDto.setMessageType("S");
					messageDto.setMsgClass("Success");
					messageList.add(messageDto);
				}
				invoiceHeaderDto.setHeaderMessages(messageList);

			} else {
				List<ToReturnDto> returnList = rootDto.getD().getToReturn().getResults();
				for (ToReturnDto toReturnDto : returnList) {
					HeaderMessageDto messageDto = new HeaderMessageDto();
					// messageDto.setMessageId(toReturnDto.getId());
					messageDto.setMessageNumber(toReturnDto.getNumber());
					messageDto.setMessageText(toReturnDto.getMessage());
					messageDto.setMessageType(toReturnDto.getType());
					messageDto.setMsgClass(toReturnDto.getId());
					messageList.add(messageDto);
				}
				invoiceHeaderDto.setHeaderMessages(messageList);
			}
			return invoiceHeaderDto;
		} catch (Exception e) {
			List<HeaderMessageDto> messageList = new ArrayList<>();
			e.printStackTrace();
			HeaderMessageDto messageDto = new HeaderMessageDto();
			// messageDto.setMessageId(toReturnDto.getId());
			messageDto.setMessageText(e.getMessage());
			messageDto.setMessageType("Exception");
			messageDto.setMsgClass(e.getClass().toString());
			messageList.add(messageDto);
			invoiceHeaderDto.setHeaderMessages(messageList);
			return invoiceHeaderDto;
		}

	}

	@Override
	public ThreeWayMatchOutputDto threeWayMatch(InvoiceHeaderDto invoiceHeaderDto) {
		// TODO Auto-generated method stub
		ThreeWayMatchOutputDto threeWayMatchOutputDto = ObjectMapperUtils.map(invoiceHeaderDto,
				ThreeWayMatchOutputDto.class);
		List<ItemThreeWayMatchPaylod> itemThreeWayMatchPayload = new ArrayList<>();
		List<String> purchaseOrder = new ArrayList<>();
		try {
			purchaseOrder.add(threeWayMatchOutputDto.getRefpurchaseDoc());
			List<ThreeWayInvoiceItemDto> itemList = threeWayMatchOutputDto.getInvoiceItems();
			logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>" + "" + itemList.size());
			for (ThreeWayInvoiceItemDto invoiceItemDto : itemList) {
				logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>" + "invoiceItemDto.getIsTwowayMatched()"
						+ invoiceItemDto.getIsTwowayMatched());
				logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>"
						+ "invoiceItemDto.getItemStatusCode().equals()"
						+ !invoiceItemDto.getItemStatusCode().equals(ApplicationConstants.NO_GRN));
				if (invoiceItemDto.getIsTwowayMatched()
						&& !invoiceItemDto.getItemStatusCode().equals(ApplicationConstants.NO_GRN)) {

					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>inside if block at line 555");
					List<ItemThreeWayMatchPaylod> eachItemThreewayMatchPayload = new ArrayList<>();
					String matchedItem = invoiceItemDto.getMatchDocItem();// po
																			// item
																			// no
					String matchedPo = String.valueOf(invoiceItemDto.getMatchDocNum());// po
																						// no
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>matchedItem " + matchedItem
							+ " matchedPo " + matchedPo);
					List<PoHistoryDo> poHistoryDoList = poHistoryRepository.getByItemAndPO(matchedItem, matchedPo);
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>");
					List<PoHistoryDto> poHistoryDtoList = ObjectMapperUtils.mapAll(poHistoryDoList, PoHistoryDto.class);
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>");
					// pass serial_no if required in poHistoryTotal
					PoHistoryTotalsDo poHistoryTotalsDo = poHistoryTotalsRepository.getHistoryTotals(matchedItem,
							matchedPo);
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>");
					PoHistoryTotalsDto poHistoryTotalsDto = ObjectMapperUtils.map(poHistoryTotalsDo,
							PoHistoryTotalsDto.class);
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>");
					ConsumtionLogicOutputDto consumtionLogicOutput = cosumptionLogic(invoiceHeaderDto, invoiceItemDto,
							poHistoryDtoList, poHistoryTotalsDto);
					logger.error("ValidateInvoiceServiceImpl.threeWayMatch()------>consumtionLogicOutput"
							+ consumtionLogicOutput);
					eachItemThreewayMatchPayload.addAll(consumtionLogicOutput.getItemThreeWayMatchPaylod());
					invoiceItemDto.setItemThreeWayMatchPayload(eachItemThreewayMatchPayload);
					itemThreeWayMatchPayload.addAll(consumtionLogicOutput.getItemThreeWayMatchPaylod());

				}
			}
			// making itemId to null again
			itemId = null;
			// call odata service

			ToMatchInput ToMatchInput = new ToMatchInput();
			ToMatchInput.setResults(itemThreeWayMatchPayload);
			ThreeWayMatchingRootNode d = new ThreeWayMatchingRootNode();
			ToMatchOutput toMatchOutput = new ToMatchOutput();
			List<ToMatchOutputDto> results = new ArrayList<>();
			toMatchOutput.setResults(results);
			d.setToMatchInput(ToMatchInput);
			d.setVendor(threeWayMatchOutputDto.getVendorId());
			d.setToMatchOutput(toMatchOutput);
			ThreeWayMatchOdataDto threeWayMatchOdataDto = new ThreeWayMatchOdataDto();
			threeWayMatchOdataDto.setD(d);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String payload = objectMapper.writeValueAsString(threeWayMatchOdataDto);
			// calling the OData method for Post call to Odata , with two
			// methods one for Dev and one for QA
			// based on the USERNAME of the DB Credential to find if its dev or
			// QA .
			// ThreeWayMatchedOdataCall - supports only in Dev and not in QA
			ResponseEntity<?> responseFromOData = new ResponseEntity<>("Failed ", HttpStatus.CONFLICT);
			if (ApplicationConstants.USERNAME.equals("MENABEVD")) {
				responseFromOData = threeWayMatchOdataCall(payload);
				System.err.println(
						"responseFromOData validateserviceimpl line 615 for Dev " + responseFromOData.getStatusCode());
			} else {
				responseFromOData = threeWayMatchOdataCallForQa(payload);
				System.err.println(
						"responseFromOData validateserviceimpl line 618 for QA " + responseFromOData.getStatusCode());
			}
			System.err
					.println("responseFromOData validateserviceimpl line 619 for " + responseFromOData.getStatusCode());
			String responseBody = responseFromOData.getBody().toString();
			ThreeWayMatchOdataDto threeWayMatchOdataResponse = objectMapper.readValue(responseBody,
					ThreeWayMatchOdataDto.class);
			System.err.println(threeWayMatchOdataResponse);
			logger.error("threeWayMatchOdataResponse>>>>>>>>>>>>>>>>>" + threeWayMatchOdataResponse);
			List<ToMatchOutputDto> messageResponse = threeWayMatchOdataResponse.getD().getToMatchOutput().getResults();
			// get status code

			Boolean flag = false;
			for (ThreeWayInvoiceItemDto invoiceItemDto : itemList) {
				int priceMismatchCount = 0;
				int qtyMismatchCount = 0;
				// if the item is two way match is false and was been not set for Three way match consumption logic 
				// ignore the item , and let the item status be same as the input item status 
				if(invoiceItemDto.getIsTwowayMatched()
						&& !invoiceItemDto.getItemStatusCode().equals(ApplicationConstants.NO_GRN)){
				if (!ServiceUtil.isEmpty(messageResponse) ) {
					logger.error("inside messageResponse not empty");
					List<ItemMessageDto> messageItemList = invoiceItemDto.getInvoiceItemMessages();
					String reqId = invoiceItemDto.getRequestId();
					String itemCode = invoiceItemDto.getMatchDocItem();
					for (ToMatchOutputDto toMatchOutputDto : messageResponse) {
						if (toMatchOutputDto.getRequestId().equalsIgnoreCase(reqId)
								&& toMatchOutputDto.getReferenceInvoiceItem().equalsIgnoreCase(itemCode)) {

							logger.error("inside matched item " + toMatchOutputDto.getInvoiceItem() + " and req id "
									+ toMatchOutputDto.getRequestId());
							int msgNo = 1;
							ItemMessageDto dto = new ItemMessageDto();
							dto.setItemId(toMatchOutputDto.getInvoiceItem());
							dto.setMessageClass(toMatchOutputDto.getMessageClass());
							dto.setMessageId(String.valueOf(msgNo));
							dto.setMessageNo(toMatchOutputDto.getMessageNumber());
							dto.setMessageType(toMatchOutputDto.getMessageType());
							dto.setMessageText(toMatchOutputDto.getMessageText());
							boolean added = messageItemList.add(dto);
							System.err.println(added);
							msgNo++;

							if (ApplicationConstants.QUANTITY_HIGH_MSG_NUMBER
									.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())) {
								qtyMismatchCount++;
							} else if (ApplicationConstants.PRICE_HIGH_MSG_NUMBER
									.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())
									|| ApplicationConstants.PRICE_LOW_MSG_NUMBER
											.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())) {
								priceMismatchCount++;
							}

							if (priceMismatchCount > 0 && qtyMismatchCount > 0) {
								invoiceItemDto.setItemStatusCode(ApplicationConstants.PRICE_OR_QTY_MISMATCH);
								invoiceItemDto.setItemStatusText("Price/Qty");

							} else {
								if (priceMismatchCount > 0) {
									invoiceItemDto.setItemStatusCode(ApplicationConstants.PRICE_MISMATCH);
									invoiceItemDto.setItemStatusText("Price Mismatch");
								}
								if (qtyMismatchCount > 0) {
									invoiceItemDto.setItemStatusCode(ApplicationConstants.QTY_MISMATCH);
									invoiceItemDto.setItemStatusText("Qty Mismatch");
								}

							}
							invoiceItemDto.setIsThreewayMatched(false);
						} else {

							invoiceItemDto.setItemStatusCode(ApplicationConstants.THREE_WAY_MATCH_SUCCESS);
							invoiceItemDto.setItemStatusText("Ready to Post");
							invoiceItemDto.setIsThreewayMatched(true);
						}
					}
					invoiceItemDto.setInvoiceItemMessages(messageItemList);

				}else {
					flag = true;
					invoiceItemDto.setIsThreewayMatched(true);
					invoiceItemDto.setItemStatusCode(ApplicationConstants.READY_TO_POST);
					invoiceItemDto.setItemStatusText("Ready To Post");
				}
				}
			}
			// if(flag){
			// threeWayMatchOutputDto.setInvoiceStatus("17");
			// threeWayMatchOutputDto.setInvoiceStatusText("Ready To Post");
			// }

			InvoiceHeaderDto invoiceHeaderDtoUpdated = ObjectMapperUtils.map(threeWayMatchOutputDto,
					InvoiceHeaderDto.class);
			System.err.println("THREE WAY MATCH :::::UPDATED" + invoiceHeaderDtoUpdated);
			InvoiceHeaderDto updatedHeaderStatusAfterThreeWayMatch = duplicateCheckService.determineHeaderStatus(invoiceHeaderDtoUpdated);
			
			System.err.println("THREE WAY MATCH :::::UPDATED HEADER STATUS DETERMINATION" + updatedHeaderStatusAfterThreeWayMatch);
			threeWayMatchOutputDto.setInvoiceStatus(updatedHeaderStatusAfterThreeWayMatch.getInvoiceStatus());
			threeWayMatchOutputDto.setInvoiceStatusText(updatedHeaderStatusAfterThreeWayMatch.getInvoiceStatusText());
			// threeWayMatchOutputDto.setHeaderMessages(headerMessages);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return threeWayMatchOutputDto;
	}

	private List<HeaderMessageDto> threeWayOdataCall(List<ItemThreeWayMatchPaylod> itemThreeWayMatchPayload) {

		List<HeaderMessageDto> messageDtoList = new ArrayList<>();
		for (ItemThreeWayMatchPaylod dto : itemThreeWayMatchPayload) {
			HeaderMessageDto messageDto = new HeaderMessageDto();
			messageDto.setMsgClass("Item no :" + dto.getInvoiceItem());
			messageDtoList.add(messageDto);
		}
		// TODO Auto-generated method stub
		return messageDtoList;
	}

	public static ResponseEntity<?> postToERPOdataCall(String entity) {

		try {

			ResponseEntity<?> responsePost = new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
			// call Destination Service
			Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");

			String endPointurl = "/sap/opu/odata/sap/ZP2P_API_POST_INVOICE_SRV/HeaderSet";
			System.err.println("destinationInfo " + destinationInfo);
			if (ApplicationConstants.USERNAME.equals("MENABEVD")) {

				ResponseEntity<?> responseFromOdata = OdataHelperClass.getOdataServiceToken(endPointurl, null, "GET",
						destinationInfo);

				System.err.println(" Dev responseFromOdata getToken" + responseFromOdata);

				if (responseFromOdata.getStatusCodeValue() == 200) {
					Header[] header = (Header[]) responseFromOdata.getBody();

					responsePost = OdataHelperClass.postOdataServiceToken(endPointurl, entity, "POST", destinationInfo,
							header);
					System.err.println(" Dev responseFromOdata Post" + responsePost);
				}

			} else {
				// String entityInput= "{ \"d\": { \"Vendor\": \"1000031\",
				// \"ToMatchInput\": { \"results\": [{ \"Vendor\": \"1000031\",
				// \"InvoiceItem\": \"000001\", \"PurchasingDocumentNumber\":
				// \"4100000127\", \"PurchasingDocumentItem\": \"00010\",
				// \"BaseUnit\": \"KG\", \"DntrConvOpuOu\": \"1\",
				// \"NmtrConvOpuOu\": \"1\", \"QtyOrdPurReq\": \"100\",
				// \"PostingDate\": \"20.05.2021\", \"CompanyCode\": \"1010\",
				// \"ReferenceDocument\": \"5000000267\", \"Quantity\": \"200\",
				// \"NetValDC\": \"400.00\", \"NoQuantityLogic\": \"\",
				// \"ItemCategory\": \"0\", \"QtyInvoiced\": \"0\",
				// \"ReturnsItem\": \"\", \"DrCrInd\": \"S\", \"SubDrCrInd\":
				// \"\", \"CurrencyKey\": \"SAR\", \"GrBasedIvInd\": \"X\",
				// \"QtyGoodsReceived\": \"100\", \"GoodsReceiptInd\": \"X\",
				// \"TranslationDate\": \"20.05.2021\", \"UpdatePODelCosts\":
				// \"\", \"PostInvInd\": \"X\", \"DelItemAllocInd\": \"X\",
				// \"RetAllocInd\": \"X\", \"IvOrigin\": \"\", \"QtyOpu\":
				// \"200\", \"InvValFC\": \"0.00\", \"EstPriceInd\": \"\",
				// \"GrNonValInd\": \"\", \"NetValSrvFC\": \"0\", \"AmtDC\":
				// \"400\", \"ValGrFC\": \"400\", \"NewInputVal\": \"\",
				// \"RequestId\": \"APA-001\", \"ReferenceInvoiceItem\": \"01\"
				// }, { \"Vendor\": \"1000031\", \"InvoiceItem\": \"000002\",
				// \"PurchasingDocumentNumber\": \"4100000127\",
				// \"PurchasingDocumentItem\": \"00010\", \"BaseUnit\": \"KG\",
				// \"DntrConvOpuOu\": \"1\", \"NmtrConvOpuOu\": \"1\",
				// \"QtyOrdPurReq\": \"100\", \"PostingDate\": \"20.05.2021\",
				// \"CompanyCode\": \"1010\", \"ReferenceDocument\":
				// \"5000000267\", \"Quantity\": \"10\", \"NetValDC\":
				// \"400.00\", \"NoQuantityLogic\": \"\", \"ItemCategory\":
				// \"0\", \"QtyInvoiced\": \"0\", \"ReturnsItem\": \"\",
				// \"DrCrInd\": \"S\", \"SubDrCrInd\": \"\", \"CurrencyKey\":
				// \"SAR\", \"GrBasedIvInd\": \"X\", \"QtyGoodsReceived\":
				// \"100\", \"GoodsReceiptInd\": \"X\", \"TranslationDate\":
				// \"20.05.2021\", \"UpdatePODelCosts\": \"\", \"PostInvInd\":
				// \"X\", \"DelItemAllocInd\": \"X\", \"RetAllocInd\": \"X\",
				// \"IvOrigin\": \"\", \"QtyOpu\": \"10\", \"InvValFC\":
				// \"0.00\", \"EstPriceInd\": \"\", \"GrNonValInd\": \"\",
				// \"NetValSrvFC\": \"0\", \"AmtDC\": \"400\", \"ValGrFC\":
				// \"400\", \"NewInputVal\": \"\", \"RequestId\": \"APA-001\",
				// \"ReferenceInvoiceItem\": \"02\" }] }, \"ToMatchOutput\": {
				// \"results\": [] } } }";
				responsePost = OdataHelperClass.postService(endPointurl, entity, destinationInfo);

				System.err.println(" Qa responseFromOdata Post" + responsePost);
			}
			return responsePost;
			// return header;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public static ResponseEntity<?> threeWayMatchOdataCall(String entity) {
		try {

			ResponseEntity<?> responsePost = new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
			// call Destination Service
			Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");

			String endPointurl = "/sap/opu/odata/sap/ZP2P_API_THREEWAYMATCH_SRV/MatchHeaderSet";
			// String entity =
			// "{\"d\":{\"Vendor\":\"1000031\",\"ToMatchInput\":{\"results\":[{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000001\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"200\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"200\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"},{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000002\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"10\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"10\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"}]},\"ToMatchOutput\":{\"results\":[]}}}";
			ResponseEntity<?> responseFromOdata = OdataHelperClass.getOdataServiceToken(endPointurl, null, "GET",
					destinationInfo);
			logger.error("responseFromOdata.getStatusCodeValue()------------------------------"
					+ responseFromOdata.getStatusCodeValue());
			if (responseFromOdata.getStatusCodeValue() == 200) {
				Header[] header = (Header[]) responseFromOdata.getBody();

				responsePost = OdataHelperClass.postOdataServiceToken(endPointurl, entity, "POST", destinationInfo,
						header);
			}
			logger.error("before return line 774" + responsePost);
			return responsePost;
			// return header;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// OData Call for QA
	public static ResponseEntity<?> threeWayMatchOdataCallForQa(String entity) {
		try {

			ResponseEntity<?> responsePost = new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
			// call Destination Service
			Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");

			String endPointurl = "/sap/opu/odata/sap/ZP2P_API_THREEWAYMATCH_SRV/MatchHeaderSet";
			responsePost = OdataHelperClass.postService(endPointurl, entity, destinationInfo);
			logger.error("before return line 774" + responsePost);
			return responsePost;
			// return header;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("Error" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public static ConsumtionLogicOutputDto cosumptionLogic(InvoiceHeaderDto invoiceHeaderDto,
			ThreeWayInvoiceItemDto invoiceItemDto, List<PoHistoryDto> poHistoryDtoList,
			PoHistoryTotalsDto poHistoryTotalsDto) {
		ConsumtionLogicOutputDto consumtionLogicOutputDto = new ConsumtionLogicOutputDto();
		List<ItemThreeWayMatchPaylod> threeWayInputPayloadList = new ArrayList<>();
		List<ItemThreeWayAccAssgnPaylod> itemThreeWayAccAssgnPaylod = new ArrayList<>();
		// Map<GRDto, List<IRDto>> grIrMap = getGrAndItem(poHistoryDtoList);

		// TODO Auto-generated method stub
		try {
			// Step-1
			String itemCategory = invoiceItemDto.getItemCategory();
			String accAssignmentCat = invoiceItemDto.getAccAssignmentCat();
			Boolean goodsReceiptFlag = invoiceItemDto.getGrFlag();
			Boolean grBsdIvFlag = invoiceItemDto.getGrBsdIv();
			Boolean invoiceReceiptFlag = invoiceItemDto.getIvFlag();

			logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()------>itemCategory" + itemCategory
					+ "accAssignmentCat" + accAssignmentCat + "goodsReceiptFlag" + goodsReceiptFlag + "grBsdIvFlag"
					+ grBsdIvFlag + "invoiceReceiptFlag" + invoiceReceiptFlag);
			// "" null

			if ("0".equalsIgnoreCase(itemCategory)
					&& (ServiceUtil.isEmpty(accAssignmentCat) || accAssignmentCat.equalsIgnoreCase("K")
							|| accAssignmentCat.equalsIgnoreCase("F"))
					&& goodsReceiptFlag && grBsdIvFlag && invoiceReceiptFlag) {
				// Raw Mterial Scenario
				// invqty in po ou
				// invqty in po opu
				logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()------>inside if block line no 811");
				// invunitprice in po ou
				// invunitprice in po opu

				// invpriceunit (opu)
				Integer invpriceunitOPU = invoiceItemDto.getPricingUnit();
				logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()------>invpriceunitOPU  =" + invpriceunitOPU);
				Double invItemQtyPoOU = 0.0;
				Double invItemQtyPoOPU = 0.0;
				String uom = invoiceItemDto.getUom();
				logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()------>uom  " + uom);
				if (uom.equals(invoiceItemDto.getOrderUnit())) {
					logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()------>invoiceItemDto.getOrderUnit()  "
							+ invoiceItemDto.getOrderUnit());
					invItemQtyPoOU = (Double) invoiceItemDto.getInvQty();
					logger.error("invItemQtyPoOU  " + invItemQtyPoOU);
				} else if (uom.equals(invoiceItemDto.getOrderPriceUnit())) {
					invItemQtyPoOPU = (Double) invoiceItemDto.getInvQty();
					logger.error(
							"ValidateInvoiceServiceImpl.cosumptionLogic()------>invItemQtyPoOPU  " + invItemQtyPoOPU);
				}

				Integer den = invoiceItemDto.getConvDen1();
				Integer num = invoiceItemDto.getConvNum1();
				if (!ServiceUtil.isEmpty(invItemQtyPoOU)) {
					invItemQtyPoOPU = (double) (num / den) * invItemQtyPoOU;
					logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()---Line 835--->invItemQtyPoOPU ==="
							+ invItemQtyPoOPU);
				} else if (!ServiceUtil.isEmpty(invItemQtyPoOPU)) {
					invItemQtyPoOU = (double) (den / num) * invItemQtyPoOPU;
					logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()---Line 838--->invItemQtyPoOU   "
							+ invItemQtyPoOU);
				}

				Double invoiceUnitPrice = (double) (invoiceItemDto.getGrossPrice() * invpriceunitOPU)
						/ invoiceItemDto.getInvQty();
				logger.error("ValidateInvoiceServiceImpl.cosumptionLogic()---Line 843--->invoiceUnitPrice   "
						+ invoiceUnitPrice);
				// invoiceUnitPrice in po OU
				Double invoiceUnitPricePoOU = 0.00;
				Double invoiceUnitPricePoOPU = 0.00;
				// invoiceUnitPrice in po OPU

				if (uom.equals(invoiceItemDto.getOrderUnit())) {
					invoiceUnitPricePoOU = (double) invoiceUnitPrice;

				} else if (uom.equals(invoiceItemDto.getOrderPriceUnit())) {
					invoiceUnitPricePoOPU = (double) invoiceUnitPrice;
				}

				if (!ServiceUtil.isEmpty(invoiceUnitPricePoOU)) {
					invoiceUnitPricePoOPU = (double) (den / num) * invoiceUnitPricePoOU;
				} else if (!ServiceUtil.isEmpty(invoiceUnitPricePoOPU)) {
					invoiceUnitPricePoOU = (double) (num / den) * invoiceUnitPricePoOPU;
				}

				Double remainingQTY = (double) invItemQtyPoOU;

				Double remainingAmount = (double) invoiceItemDto.getGrossPrice();

				Double settledQTY = 0.00;
				Double unSettledQTY = 0.00;

				List<GRDto> grDtoList = new ArrayList<>();
				for (int i = 0; i < poHistoryDtoList.size(); i++) {

					PoHistoryDto poHistoryDto = poHistoryDtoList.get(i);
					logger.error(
							"ValidateInvoiceServiceImpl.cosumptionLogic()------>" + poHistoryDto.getHistoryCategory()
									+ "poHistoryDto.getGoodsMvmtType()" + poHistoryDto.getGoodsMvmtType());
					if ("E".equalsIgnoreCase(poHistoryDto.getHistoryCategory())
							&& "101".equals(poHistoryDto.getGoodsMvmtType())) {

						// GR
						GRDto grDto = new GRDto();
						grDto.setHdocCategory(poHistoryDto.getHistoryCategory());
						grDto.setHDocItem(poHistoryDto.getHistoryDocumentItem());
						grDto.setHdocMvmtType(poHistoryDto.getGoodsMvmtType());
						grDto.setHdocNum(poHistoryDto.getHistoryDocument());
						grDto.setHDocQty(poHistoryDto.getQuantity());
						grDto.setHDocStlQty(0.0);
						grDto.setHDocUnStlQty(poHistoryDto.getQuantity());
						grDto.setRefDoc(poHistoryDto.getRefDocNum());
						grDto.setRefDocItem(poHistoryDto.getRefDocItem());
						grDto.setRefDocYear(poHistoryDto.getRefDocYear());
						grDto.setHDocYear(poHistoryDto.getHistoryYear());
						grDtoList.add(grDto);

					} else if ("Q".equalsIgnoreCase(poHistoryDto.getHistoryType())) {
						// IR
						for (GRDto grDto : grDtoList) {
							String docItem = grDto.getHDocItem();
							String docNum = grDto.getHdocNum();
							String rDocItem = grDto.getRefDoc();
							String rDocNum = grDto.getRefDocItem();
							if (invoiceItemDto.getProductType().equalsIgnoreCase("2")) {
								if (poHistoryDto.getRefDocItem().equals(rDocItem)
										&& poHistoryDto.getRefDocNum().equals(rDocNum)) {
									grDto.setHDocStlQty(grDto.getHDocStlQty() + poHistoryDto.getQuantity());
									grDto.setHDocUnStlQty(grDto.getHDocQty() - grDto.getHDocStlQty());
								}
							} else {
								if (poHistoryDto.getRefDocItem().equals(docItem)
										&& poHistoryDto.getRefDocNum().equals(docNum)) {
									grDto.setHDocStlQty(grDto.getHDocStlQty() + poHistoryDto.getQuantity());
									grDto.setHDocUnStlQty(grDto.getHDocQty() - grDto.getHDocStlQty());
								}
							}

						}

					}

				}
				System.err.println("GR DTO LIST:::::" + grDtoList);
				for (int i = 0; i < grDtoList.size(); i++) {
					if (remainingQTY > 0) {
						GRDto grDto = grDtoList.get(i);
						ItemThreeWayMatchPaylod inputPayload = new ItemThreeWayMatchPaylod();
						// 000001
						try {
							if (ServiceUtil.isEmpty(itemId)) {
								inputPayload.setInvoiceItem("000001");
								itemId = inputPayload.getInvoiceItem();
							} else {
								inputPayload.setInvoiceItem(String.format("%06d", Integer.parseInt(itemId) + 1));
								itemId = inputPayload.getInvoiceItem();
							}
						} catch (Exception e) {
							logger.error("threeway consumtion logic" + e.getMessage());
						}
						Double unStlQty = (double) grDto.getHDocUnStlQty();
						if (remainingQTY >= unStlQty) {
							logger.error("remainingQTY  " + remainingQTY + "unStlQty   " + unStlQty);

							String doubleAsString = String.valueOf(unStlQty);
							int indexOfDecimal = doubleAsString.indexOf(".");
							inputPayload.setQuantity(doubleAsString.substring(0, indexOfDecimal));
						} else {
							String doubleAsString = String.valueOf(remainingQTY);
							int indexOfDecimal = doubleAsString.indexOf(".");
							inputPayload.setQuantity(doubleAsString.substring(0, indexOfDecimal));
						}
						remainingQTY = remainingQTY - Double.valueOf(inputPayload.getQuantity());

						if (i == grDtoList.size() - 1) {
							// last item
							if (remainingQTY > 0) {
								String qty = String.valueOf(Double.valueOf(inputPayload.getQuantity()) + remainingQTY);
								int indexOfDecimal = qty.indexOf(".");
								inputPayload.setQuantity(qty.substring(0, indexOfDecimal));
								remainingQTY = 0.0;
							}

						}
						// default values
						// set reqID and item id for ref
						inputPayload.setRequestId(invoiceHeaderDto.getRequestId());
						inputPayload.setReferenceInvoiceItem(invoiceItemDto.getMatchDocItem());
						inputPayload.setVendor(invoiceHeaderDto.getVendorId());
						inputPayload.setPurchasingDocumentNumber(String.valueOf(invoiceItemDto.getMatchDocNum()));
						inputPayload.setPurchasingDocumentItem(invoiceItemDto.getMatchDocItem());
						inputPayload.setBaseUnit("");// check with meghana
														// added baseunit as uom
														// ,but was seting as ""
						inputPayload.setDntrConvOpuOu(String.valueOf(den));
						inputPayload.setNmtrConvOpuOu(String.valueOf(num));
						inputPayload.setQtyOrdPurReq(String.valueOf(invoiceItemDto.getPoQtyOU()));
						inputPayload.setPostingDate(
								ServiceUtil.getDateByEpoc(invoiceHeaderDto.getPostingDate(), "dd.MM.yyyy"));// TODO
						inputPayload.setCompanyCode(invoiceHeaderDto.getCompCode());
						// change
						// check with meghana inputPayload.setNetValDC will be
						// PO_NET_PRICE
						// in Invoice Item Table
						inputPayload.setNetValDC(
								String.valueOf(invoiceItemDto.getPoQtyOU() * invoiceItemDto.getPoUnitPriceOU()));
						// PO_NET_PRICE
						// in Invoice Item Table
						inputPayload.setNoQuantityLogic("");
						inputPayload.setItemCategory(invoiceItemDto.getItemCategory() == "0" ? "" : "");
						inputPayload.setQtyInvoiced(String.valueOf(grDto.getHDocStlQty()));
						inputPayload.setReturnsItem("");
						inputPayload.setDrCrInd("S");
						inputPayload.setSubDrCrInd("");
						inputPayload.setCurrencyKey(invoiceHeaderDto.getCurrency());
						inputPayload.setGrBasedIvInd(grBsdIvFlag == true ? "X" : "");
						inputPayload.setQtyGoodsReceived(String.valueOf(grDto.getHDocQty()));
						inputPayload.setGoodsReceiptInd(goodsReceiptFlag == true ? "X" : "");
						// inputPayload.setTranslationDate("");
						inputPayload.setUpdatePODelCosts("");
						inputPayload.setPostInvInd(invoiceReceiptFlag == true ? "X" : "");
						inputPayload.setDelItemAllocInd("");
						inputPayload.setRetAllocInd("");
						inputPayload.setIvOrigin("");

						String qtyOPU = String
								.valueOf((double) (num / den) * Double.valueOf(inputPayload.getQuantity()));
						int indexOfDecimal = qtyOPU.indexOf(".");
						inputPayload.setQtyOpu(qtyOPU.substring(0, indexOfDecimal));
						inputPayload.setEstPriceInd("");
						inputPayload.setGrNonValInd(invoiceItemDto.getGrNonValInd() == true ? "X" : "");//
						// discuss with PK
						inputPayload.setNewInputVal("");
						inputPayload.setInvValFC(
								String.valueOf((double) grDto.getHDocStlQty() * invoiceItemDto.getPoUnitPriceOU()));

						String valgrFc = String
								.valueOf((double) grDto.getHDocQty() * invoiceItemDto.getPoUnitPriceOU());
						int valgrFcInt = valgrFc.indexOf(".");
						inputPayload.setValGrFC(valgrFc.substring(0, valgrFcInt));
						// change
						if (ServiceUtil.isEmpty(accAssignmentCat)
								|| ((accAssignmentCat.equalsIgnoreCase("K") || accAssignmentCat.equalsIgnoreCase("F"))
										&& invoiceItemDto.getProductType().equalsIgnoreCase("1"))) {

							inputPayload.setReferenceDocument(grDto.getHdocNum());
							inputPayload.setNetValSrvFC("0");
						} else if ((accAssignmentCat.equalsIgnoreCase("K") || accAssignmentCat.equalsIgnoreCase("F"))
								&& invoiceItemDto.getProductType().equalsIgnoreCase("2")) {
							inputPayload.setReferenceDocument(grDto.getRefDoc());
							inputPayload.setNetValSrvFC(inputPayload.getValGrFC());
						}

						if (i == grDtoList.size() - 1) {
							String amtDCString = String.valueOf(remainingAmount);
							int amtDCInt = amtDCString.indexOf(".");
							inputPayload.setAmtDC(amtDCString.substring(0, amtDCInt));
							remainingAmount = 0.0;
						} else {
							Double amountDC = (double) invoiceUnitPricePoOU
									* (Double.valueOf(inputPayload.getQuantity())
											/ Double.valueOf(invoiceItemDto.getPricingUnit()));
							String amtDCString = String.valueOf(amountDC);
							int amtDCInt = amtDCString.indexOf(".");
							inputPayload.setAmtDC(amtDCString.substring(0, amtDCInt));
							remainingAmount = (double) remainingAmount - amountDC;
						}

						// here we need to form inv_itm_acc_ass
						System.err.println("AFTER INPUT PAYLOAD SET INSIDE::::::::" + inputPayload);
						ItemThreeWayAccAssgnPaylod accAssgnDto = new ItemThreeWayAccAssgnPaylod();
						// inputPayload
						// invoiceHeaderDto
						// accAssgnDto
						// invoiceItemDto
						accAssgnDto.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());
						accAssgnDto.setInvoiceDocItem(inputPayload.getInvoiceItem());
						accAssgnDto.setPoNumber(inputPayload.getPurchasingDocumentNumber());
						accAssgnDto.setPoItem(inputPayload.getPurchasingDocumentItem());
						if (invoiceItemDto.getProductType().equalsIgnoreCase("1")) {

							accAssgnDto.setRefDoc(grDto.getHdocNum());
							accAssgnDto.setRefDocYear(grDto.getHDocYear());
							accAssgnDto.setRefDocIt(grDto.getHDocItem());
							accAssgnDto.setSheetNo("");
							accAssgnDto.setSheetItem("");
						} else if (invoiceItemDto.getProductType().equalsIgnoreCase("2")) {
							accAssgnDto.setRefDoc("");
							accAssgnDto.setRefDocYear("");
							accAssgnDto.setRefDocIt("");
							accAssgnDto.setSheetNo(grDto.getRefDoc());
							accAssgnDto.setSheetItem(grDto.getRefDocItem());
						}

						accAssgnDto.setTaxCode(invoiceItemDto.getTaxCode() == null ? "" : invoiceItemDto.getTaxCode());
						accAssgnDto.setItemAmount(inputPayload.getAmtDC());
						accAssgnDto.setQuantity(inputPayload.getQuantity());
						accAssgnDto.setPoPrQnt(inputPayload.getQtyOpu());
						accAssgnDto.setPoUnit(invoiceItemDto.getOrderUnit());
						accAssgnDto.setPoPrUom(invoiceItemDto.getOrderPriceUnit());
						accAssgnDto.setPoPrUomIso("");
						accAssgnDto.setPoUnitIso("");

						//

						List<ToAccountingAccAssgnResultDto> resultsList = new ArrayList<>();
						if ("K".equalsIgnoreCase(accAssignmentCat) || "F".equalsIgnoreCase(accAssignmentCat)) {

							List<InvoiceItemAcctAssignmentDto> invItemAcctDtoList = invoiceItemDto
									.getInvItemAcctDtoList();
							for (InvoiceItemAcctAssignmentDto invitmaccassgn : invItemAcctDtoList) {
								ToAccountingAccAssgnResultDto toAccAssgnDto = new ToAccountingAccAssgnResultDto();
								toAccAssgnDto.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());
								toAccAssgnDto.setInvoiceDocItem(inputPayload.getInvoiceItem());
								toAccAssgnDto.setXunpl(invitmaccassgn.getIsPlanned());
								toAccAssgnDto.setSerialNo(invitmaccassgn.getSerialNo());
								toAccAssgnDto.setTaxCode(invoiceItemDto.getTaxCode());
								toAccAssgnDto.setPoUnit(invoiceItemDto.getOrderUnit());
								toAccAssgnDto.setPoPrUom(invoiceItemDto.getOrderPriceUnit());
								toAccAssgnDto.setPoUnitIso("");
								toAccAssgnDto.setPoPrUomIso("");
								toAccAssgnDto.setGlAccount(invitmaccassgn.getGlAccount());
								toAccAssgnDto.setCostcenter(invitmaccassgn.getCostCenter());
								toAccAssgnDto.setProfitCtr(invitmaccassgn.getProfitCtr());
								toAccAssgnDto.setCoArea(invitmaccassgn.getCoArea());// TODO
																					// to
																					// be
																					// added
								// by Lakhu in
								// InvoiceItemAcctAssignmentDto

								toAccAssgnDto.setOrderId(invitmaccassgn.getOrderId());
								// partialInvInd;distributionInd

								if ((ServiceUtil.isEmpty(invoiceItemDto.getDistributionInd())
										&& ServiceUtil.isEmpty(invoiceItemDto.getPartialInvInd()))
										|| ("".equalsIgnoreCase(invoiceItemDto.getDistributionInd())
												&& "".equalsIgnoreCase(invoiceItemDto.getPartialInvInd()))) {
									// if in item distribution filed is blank &
									// partial inv indicator is blank --- > then
									// its a single acc assgn

									toAccAssgnDto.setItemAmount(inputPayload.getAmtDC());
									toAccAssgnDto.setQuantity(inputPayload.getQuantity());
									toAccAssgnDto.setPoPrQnt(inputPayload.getQtyOpu());
								} else if ("2".equalsIgnoreCase(invoiceItemDto.getDistributionInd())
										&& "2".equalsIgnoreCase(invoiceItemDto.getPartialInvInd())) {
									// if in item distribution filed is "2" &
									// partial inv indicator is "2" --- > then
									// its a multiple acc assgn based on %
									// proportional

									Double itemAmount = (invitmaccassgn.getDistPerc()
											* Double.valueOf(inputPayload.getAmtDC()) / 100);
									toAccAssgnDto.setItemAmount(String.valueOf(itemAmount));
									Double quantity = (invitmaccassgn.getDistPerc()
											* Double.valueOf(inputPayload.getQuantity()) / 100);
									toAccAssgnDto.setQuantity(String.valueOf(quantity));
									Double poPrQnt = (invitmaccassgn.getDistPerc()
											* Double.valueOf(inputPayload.getQtyOpu()) / 100);
									toAccAssgnDto.setPoPrQnt(String.valueOf(poPrQnt));

								}
								resultsList.add(toAccAssgnDto);
							}
						}
						ToAccountingAccAssgn toAccounting = new ToAccountingAccAssgn();
						toAccounting.setResults(resultsList);
						accAssgnDto.setToAccounting(toAccounting);
						System.err.println("PAYLOAD :::::" + accAssgnDto);
						itemThreeWayAccAssgnPaylod.add(accAssgnDto);
						System.err.println("INPUT PAYLOAD :::::" + inputPayload);
						threeWayInputPayloadList.add(inputPayload);
					} else {
						System.err.println("INSIDE BREAK");
						break;
					}
				}
				System.err.println("OUTSIDE FOR LOOP GR DTO LIST:::::" + grDtoList);

			} else {
				System.err.println("ELSE PART");
			}
			System.err.println("AFTER ELSE INPUT PAYLOAD LIST 3WAY:::" + threeWayInputPayloadList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		consumtionLogicOutputDto.setItemThreeWayMatchPaylod(threeWayInputPayloadList);
		consumtionLogicOutputDto.setItemThreeWayAccAssgnPaylod(itemThreeWayAccAssgnPaylod);
		System.err.println("INPUT PAYLOAD LIST:::::::" + consumtionLogicOutputDto);
		return consumtionLogicOutputDto;
	}

}
