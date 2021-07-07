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
import com.ap.menabev.dto.ToItem;
import com.ap.menabev.dto.ToMatchInput;
import com.ap.menabev.dto.ToMatchOutput;
import com.ap.menabev.dto.ToMatchOutputDto;
import com.ap.menabev.dto.ToTax;
import com.ap.menabev.dto.ToTaxDto;
import com.ap.menabev.dto.VendorCheckDto;
import com.ap.menabev.entity.PoHistoryDo;
import com.ap.menabev.entity.PoHistoryTotalsDo;
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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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

					// a. If success, and move on
					//
					// b. If error stop processing and send response to UI.
					//
					// 3. Determine Payment Term and dependent data
					//
					// a. Call determinePaymentTermsOdata service and
					// setPaymentTerms

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

	@Autowired
	PurchaseDocumentHeaderRepository poHeaderRepository;
	@Autowired
	PoHistoryRepository poHistoryRepository;
	@Autowired
	PoHistoryTotalsRepository poHistoryTotalsRepository;

	@Override
	public InvoiceHeaderDto postToERP(InvoiceHeaderDto invoiceHeaderDto) {
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
				// END - creating tax map
			}

			PostToERPRootDto rootDto = new PostToERPRootDto();
			PostToERPDto d = new PostToERPDto();

			d.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());

			if (invoiceHeaderDto.getTransactionType().contains("Invoice")
					|| invoiceHeaderDto.getTransactionType().equalsIgnoreCase("invoice"))
				d.setInvoiceInd("X");
			else if (invoiceHeaderDto.getTransactionType().contains("Credit")
					|| invoiceHeaderDto.getTransactionType().equalsIgnoreCase("Credit"))
				d.setInvoiceInd("");
			else
				d.setInvoiceInd("");

			String docDate = "\\/Date(" + invoiceHeaderDto.getDocumentDate() + ")\\/";
			d.setDocDate(docDate);
			String pstngDate = "\\/Date(" + invoiceHeaderDto.getPostingDate() + ")\\/";
			d.setPstngDate(pstngDate);
			String blineDate = "\\/Date(" + invoiceHeaderDto.getBaseLineDate() + ")\\/";
			d.setBlineDate(blineDate);
			d.setCompCode(invoiceHeaderDto.getCompanyCode());
			d.setCurrency(invoiceHeaderDto.getCurrency());
			invoiceHeaderDto.getInvoiceGross();
			d.setGrossAmount(String.valueOf(invoiceHeaderDto.getInvoiceTotal())); // checked
																					// with
																					// PK
																					// and
																					// setting
																					// invoice
																					// total
			d.setPmnttrms(invoiceHeaderDto.getPaymentTerms());
			d.setPmntBlock(invoiceHeaderDto.getPaymentBlock());
			d.setDelCosts(String.valueOf(invoiceHeaderDto.getUnplannedCost()));
			d.setPymtMeth(invoiceHeaderDto.getPaymentMethod());
			ToItem toItem = new ToItem();
			toItem.setResults(itemAccAssgnDtoList);
			d.setToItem(toItem);
			ToTax toTax = new ToTax();

			// for (ThreeWayInvoiceItemDto item : itemDtoList) {
			// Double itemTaxAmount = item.getTaxValue();
			// Double itemGrossPrice = item.getGrossPrice();
			// String itemTaxCode = item.getTaxCode();
			//
			// if (toTaxDtoMap.containsKey(itemTaxCode)) {
			// ToTaxDto valueDto = toTaxDtoMap.get(itemTaxCode);
			// valueDto.setTaxAmount(String.valueOf(Double.valueOf(valueDto.getTaxAmount())
			// + itemTaxAmount));
			// valueDto.setTaxBaseAmount(String.valueOf(Double.valueOf(valueDto.getTaxBaseAmount())
			// + itemGrossPrice));
			// toTaxDtoMap.put(itemTaxCode, valueDto);
			// } else {
			//
			// ToTaxDto valueDto = new ToTaxDto();
			// valueDto.setInvoiceReferenceNumber(invoiceHeaderDto.getInvoice_ref_number());
			// valueDto.setTaxCode(itemTaxCode);
			// valueDto.setTaxAmount(String.valueOf(itemTaxAmount));
			// valueDto.setTaxBaseAmount(String.valueOf(itemGrossPrice));
			// toTaxDtoMap.put(itemTaxCode, valueDto);
			// }
			// }
			List<ToTaxDto> results = new ArrayList<>();
			for (Map.Entry<String, ToTaxDto> entry : toTaxDtoMap.entrySet()) {
				results.add(entry.getValue());
			}
			toTax.setResults(results);
			d.setToTax(toTax);
			rootDto.setD(d);
			// post to erp
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String payload = objectMapper.writeValueAsString(rootDto);

			return null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return invoiceHeaderDto;

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
			for (ThreeWayInvoiceItemDto invoiceItemDto : itemList) {
				if (invoiceItemDto.getIsTwowayMatched() && invoiceItemDto.getItemStatusCode() != "NO-GRN") {
					List<ItemThreeWayMatchPaylod> eachItemThreewayMatchPayload = new ArrayList<>();
					String matchedItem = invoiceItemDto.getMatchDocItem();// po
																			// item
																			// no
					String matchedPo = String.valueOf(invoiceItemDto.getMatchDocNum());// po
																						// no

					List<PoHistoryDo> poHistoryDoList = poHistoryRepository.getByItemAndPO(matchedItem, matchedPo);
					List<PoHistoryDto> poHistoryDtoList = ObjectMapperUtils.mapAll(poHistoryDoList, PoHistoryDto.class);

					// pass serial_no if required in poHistoryTotal
					PoHistoryTotalsDo poHistoryTotalsDo = poHistoryTotalsRepository.getHistoryTotals(matchedItem,
							matchedPo);
					PoHistoryTotalsDto poHistoryTotalsDto = ObjectMapperUtils.map(poHistoryTotalsDo,
							PoHistoryTotalsDto.class);
					ConsumtionLogicOutputDto consumtionLogicOutput = cosumptionLogic(invoiceHeaderDto, invoiceItemDto,
							poHistoryDtoList, poHistoryTotalsDto);
					eachItemThreewayMatchPayload.addAll(consumtionLogicOutput.getItemThreeWayMatchPaylod());
					invoiceItemDto.setItemThreeWayMatchPayload(eachItemThreewayMatchPayload);
					itemThreeWayMatchPayload.addAll(consumtionLogicOutput.getItemThreeWayMatchPaylod());

				}
			}

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
			ResponseEntity<?> responseFromOData = threeWayMatchOdataCall(payload);
			System.err.println(responseFromOData.getStatusCode());
			String responseBody = responseFromOData.getBody().toString();
			ThreeWayMatchOdataDto threeWayMatchOdataResponse = objectMapper.readValue(responseBody,
					ThreeWayMatchOdataDto.class);
			List<ToMatchOutputDto> messageResponse = threeWayMatchOdataResponse.getD().getToMatchOutput().getResults();
			//get status code
			
			Boolean flag = false;
			for (ThreeWayInvoiceItemDto invoiceItemDto : itemList) {
				int priceMismatchCount = 0;
				int qtyMismatchCount = 0;
				if (!ServiceUtil.isEmpty(messageResponse)) {
					List<ItemMessageDto> messageItemList = invoiceItemDto.getInvoiceItemMessages();
					String reqId = invoiceItemDto.getRequestId();
					String itemCode = invoiceItemDto.getItemCode();
					for (ToMatchOutputDto toMatchOutputDto : messageResponse) {
						if (toMatchOutputDto.getRequestId().equalsIgnoreCase(reqId)
								&& toMatchOutputDto.getInvoiceItem().equalsIgnoreCase(itemCode)) {
							int msgNo = 1;
							ItemMessageDto dto = new ItemMessageDto();
							dto.setItemId(toMatchOutputDto.getInvoiceItem());
							dto.setMessageClass(toMatchOutputDto.getMessageClass());
							dto.setMessageId(String.valueOf(msgNo));
							dto.setMessageNo(toMatchOutputDto.getMessageNumber());
							dto.setMessageType(toMatchOutputDto.getMessageType());
							dto.setMessageText(toMatchOutputDto.getMessageText());
							messageItemList.add(dto);
							msgNo++;

							if (ApplicationConstants.QUANTITY_HIGH_MSG_NUMBER
									.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())
									) {
								qtyMismatchCount++;
							} else if (ApplicationConstants.PRICE_HIGH_MSG_NUMBER
									.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())
									|| ApplicationConstants.PRICE_LOW_MSG_NUMBER
											.equalsIgnoreCase(toMatchOutputDto.getMessageNumber())) {
								priceMismatchCount++;
							}
							
							if(priceMismatchCount>0 && qtyMismatchCount>0){
								invoiceItemDto.setItemStatusCode(ApplicationConstants.PRICE_OR_QTY_MISMATCH);
								invoiceItemDto.setItemStatusText("Price/Qty");
								
							}else{
								if(priceMismatchCount>0){
									invoiceItemDto.setItemStatusCode(ApplicationConstants.PRICE_MISMATCH);
									invoiceItemDto.setItemStatusText("Price Mismatch");
								}
								if(qtyMismatchCount>0){
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

				} else {
					flag = true;
					invoiceItemDto.setIsThreewayMatched(true);
					invoiceItemDto.setItemStatusCode("17");
					invoiceItemDto.setItemStatusText("Ready To Post");
				}

			}
			// if(flag){
			// threeWayMatchOutputDto.setInvoiceStatus("17");
			// threeWayMatchOutputDto.setInvoiceStatusText("Ready To Post");
			// }

			String headerStatus = duplicateCheckService.determineHeaderStatus(invoiceHeaderDto).getInvoiceStatus();
			String headerStatusText = duplicateCheckService.determineHeaderStatus(invoiceHeaderDto)
					.getInvoiceStatusText();
			threeWayMatchOutputDto.setInvoiceStatus(headerStatus);
			threeWayMatchOutputDto.setInvoiceStatusText(headerStatusText);
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

	private static String itemId = null;

	// public static void main(String[] args) {
	// ObjectMapper objectMapper = new ObjectMapper();
	// objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	// false);
	// String payload =
	// "{\"ToMatchInput\":{\"results\":[{\"AAAAAAAAAAA\":\"BBBBBBBB\",\"Vendor\":\"1000031\",\"InvoiceItem\":\"000001\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"200\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"200\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"},{\"AAAAAAAAAAA\":\"BBBBBBBB\",\"Vendor\":\"1000031\",\"InvoiceItem\":\"000002\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"10\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"10\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"}]}}";
	// try {
	// Reader reader = new StringReader(payload);
	// ThreeWayMatchingRootNode node = objectMapper.readValue(reader,
	// ThreeWayMatchingRootNode.class);
	// System.out.println(node);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	public static ResponseEntity<?> postToERPOdataCall(String entity) {

		try {

			ResponseEntity<?> responsePost = new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
			// call Destination Service
			Map<String, Object> destinationInfo = OdataHelperClass.getDestination("SD4_DEST");

			String endPointurl = "/sap/opu/odata/sap/ZP2P_API_POST_INVOICE_SRV/HeaderSet";
			// String entity =
			// "{\"d\":{\"Vendor\":\"1000031\",\"ToMatchInput\":{\"results\":[{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000001\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"200\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"200\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"},{\"Vendor\":\"1000031\",\"InvoiceItem\":\"000002\",\"PurchasingDocumentNumber\":\"4100000127\",\"PurchasingDocumentItem\":\"00010\",\"BaseUnit\":\"KG\",\"DntrConvOpuOu\":\"1\",\"NmtrConvOpuOu\":\"1\",\"QtyOrdPurReq\":\"100\",\"PostingDate\":\"20.05.2021\",\"CompanyCode\":\"1010\",\"ReferenceDocument\":\"5000000267\",\"Quantity\":\"10\",\"NetValDC\":\"400.00\",\"NoQuantityLogic\":\"\",\"ItemCategory\":\"0\",\"QtyInvoiced\":\"0\",\"ReturnsItem\":\"\",\"DrCrInd\":\"S\",\"SubDrCrInd\":\"\",\"CurrencyKey\":\"SAR\",\"GrBasedIvInd\":\"X\",\"QtyGoodsReceived\":\"100\",\"GoodsReceiptInd\":\"X\",\"TranslationDate\":\"20.05.2021\",\"UpdatePODelCosts\":\"\",\"PostInvInd\":\"X\",\"DelItemAllocInd\":\"X\",\"RetAllocInd\":\"X\",\"IvOrigin\":\"\",\"QtyOpu\":\"10\",\"InvValFC\":\"0.00\",\"EstPriceInd\":\"\",\"GrNonValInd\":\"\",\"NetValSrvFC\":\"0\",\"AmtDC\":\"400\",\"ValGrFC\":\"400\",\"NewInputVal\":\"\"}]},\"ToMatchOutput\":{\"results\":[]}}}";
			ResponseEntity<?> responseFromOdata = OdataHelperClass.getOdataServiceToken(endPointurl, null, "GET",
					destinationInfo);
			if (responseFromOdata.getStatusCodeValue() == 200) {
				Header[] header = (Header[]) responseFromOdata.getBody();

				responsePost = OdataHelperClass.postOdataServiceToken(endPointurl, entity, "POST", destinationInfo,
						header);
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
			if (responseFromOdata.getStatusCodeValue() == 200) {
				Header[] header = (Header[]) responseFromOdata.getBody();

				responsePost = OdataHelperClass.postOdataServiceToken(endPointurl, entity, "POST", destinationInfo,
						header);
			}
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
			Boolean invoiceReceiptFlag = invoiceItemDto.getIvFlag(); // "" null
			if (ServiceUtil.isEmpty(itemCategory)
					&& (ServiceUtil.isEmpty(accAssignmentCat) || accAssignmentCat.equalsIgnoreCase("K"))
					&& goodsReceiptFlag && grBsdIvFlag && invoiceReceiptFlag) {
				// Raw Mterial Scenario
				// invqty in po ou
				// invqty in po opu

				// invunitprice in po ou
				// invunitprice in po opu

				// invpriceunit (opu)
				Integer invpriceunitOPU = invoiceItemDto.getPricingUnit();

				Double invItemQtyPoOU = null;
				Double invItemQtyPoOPU = null;
				String uom = invoiceItemDto.getUom();

				if (uom.equals(invoiceItemDto.getOrderUnit())) {
					invItemQtyPoOU = invoiceItemDto.getInvQty();
				} else if (uom.equals(invoiceItemDto.getOrderPriceUnit())) {
					invItemQtyPoOPU = invoiceItemDto.getInvQty();
				}

				Integer den = invoiceItemDto.getConvDen1();
				Integer num = invoiceItemDto.getConvNum1();
				if (!ServiceUtil.isEmpty(invItemQtyPoOU)) {
					invItemQtyPoOPU = (num / den) * invItemQtyPoOU;
				} else if (!ServiceUtil.isEmpty(invItemQtyPoOPU)) {
					invItemQtyPoOU = (den / num) * invItemQtyPoOPU;
				}

				Double invoiceUnitPrice = (invoiceItemDto.getGrossPrice() * invpriceunitOPU)
						/ invoiceItemDto.getInvQty();

				// invoiceUnitPrice in po OU
				Double invoiceUnitPricePoOU = null;
				Double invoiceUnitPricePoOPU = null;
				// invoiceUnitPrice in po OPU

				if (uom.equals(invoiceItemDto.getOrderUnit())) {
					invoiceUnitPricePoOU = invoiceUnitPrice;
				} else if (uom.equals(invoiceItemDto.getOrderPriceUnit())) {
					invoiceUnitPricePoOPU = invoiceUnitPrice;
				}

				if (!ServiceUtil.isEmpty(invoiceUnitPricePoOU)) {
					invoiceUnitPricePoOPU = (den / num) * invoiceUnitPricePoOU;
				} else if (!ServiceUtil.isEmpty(invoiceUnitPricePoOPU)) {
					invoiceUnitPricePoOU = (num / den) * invoiceUnitPricePoOPU;
				}

				Double remainingQTY = invItemQtyPoOU;

				Double remainingAmount = invoiceItemDto.getGrossPrice();

				Double settledQTY = null;
				Double unSettledQTY = null;

				List<GRDto> grDtoList = new ArrayList<>();
				for (int i = 0; i < poHistoryDtoList.size(); i++) {

					PoHistoryDto poHistoryDto = poHistoryDtoList.get(i);

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
							if (poHistoryDto.getRefDocItem().equals(docItem)
									&& poHistoryDto.getRefDocNum().equals(docNum)) {
								grDto.setHDocStlQty(grDto.getHDocStlQty() + poHistoryDto.getQuantity());
								grDto.setHDocUnStlQty(grDto.getHDocQty() - grDto.getHDocStlQty());
							}
						}

					}

				}

				for (int i = 0; i < grDtoList.size(); i++) {
					if (remainingQTY > 0) {
						GRDto grDto = grDtoList.get(i);
						ItemThreeWayMatchPaylod inputPayload = new ItemThreeWayMatchPaylod();
						// 000001
						try {
							if (ServiceUtil.isEmpty(itemId)) {
								inputPayload.setInvoiceItem("000001");
							} else {
								inputPayload.setInvoiceItem(String.format("%06d", Integer.parseInt(itemId) + 1));
							}
						} catch (Exception e) {
							logger.error("threeway consumtion logic" + e.getMessage());
						}
						Double unStlQty = grDto.getHDocUnStlQty();
						if (remainingQTY >= unStlQty) {
							String[] str = String.valueOf(unStlQty).split(".");
							inputPayload.setQuantity(str[0]);
						} else {
							String[] str = String.valueOf(remainingQTY).split(".");
							inputPayload.setQuantity(str[0]);
						}
						remainingQTY = remainingQTY - Double.valueOf(inputPayload.getQuantity());

						if (i == grDtoList.size() - 1) {
							// last item
							if (remainingQTY > 0) {
								String qty = String.valueOf(Double.valueOf(inputPayload.getQuantity()) + remainingQTY);
								String[] qtyArr = qty.split(".");
								inputPayload.setQuantity(qtyArr[0]);
								remainingQTY = 0.0;
							}

						}
						// default values
						// set reqID and item id for ref
						inputPayload.setRequestId(invoiceHeaderDto.getRequestId());
						inputPayload.setReferenceInvoiceItem(invoiceItemDto.getItemCode());
						inputPayload.setVendor(invoiceHeaderDto.getVendorId());
						inputPayload.setPurchasingDocumentNumber(String.valueOf(invoiceItemDto.getMatchDocNum()));
						inputPayload.setPurchasingDocumentItem(invoiceItemDto.getMatchDocItem());
						inputPayload.setBaseUnit("");
						inputPayload.setDntrConvOpuOu(String.valueOf(den));
						inputPayload.setNmtrConvOpuOu(String.valueOf(num));
						inputPayload.setQtyOrdPurReq(String.valueOf(invoiceItemDto.getPoQtyOU()));
						inputPayload.setPostingDate(
								ServiceUtil.getDateByEpoc(invoiceHeaderDto.getPostingDate(), "dd.MM.yyyy"));
						inputPayload.setCompanyCode(invoiceHeaderDto.getCompanyCode());
						// change

						// inputPayload.setNetValDC(invoiceItemDto.g);//PO_NET_PRICE
						// in Invoice Item Table
						inputPayload.setNoQuantityLogic("");
						inputPayload.setItemCategory(invoiceItemDto.getItemCategory());
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
						String qtyOPU = String.valueOf((num / den) * Double.valueOf(inputPayload.getQuantity()));
						inputPayload.setQtyOpu(qtyOPU.split(".")[0]);
						inputPayload.setEstPriceInd("");
						// inputPayload.setGrNonValInd(invoiceItemDto.get);//
						// discuss with PK
						inputPayload.setNewInputVal("");
						inputPayload
								.setInvValFC(String.valueOf(grDto.getHDocStlQty() * invoiceItemDto.getPoUnitPriceOU()));

						String valgrFc = String.valueOf(grDto.getHDocQty() * invoiceItemDto.getPoUnitPriceOU());
						String[] valgrFcArr = valgrFc.split(".");
						inputPayload.setValGrFC(valgrFcArr[0]);
						// change
						if (ServiceUtil.isEmpty(accAssignmentCat) || (accAssignmentCat.equalsIgnoreCase("K")
								&& invoiceItemDto.getProductType().equalsIgnoreCase("1"))) {

							inputPayload.setReferenceDocument(grDto.getHdocNum());
							inputPayload.setNetValSrvFC("0");
						} else if (accAssignmentCat.equalsIgnoreCase("K")
								&& invoiceItemDto.getProductType().equalsIgnoreCase("2")) {
							inputPayload.setReferenceDocument(grDto.getRefDoc());
							inputPayload.setNetValSrvFC(inputPayload.getValGrFC());
						}

						if (i == grDtoList.size() - 1) {
							inputPayload.setAmtDC(String.valueOf(remainingAmount).split(".")[0]);
							remainingAmount = 0.0;
						} else {
							Double amountDC = invoiceUnitPricePoOU * (Double.valueOf(inputPayload.getQuantity())
									/ Double.valueOf(invoiceItemDto.getPricingUnit()));
							inputPayload.setAmtDC(String.valueOf(amountDC).split(".")[0]);
							remainingAmount = remainingAmount - amountDC;
						}

						// here we need to form inv_itm_acc_ass

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

						accAssgnDto.setTaxCode(invoiceItemDto.getTaxCode());
						accAssgnDto.setItemAmount(inputPayload.getAmtDC());
						accAssgnDto.setQuantity(inputPayload.getQuantity());
						accAssgnDto.setPoPrQnt(inputPayload.getQtyOpu());
						accAssgnDto.setPoUnit(invoiceItemDto.getOrderUnit());
						accAssgnDto.setPoPrUom(invoiceItemDto.getOrderPriceUnit());
						accAssgnDto.setPoPrUomIso("");
						accAssgnDto.setPoUnitIso("");

						//

						List<ToAccountingAccAssgnResultDto> resultsList = new ArrayList<>();
						if ("K".equalsIgnoreCase(accAssignmentCat)) {

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
								toAccAssgnDto.setCoArea("");// TODO to be added
															// by Lakhu in
															// InvoiceItemAcctAssignmentDto
								if (1 == 1) {
									// if in item distribution filed is blank &
									// partial inv indicator is blank --- > then
									// its a single acc assgn

									toAccAssgnDto.setItemAmount(inputPayload.getAmtDC());
									toAccAssgnDto.setQuantity(inputPayload.getQuantity());
									toAccAssgnDto.setPoPrQnt(inputPayload.getQtyOpu());
								} else if (2 == 2) {
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
						itemThreeWayAccAssgnPaylod.add(accAssgnDto);
						threeWayInputPayloadList.add(inputPayload);
					} else {
						break;
					}
				}

			} else {
				// nothing
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		consumtionLogicOutputDto.setItemThreeWayMatchPaylod(threeWayInputPayloadList);
		consumtionLogicOutputDto.setItemThreeWayAccAssgnPaylod(itemThreeWayAccAssgnPaylod);
		return consumtionLogicOutputDto;
	}

	// private Map<GRDto, List<IRDto>> getGrAndItem(List<PoHistoryDto>
	// poHistoryDtoList) {
	// // TODO Auto-generated method stub
	// Map<GRDto, List<IRDto>> map = new HashMap<>();
	// for (PoHistoryDto poHistoryDto : poHistoryDtoList) {
	// if ("E".equalsIgnoreCase(poHistoryDto.getHistoryCategory())) {
	// GRDto grDto = new GRDto();
	// map.put(grDto, null);
	//
	// } else if ("Q".equalsIgnoreCase(poHistoryDto.getHistoryCategory())) {
	// GRDto grDto = new GRDto();
	// grDto.setHDoc(poHistoryDto.getHistoryDocument());
	// grDto.setHDocItem(poHistoryDto.getHistoryDocumentItem());
	//
	// IRDto irDto = new IRDto();
	// // set grdto for the ir
	// if (map.containsKey(grDto)) {
	// List<IRDto> value = map.get(grDto);
	// value.add(irDto);
	// map.put(grDto, value);
	// } else {
	// List<IRDto> value = new ArrayList<>();
	// value.add(irDto);
	// map.put(grDto, value);
	// }
	// }
	// }
	//
	// return map;
	// }

}
