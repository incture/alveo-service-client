package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceHeaderObjectDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.MatchingHistoryDto;
import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.dto.TwoWayMatchInputDto;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.PurchaseDocumentHeaderRepository;
import com.ap.menabev.service.DuplicateCheckService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class DuplicatecheckServiceImpl implements DuplicateCheckService {

	@Autowired
	EntityManager entityManager;

	@Autowired
	InvoiceItemRepository invoiceItemRepository;

	@Autowired
	PurchaseDocumentHeaderRepository purchaseDocumentheaderRepository;

	@Autowired
	InvoiceHeaderServiceImpl invoiceHeaderServiceImpl;

	@Autowired
	MatchingHistoryServiceImpl matchingHistoryServiceImpl;

	@Override
	public InvoiceHeaderObjectDto duplicateCheck(InvoiceHeaderObjectDto dto) {
		ModelMapper mapper = new ModelMapper();
		InvoiceHeaderObjectDto returnDto = new InvoiceHeaderObjectDto();
		String requestId = "";
		String baseQuery = "Select requestId from InvoiceHeaderDo";
		String query = createStringForQuery(dto);
		String finalQuery = baseQuery;
		System.out.println(query);
		if (!ServiceUtil.isEmpty(query)) {
			int index = query.lastIndexOf(" ");
			String sb = query.substring(0, index - 3);
			finalQuery = finalQuery + " where " + sb;
		}
		System.out.println("Final Query ::::: " + finalQuery);

		Query queryForOutput = entityManager.createQuery(finalQuery);
		List<String> dos = queryForOutput.getResultList();
		if (ServiceUtil.isEmpty(dos)) {
			returnDto = mapper.map(dto, InvoiceHeaderObjectDto.class);
			returnDto.setIsDuplicate(false);
		} else {
			for (String itr : dos) {
				System.out.println("Here Java 49");
				requestId = requestId + itr + ",";

			}
			StringBuilder sb = new StringBuilder(requestId);
			sb.deleteCharAt(requestId.length() - 1);
			HeaderMessageDto message = new HeaderMessageDto();
			returnDto = mapper.map(dto, InvoiceHeaderObjectDto.class);
			returnDto.setIsDuplicate(true);
			message.setMessageId(20);
			message.setMsgClass("DuplicateCheck");
			message.setMessageNumber(sb.toString());
			message.setMessageText("Duplicate invoices found -<" + sb + ">");
			message.setMessageType("E");
			returnDto.setMessages(message);

		}

		return returnDto;
	}

	private String createStringForQuery(InvoiceHeaderObjectDto dto) {
		String query = "";
		if (!ServiceUtil.isEmpty(dto.getVendorId())) {
			query = query + "vendorId like '" + dto.getVendorId() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
			query = query + "invoiceStatus like '" + dto.getInvoiceStatus() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceReference())) {
			query = query + "extInvNum like '" + dto.getInvoiceReference() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceDate())) {
			query = query + "invoiceDate =" + dto.getInvoiceDate() + " and ";
		}
		if (!ServiceUtil.isEmpty(dto.getInvoiceAmount())) {
			query = query + "grossAmount like '" + dto.getInvoiceAmount() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getCompanyCode())) {
			query = query + "compCode like '" + dto.getCompanyCode() + "' and ";
		}
		return query;
	}

	@Override
	public InvoiceHeaderObjectDto vendorCheck(InvoiceHeaderObjectDto dto) {
		InvoiceHeaderObjectDto response = new InvoiceHeaderObjectDto();
		ModelMapper mapper = new ModelMapper();
		String message = "";
		response = mapper.map(dto, InvoiceHeaderObjectDto.class);
		// List<String> purchaseOrder =
		// invoiceItemRepository.PurchaseOrderByRequestId(dto.getRequestId());
		// if(ServiceUtil.isEmpty(purchaseOrder)){
		// return response;
		// }
		// List<String> matchedPurchaseOrder =
		// purchaseDocumentheaderRepository.matchedPurchaseOrder(purchaseOrder);
		String finalQuery = "select documentNumber from PurchaseDocumentHeaderDo where documentNumber in "
				+ "(select distinct refDocNum from InvoiceItemDo th where requestId = '" + dto.getRequestId() + "') "
				+ " and vendor <> '" + dto.getVendorId() + "'";
		Query queryForOutput = entityManager.createQuery(finalQuery);
		List<String> matchedPurchaseOrder = queryForOutput.getResultList();
		if (ServiceUtil.isEmpty(matchedPurchaseOrder)) {
			return response;
		} else {
			List<String> vendorCheck = purchaseDocumentheaderRepository.checkVendor(matchedPurchaseOrder,
					dto.getVendorId());
			for (String po : vendorCheck) {
				message = message + po + ",";
			}
			StringBuilder sb = new StringBuilder(message);
			sb.deleteCharAt(message.length() - 1);
			HeaderMessageDto messageHeader = new HeaderMessageDto();
			response = mapper.map(dto, InvoiceHeaderObjectDto.class);
			response.setIsDuplicate(true);
			messageHeader.setMessageId(20);
			messageHeader.setMsgClass("VendorCheck");
			messageHeader.setMessageNumber("<" + sb.toString() + ">");
			messageHeader.setMessageText("Vendor ID does not match with PO number -<" + sb + ">");
			messageHeader.setMessageType("E");
			response.setMessages(messageHeader);

		}
		return response;
	}

	public InvoiceHeaderDto determineHeaderStatus(InvoiceHeaderDto dto) {
		InvoiceHeaderDto checkStatus = new InvoiceHeaderDto();
		if (!ServiceUtil.isEmpty(dto)) {
			if (ServiceUtil.isEmpty(dto.getRequestId())) {
				ResponseEntity<?> response = invoiceHeaderServiceImpl.getInvoiceDetail(dto.getRequestId());
				if (response.getStatusCode().equals(200)) {
					checkStatus = (InvoiceHeaderDto) response.getBody();
				}
			} else if (!ServiceUtil.isEmpty(dto.getRequestId())) {
				checkStatus = dto;
			}
			// If invoiceHeaderStatus is NOT EQUAL to “PO Missing/Invalid” or
			// “Duplicate”
			if (!ServiceUtil.isEmpty(dto.getInvoiceStatus())) {
				if ((!ApplicationConstants.DUPLICATE_INVOICE.equals(checkStatus.getInvoiceStatus()))) {
					// 1. Loop at invoice line items
					for (InvoiceItemDto item : checkStatus.getInvoiceItems()) {

						if (ApplicationConstants.NO_GRN.equals(item.getItemStatusCode())) {
							// a. If itemStatus=No-GRN-04
							//
							// i. SetHeaderStatus=No-GRN
							checkStatus.setInvoiceStatus(ApplicationConstants.NO_GRN);
							checkStatus.setInvoiceStatusText("No GRN");
							// ii. Break out of the loop

							break;
						}
						if (ApplicationConstants.ITEM_MISMATCH.equals(item.getItemStatusCode())
								&& !ApplicationConstants.NO_GRN.equals(checkStatus.getInvoiceStatus())) {
							// b. If itemStatus=ItemMismatch AND HeaderStatus NE
							// NO-GRN
							//
							// i. Set HeaderStatus = ItemMismatch
							checkStatus.setInvoiceStatus(ApplicationConstants.ITEM_MISMATCH);
							checkStatus.setInvoiceStatusText("Item Mismatch");
							// ii. Break out of the loop
							break;
						}
						if (ApplicationConstants.PRICE_MISMATCH.equals(item.getItemStatusCode())
								&& (!ApplicationConstants.NO_GRN.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_MISMATCH
												.equals(checkStatus.getInvoiceStatus()))) {
							// c. If itemStatus=Price Mismtach and (HeaderStatus
							// NE NO-GRN or HeaderStatus NE ItemMismatch)
							//
							// i. HeaderStatus=PriceMismatch
							checkStatus.setInvoiceStatus(ApplicationConstants.PRICE_MISMATCH);
							checkStatus.setInvoiceStatusText("Price Mismatch");
						}
						if (ApplicationConstants.PRICE_OR_QTY_MISMATCH.equals(item.getItemStatusCode())
								&& (!ApplicationConstants.NO_GRN.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.ITEM_MISMATCH.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_MISMATCH
												.equals(checkStatus.getInvoiceStatus()))) {
							// d. If itemStatus=Price/Qty Mismatch and
							// (HeaderStatus NE NO-GRN or Header status NE
							// HeaderStatus NE ItemMismatch or headerStatus NE
							// Price Mismatch)
							//
							// i. Header status=Price/Qty Mismatch
							checkStatus.setInvoiceStatus(ApplicationConstants.QTY_MISMATCH);
							checkStatus.setInvoiceStatusText("Qty Mismatch");
						}
						if (ApplicationConstants.QTY_MISMATCH.equals(item.getItemStatusCode())
								&& (!ApplicationConstants.NO_GRN.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.ITEM_MISMATCH.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_MISMATCH.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_OR_QTY_MISMATCH
												.equals(checkStatus.getInvoiceStatus()))) {
							// e. If itemStatus=Qty Mismatch and (HeaderStatus
							// NE NO-GRN or Header status NE HeaderStatus NE
							// ItemMismatch or headerStatus NE Price Mismatch or
							// headerStatus NE Price/Qty Mismatch)
							//
							// i. HeaderStatus=QtyMismatch
							checkStatus.setInvoiceStatus(ApplicationConstants.QTY_MISMATCH);
							checkStatus.setInvoiceStatusText("Qty Mismatch");
						}
						if(ApplicationConstants.READY_TO_POST.equals(item.getItemStatusCode())
								&& (!ApplicationConstants.NO_GRN.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.ITEM_MISMATCH.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_MISMATCH.equals(checkStatus.getInvoiceStatus())
										|| !ApplicationConstants.PRICE_OR_QTY_MISMATCH
												.equals(checkStatus.getInvoiceStatus()) || !ApplicationConstants.QTY_MISMATCH
												.equals(checkStatus.getInvoiceStatus()))){
//							If ItemStatus=”ReadyToPost” and (HeaderStatus NE NO-GRN AND Header
//							status NE ItemMismatch AND headerStatus NE Price Mismatch AND headerStatus NE Price/Qty Mismatch AND HeaderStatus NE QtyMismatch)
//									i. Set HeaderStatus = “3Way Success”
							checkStatus.setInvoiceStatus(ApplicationConstants.THREE_WAY_MATCH_SUCCESS);
							checkStatus.setInvoiceStatusText("3Way Success");
						}

					} 
//					a. If HeaderStatus = “3way success” AND Balance is NE 0.00 then set headerStatus=”Balance Mismatch”
//
//							b. If HeaderStatus = “3way success” AND Balance = 0.00 then set headerStatus=”Ready to Post”

					if (!"0.00".equals(checkStatus.getBalanceAmount())
							&& ApplicationConstants.THREE_WAY_MATCH_SUCCESS.equals(checkStatus.getInvoiceStatus())) {
						checkStatus.setInvoiceStatus(ApplicationConstants.BALANCE_MISMATCH);
						checkStatus.setInvoiceStatusText("Balance Mismatch");
					}
					if ("0.00".equals(checkStatus.getBalanceAmount())
							&& ApplicationConstants.THREE_WAY_MATCH_SUCCESS.equals(checkStatus.getInvoiceStatus())) {
						checkStatus.setInvoiceStatus(ApplicationConstants.READY_TO_POST);
						checkStatus.setInvoiceStatusText("Ready To Post");
					}

				} else {
					// iv. Else
					//
					// 1. Do nothing
					return checkStatus;
				}
			}

		}
		return checkStatus;
	}

	@Override
	public InvoiceItemDto twoWayMatch(TwoWayMatchInputDto dto) {
		System.out.println("Item Match Starts");
		InvoiceItemDto invoiceItem = new InvoiceItemDto();
		invoiceItem = dto.getInvoiceItem();
		if (!ServiceUtil.isEmpty(dto)) {
			// Manual Matching
			if (!ServiceUtil.isEmpty(dto.getManualVsAuto())) {
				if (ApplicationConstants.MANUAL_MATCH.equals(dto.getManualVsAuto())) {
					System.out.println(dto.getPurchaseDocumentHeader().get(0).getPoItem().get(0));
					InvoiceItemDto invItemPoManual = matchPoToInvItem(dto, dto.getInvoiceItem(),
							dto.getPurchaseDocumentHeader().get(0).getPoItem().get(0),
							ApplicationConstants.MANUAL_MATCH);
					return invItemPoManual;

				}

				// Auto case
				else if (ApplicationConstants.AUTO_MATCH.equals(dto.getManualVsAuto())) {
					InvoiceItemDto itemReturn = dto.getInvoiceItem();
					System.out.println("Auto Match Starts");
					Boolean matchFound = false;
					int i = 0;
					for (i = 0; i < dto.getPurchaseDocumentHeader().size(); i++) {

						Integer matchValue = Integer.MIN_VALUE;
						for (PurchaseDocumentItemDto poItem : dto.getPurchaseDocumentHeader().get(i).getPoItem()) {
							if (!ServiceUtil.isEmpty(poItem.getVendMat())
									&& !ServiceUtil.isEmpty(itemReturn.getCustomerItemId())) {
								Integer fuzzySearchVMN = FuzzySearch.weightedRatio(poItem.getVendMat(),
										itemReturn.getCustomerItemId().toString());
								System.out.println("FUZZY_ MEAN::: A " + fuzzySearchVMN);
								if (fuzzySearchVMN == 100) {
									itemReturn = matchPoToInvItem(dto, dto.getInvoiceItem(), poItem,
											ApplicationConstants.AUTO_MATCH);
									matchFound = true;
									return itemReturn;
								} else {
									if (matchValue < fuzzySearchVMN) {
										matchValue = fuzzySearchVMN;
									}
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getMaterial())
									&& !ServiceUtil.isEmpty(itemReturn.getArticleNum())) {
								Integer fuzzySearchSapMatNo = FuzzySearch.weightedRatio(poItem.getMaterial(),
										itemReturn.getArticleNum());
								System.out.println("FUZZY_ MEAN::: B " + fuzzySearchSapMatNo);
								if (fuzzySearchSapMatNo == 100) {
									itemReturn = matchPoToInvItem(dto, dto.getInvoiceItem(), poItem,
											ApplicationConstants.AUTO_MATCH);
									matchFound = true;
									return itemReturn;
								} else {
									if (matchValue < fuzzySearchSapMatNo) {
										matchValue = fuzzySearchSapMatNo;
									}
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getInterArticleNum())
									&& !ServiceUtil.isEmpty(itemReturn.getUpcCode())) {
								Integer fuzzySearchUpcCode = FuzzySearch.weightedRatio(poItem.getInterArticleNum(),
										itemReturn.getUpcCode());
								System.out.println("FUZZY_ MEAN::: C " + fuzzySearchUpcCode);
								if (fuzzySearchUpcCode == 100) {
									itemReturn = matchPoToInvItem(dto, dto.getInvoiceItem(), poItem,
											ApplicationConstants.AUTO_MATCH);
									invoiceItem = itemReturn;
									matchFound = true;
									return itemReturn;
								} else {
									if (matchValue < fuzzySearchUpcCode) {
										matchValue = fuzzySearchUpcCode;
									}
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getShortText())
									&& !ServiceUtil.isEmpty(itemReturn.getItemText())) {
								Integer fuzzySearchDescription = FuzzySearch.weightedRatio(poItem.getShortText(),
										itemReturn.getItemText());
								System.out.println("FUZZY_ MEAN::: D " + fuzzySearchDescription);
								if (fuzzySearchDescription == 100) {
									itemReturn = matchPoToInvItem(dto, dto.getInvoiceItem(), poItem,
											ApplicationConstants.AUTO_MATCH);
									System.out.println("351 2WM ::::" + itemReturn);
									invoiceItem = itemReturn;
									System.out.println("353 2WM ::::" + invoiceItem);
									matchFound = true;
									return itemReturn;
								} else {
									if (matchValue < fuzzySearchDescription) {
										matchValue = fuzzySearchDescription;
									}
								}
							}
							if (matchFound) {
								break;
							} else if (matchValue > 70) {
								if (itemReturn.getInvQty() == poItem.getQuantity()
										&& itemReturn.getPoUnitPriceOPU() == poItem.getNetPrice()) {
									itemReturn = matchPoToInvItem(dto, dto.getInvoiceItem(), poItem,
											ApplicationConstants.AUTO_MATCH);
									matchFound = true;
									return itemReturn;

								}

							}

						}
					}
					if (!matchFound) {
						MatchingHistoryDto match = new MatchingHistoryDto();
						match.setVendorId(dto.getVendorId());// VendorId
						match.setCustomerItemIdVmn(String.valueOf(itemReturn.getCustomerItemId()));
						match.setIUpcCode(itemReturn.getUpcCode());
						match.setIText(itemReturn.getItemText());

						List<MatchingHistoryDto> matchResult = matchingHistoryServiceImpl.get(match);

						System.out.println("HERE ::: 384" + matchResult.size());
						if (matchResult.size() == 1) {

							for (i = 0; i < dto.getPurchaseDocumentHeader().size(); i++) {
								for (PurchaseDocumentItemDto poItemMatch : dto.getPurchaseDocumentHeader().get(i)
										.getPoItem()) {
									if (poItemMatch.getMaterial().equals(matchResult.get(0).getPMatNo())) {
										invoiceItem = matchPoToInvItem(dto, dto.getInvoiceItem(), poItemMatch,
												ApplicationConstants.AUTO_MATCH);
										return invoiceItem;
									}
								}
							}
						

						} else {
							return itemReturn;
						}
					}

				}
			}
		}

		return invoiceItem;
	}

	// Matching
	private InvoiceItemDto matchPoToInvItem(TwoWayMatchInputDto dto, InvoiceItemDto itemReturn,
			PurchaseDocumentItemDto poItem, String matchType) {

		itemReturn.setIsTwowayMatched(true);
		itemReturn.setMatchType(matchType);
		// itemReturn.setItemCode(ApplicationConstants.NEW_INVOICE);// Item
		// Status "New"
		itemReturn.setIsSelected(true);
		if (!ServiceUtil.isEmpty(poItem.getPriceUnit())) {
			itemReturn.setPricingUnit(poItem.getPriceUnit().intValue());
		}
		if (!ServiceUtil.isEmpty(poItem.getOrderPriceUnit())) {
			itemReturn.setUom(poItem.getOrderPriceUnit());
		}
		if (!ServiceUtil.isEmpty(poItem.getTaxCode())) {
			itemReturn.setTaxCode(poItem.getTaxCode());
		}
		if (!ServiceUtil.isEmpty(poItem.getPreqItem())) {
			itemReturn.setItemRequisationNum(poItem.getPreqItem());
		}
		if (!ServiceUtil.isEmpty(poItem.getPreqNum())) {
			itemReturn.setRequisationNum(poItem.getPreqNum());
		}
		if (!ServiceUtil.isEmpty(poItem.getContractNum())) {
			itemReturn.setContractNum(poItem.getContractNum());
		}
		if (!ServiceUtil.isEmpty(poItem.getContractItm())) {
			itemReturn.setContractItem(poItem.getContractItm());
		}

//		if ((("0".equals(poItem.getItemCategory())) && ServiceUtil.isEmpty(poItem.getAccountAssCat())
//				&& "1".equals(poItem.getProductType()))
//				|| ("0".equals(poItem.getItemCategory()) && ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat()))
//						&& "2".equals(poItem.getProductType()))
//				|| ("0".equals(poItem.getItemCategory()) && ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat()))
//						&& "1".equals(poItem.getProductType()) && "".equals(poItem.getMaterial())))  TODO  Discuss with PK
		if ((("0".equals(poItem.getItemCategory())) && ServiceUtil.isEmpty(poItem.getAccountAssCat())
				&& "1".equals(poItem.getProductType()))
				|| ("0".equals(poItem.getItemCategory()) && ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat()))
						&& "2".equals(poItem.getProductType()))
				|| ("0".equals(poItem.getItemCategory()) && ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat()))
						&& "1".equals(poItem.getProductType()))) {

			System.out.println("448");
			if(!ServiceUtil.isEmpty(poItem.getShortText())){
				itemReturn.setPoItemText(poItem.getShortText());
			}
			if(!ServiceUtil.isEmpty(poItem.getInterArticleNum())){
				itemReturn.setArticleNum(poItem.getInterArticleNum());
			}
			if (!ServiceUtil.isEmpty(poItem.getDocumentItem())) {
				itemReturn.setMatchDocItem(poItem.getDocumentItem());
			}
			if (!ServiceUtil.isEmpty(poItem.getDocumentNumber())) {
				itemReturn.setMatchDocNum(Long.valueOf(poItem.getDocumentNumber()));
			}
			if (!ServiceUtil.isEmpty(poItem.getItemCategory())) {
				itemReturn.setItemCategory(poItem.getItemCategory());
			}
			if (!ServiceUtil.isEmpty(poItem.getProductType())) {
				itemReturn.setProductType(poItem.getProductType());
			}
			if (!ServiceUtil.isEmpty(poItem.getMaterial())) {
				itemReturn.setSetPoMaterialNum(poItem.getMaterial());
			}
			if (!ServiceUtil.isEmpty(poItem.getQuantity())) {
				itemReturn.setPoQtyOU(poItem.getQuantity());
			}
			if (!ServiceUtil.isEmpty(poItem.getPoUnit())) {
				itemReturn.setOrderUnit(poItem.getPoUnit());
			}
			if (!ServiceUtil.isEmpty(poItem.getOrderPriceUnit())) {
				itemReturn.setOrderPriceUnit(poItem.getOrderPriceUnit());
			}
			if (!ServiceUtil.isEmpty(poItem.getNetPrice())) {
				itemReturn.setPoUnitPriceOPU(poItem.getNetPrice());
			}
			if (!ServiceUtil.isEmpty(poItem.getGrBsdIVInd())) {
				itemReturn.setGrBsdIv(Boolean.valueOf(poItem.getGrBsdIVInd()));
			}
			if (!ServiceUtil.isEmpty(poItem.getGrInd())) {
				itemReturn.setGrFlag(Boolean.valueOf(poItem.getGrInd()));
			}
			if (!ServiceUtil.isEmpty(poItem.getIrInd())) {
				itemReturn.setIvFlag((Boolean.valueOf(poItem.getIrInd())));
			}
			if (!ServiceUtil.isEmpty(poItem.getSrvBsdIVInd())) {
				itemReturn.setSrvBsdIv((Boolean.valueOf(poItem.getSrvBsdIVInd())));
			}
			if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()) && !ServiceUtil.isEmpty(poItem.getTaxCode())) {
				Double sysSugTax = (itemReturn.getGrossPrice() * Double.valueOf(poItem.getTaxCode())) / 100;
				itemReturn.setSysSuggTax(sysSugTax);
			}
			if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()) && !ServiceUtil.isEmpty(itemReturn.getTaxValue())) {
				Double taxValue = (itemReturn.getGrossPrice() * Double.valueOf(itemReturn.getTaxValue())) / 100;
				itemReturn.setTaxValue(taxValue);
			}
			if (!ServiceUtil.isEmpty(poItem.getConvNum1())) {
				itemReturn.setConvNum1(poItem.getConvNum1().intValue());
			}
			if (!ServiceUtil.isEmpty((poItem.getConvDen1()))) {
				itemReturn.setConvDen1(poItem.getConvDen1().intValue());
			}
			if (!ServiceUtil.isEmpty(poItem.getAccountAssCat())) {
				itemReturn.setAccountAssignmentCat(poItem.getAccountAssCat());
			}
			if(!ServiceUtil.isEmpty(poItem.getGr_non_val())){
				itemReturn.setGrNonValInd(poItem.getGr_non_val());
			}
			if(!ServiceUtil.isEmpty(poItem.getDistribution())){
				itemReturn.setDistributionInd(poItem.getDistribution());
			}
			if(!ServiceUtil.isEmpty(poItem.getPartInv())){
				itemReturn.setPartialInvInd(poItem.getPartInv());
			}

			// vii. Set InvItem-poQtuOpu = poItem-qtyOpu//TODO
			// itemReturn.setPoQtyOPU(poItem.get);
			// viii. Set InvItem-orderUnit = poItem-orderUnit??

			Double deliv_qty = null;
			Double invoiced_qty = null;
			if (!ServiceUtil.isEmpty(dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals())) {
				if(!ServiceUtil.isEmpty(dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals().get(0).getDelivQty())){
					deliv_qty = dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals().get(0).getDelivQty();
				}
			}
			if (!ServiceUtil.isEmpty(dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals())) {
				if(!ServiceUtil.isEmpty(dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals().get(0).getIvQty())){
					invoiced_qty = dto.getPurchaseDocumentHeader().get(0).getPoHistoryTotals().get(0).getIvQty();
				}
			} // TODO

			if (poItem.getOrderPriceUnit().equals(poItem.getPoUnit())) {
				if (!ServiceUtil.isEmpty(deliv_qty) && !ServiceUtil.isEmpty(invoiced_qty)) {
					itemReturn.setAlvQtyUOM(deliv_qty - invoiced_qty);
				}
				if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyUOM())) {
					itemReturn.setAlvQtyOU(itemReturn.getAlvQtyUOM());
				}
				if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyUOM())) {
					itemReturn.setAlvQtyOPU(itemReturn.getAlvQtyUOM());
				}
				if (!ServiceUtil.isEmpty(poItem.getNetPrice())) {
					itemReturn.setPoUnitPriceUOM(poItem.getNetPrice());
				}
				if (!ServiceUtil.isEmpty(poItem.getNetPrice())) {
					itemReturn.setPoUnitPriceOPU(poItem.getNetPrice());
				}
				if (!ServiceUtil.isEmpty(poItem.getNetPrice())) {
					itemReturn.setPoUnitPriceOU(poItem.getNetPrice());
				}

				// Account Assignment
				if (!ServiceUtil.isEmpty(poItem.getAccountAssCat())) {
					if ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat()) ) {
						List<InvoiceItemAcctAssignmentDto> accountAssignmentArray = new ArrayList<>();
						// For Single
						if ("".equals(poItem.getDistribution())) {
							InvoiceItemAcctAssignmentDto accountAssignment = new InvoiceItemAcctAssignmentDto();
							if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
								accountAssignment.setNetValue(itemReturn.getGrossPrice());
							}
							if (!ServiceUtil.isEmpty(itemReturn.getInvQty())) {
								accountAssignment.setQty(itemReturn.getInvQty());
							}
							if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
								accountAssignment.setQtyUnit(itemReturn.getUom());
							}

							// iv. InvItem-ItemAccountAssignment-netValueOu =
							// fn(invItem-grossPrice)
							// (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()))
							// {
							// accountAssignment.setNetValue(itemReturn.getGrossPrice());
							// }TODO
							if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOU())) {
								accountAssignment.setAvlQtyOU((itemReturn.getAlvQtyOU()));
							} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAvlQtyOU(qtyOpuToOu(itemReturn.getConvNum1(),
										itemReturn.getConvDen1(), itemReturn.getAlvQtyOPU()));
							}

							// InvItem-ItemAccountAssignment-OrderUnit =
							// poItem-OrderUnit TODO
							if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAlvQtyOPU((itemReturn.getAlvQtyOPU()));
							} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAlvQtyOPU(qtyOuToOpu(itemReturn.getConvNum1(),
										itemReturn.getConvDen1(), itemReturn.getAlvQtyOU()));
							}
							// viii. InvItem-ItemAccountAssignment-netValueOpu =
							// fn(invItem-grossPrice)
							// Only Net value is there TODO
							// ix. InvItem-ItemAccountAssignment-OrderPriceUnit
							accountAssignment.setPoUnitPriceOPU(Double.valueOf(poItem.getOrderPriceUnit()));
							// TODO
							// poItem-OrderPriceUnit
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCostCenter())){
									accountAssignment.setCostCenter(poItem.getPoAccountAssigment().get(0).getCostCenter());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getGlAccount())){
									accountAssignment.setGlAccount(poItem.getPoAccountAssigment().get(0).getGlAccount());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCoArea())){
									accountAssignment.setCoArea(poItem.getPoAccountAssigment().get(0).getCoArea());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getProfitCtr())){
									accountAssignment.setProfitCtr(poItem.getPoAccountAssigment().get(0).getProfitCtr());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getOrderId())){
									accountAssignment.setOrderId(poItem.getPoAccountAssigment().get(0).getOrderId());
								}
							}
							

							accountAssignmentArray.add(accountAssignment);

						}
						// For multiple with % distribution
						else if ("2".equals(poItem.getDistribution())) {
							int i = 0;
							for (InvoiceItemAcctAssignmentDto accountAssignment : itemReturn.getInvItemAcctDtoList()) {
								if (!ServiceUtil.isEmpty(itemReturn.getInvItemAcctDtoList().get(i).getDistPerc())
										&& !ServiceUtil.isEmpty(itemReturn.getInvQty())
										&& !ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
									Double qty = (itemReturn.getInvItemAcctDtoList().get(i).getDistPerc()
											* itemReturn.getInvQty()) / 100;
									Double netValue = (itemReturn.getInvItemAcctDtoList().get(i).getDistPerc()
											* itemReturn.getGrossPrice()) / 100;
									accountAssignment.setNetValue(netValue);
									accountAssignment.setQty(qty);
								}

								// if
								// (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()))
								// {
								// accountAssignment.setNetValue(itemReturn.getGrossPrice());
								// }
								// if
								// (!ServiceUtil.isEmpty(itemReturn.getInvQty()))
								// {
								// accountAssignment.setQty(itemReturn.getInvQty());
								// }
								if (!ServiceUtil.isEmpty(itemReturn.getUom())) {
									accountAssignment.setQtyUnit(itemReturn.getUom());
								}
								// v. InvItem-ItemAccountAssignment-netValueOu =
								// fn(invItem-grossPrice)
								// TODO same field
								// vi. InvItem-ItemAccountAssignment-qtyOu =
								if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOU())) {
									accountAssignment.setAvlQtyOU((itemReturn.getAlvQtyOU()));
								} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAvlQtyOU(qtyOpuToOu(itemReturn.getConvNum1(),
											itemReturn.getConvDen1(), itemReturn.getAlvQtyOPU()));
								}
								// vii. InvItem-ItemAccountAssignment-OrderUnit/
								// =
								// poItem-OrderUnit//TODO
								if (!ServiceUtil.isEmpty(poItem.getPoUnit())) {
									// accountAssignment.setPoUnitPriceOU(poUnitPriceOU);
								}
								if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAlvQtyOPU((itemReturn.getAlvQtyOPU()));
								} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAlvQtyOPU(qtyOuToOpu(itemReturn.getConvNum1(),
											itemReturn.getConvDen1(), itemReturn.getAlvQtyOU()));
								}
								// ix. InvItem-ItemAccountAssignment-netValueOpu
								// =
								// fn(invItem-grossPrice)
								//
								// x.
								// InvItem-ItemAccountAssignment-OrderPriceUnit
								// poItem-OrderPriceUnit
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(i).getCostCenter())){
										accountAssignment
										.setCostCenter(poItem.getPoAccountAssigment().get(i).getCostCenter());
									}
									
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(i).getGlAccount())){
										accountAssignment
										.setGlAccount(poItem.getPoAccountAssigment().get(i).getGlAccount());
									}
									
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCoArea())){
										accountAssignment.setCoArea(poItem.getPoAccountAssigment().get(0).getCoArea());
									}
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getProfitCtr())){
										accountAssignment.setProfitCtr(poItem.getPoAccountAssigment().get(0).getProfitCtr());
									}
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getOrderId())){
										accountAssignment.setOrderId(poItem.getPoAccountAssigment().get(0).getOrderId());
									}
								}
								accountAssignmentArray.add(accountAssignment);
								i++;
							}
						}
						itemReturn.setInvItemAcctDtoList(accountAssignmentArray);
						itemReturn.setIsAccAssigned(true);
					}

				}

			} else {

				// 1. InvItem-uom = poItem-opu.
				itemReturn.setUom(poItem.getOrderPriceUnit());

				// 2. Invitem- AvlQty(OU) = poHistoryTotals-deliv_qty –
				// poHistoryTotals-invoicedQty.
				itemReturn.setAlvQtyOU(deliv_qty - invoiced_qty);

				// 3. Invitem-AvlQty(UoM) = fn (covert invItem-avlQtyOu to OPU)
				if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
					itemReturn.setAlvQtyUOM(
							qtyOuToOpu(itemReturn.getConvNum1(), itemReturn.getConvDen1(), itemReturn.getAlvQtyOU()));
				}

				// 4. Invitem- AvlQty(OPU) = Invitem-AvlQty(UoM)
				itemReturn.setAlvQtyOPU(itemReturn.getAlvQtyUOM());

				// 5. Invitem- PoUnitPrice(UoM) = poItem-NetPrice
				itemReturn.setPoUnitPriceUOM(poItem.getNetPrice());
				// 6. Invitem- PoUnitPrice(OPU) = poItem-NetPrice
				itemReturn.setPoUnitPriceOPU(poItem.getNetPrice());

				// 7. Invitem- PoUnitPriceOu = fn(convert poItem-NetPrice from
				// opu to ou)
				if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
					itemReturn.setPoUnitPriceOU(
							qtyOpuToOu(itemReturn.getConvNum1(), itemReturn.getConvDen1(), poItem.getNetPrice()));
				}
				// 5. Invitem- PoUnitPrice(OPU) = poItem-NetPrice
				itemReturn.setPoUnitPriceOPU(poItem.getNetPrice());
				// 6. Invitem- PoUnitPrice(OU) = fn(OU)
				if (!ServiceUtil.isEmpty(poItem.getAccountAssCat())) {
					if ("K".equals(poItem.getAccountAssCat()) || "F".equals(poItem.getAccountAssCat())) {
						List<InvoiceItemAcctAssignmentDto> accountAssignmentArray = new ArrayList<>();
						// For Single
						if ("".equals(poItem.getDistribution())) {
							InvoiceItemAcctAssignmentDto accountAssignment = new InvoiceItemAcctAssignmentDto();
							if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
								accountAssignment.setNetValue(itemReturn.getGrossPrice());
							}
							if (!ServiceUtil.isEmpty(itemReturn.getInvQty())) {
								accountAssignment.setQty(itemReturn.getInvQty());
							}
							if (!ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
								accountAssignment.setQtyUnit(itemReturn.getUom());
							}

							// iv. InvItem-ItemAccountAssignment-netValueOu =
							// fn(invItem-grossPrice)
							// (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()))
							// {
							// accountAssignment.setNetValue(itemReturn.getGrossPrice());
							// }TODO
							if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOU())) {
								accountAssignment.setAvlQtyOU((itemReturn.getAlvQtyOU()));
							} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAvlQtyOU(qtyOpuToOu(itemReturn.getConvNum1(),
										itemReturn.getConvDen1(), itemReturn.getAlvQtyOPU()));
							}

							// InvItem-ItemAccountAssignment-OrderUnit =
							// poItem-OrderUnit TODO
							if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAlvQtyOPU((itemReturn.getAlvQtyOPU()));
							} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
								accountAssignment.setAlvQtyOPU(qtyOuToOpu(itemReturn.getConvNum1(),
										itemReturn.getConvDen1(), itemReturn.getAlvQtyOU()));
							}
							// viii. InvItem-ItemAccountAssignment-netValueOpu =
							// fn(invItem-grossPrice)
							// Only Net value is there TODO
							// ix. InvItem-ItemAccountAssignment-OrderPriceUnit
							accountAssignment.setPoUnitPriceOPU(Double.valueOf(poItem.getOrderPriceUnit()));
							// TODO
							// poItem-OrderPriceUnit
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCostCenter())) {
								accountAssignment.setCostCenter(poItem.getPoAccountAssigment().get(0).getCostCenter());
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getGlAccount())) {
								accountAssignment.setGlAccount(poItem.getPoAccountAssigment().get(0).getGlAccount());
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCoArea())){
									accountAssignment.setCoArea(poItem.getPoAccountAssigment().get(0).getCoArea());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getProfitCtr())){
									accountAssignment.setProfitCtr(poItem.getPoAccountAssigment().get(0).getProfitCtr());
								}
							}
							if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
								if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getOrderId())){
									accountAssignment.setOrderId(poItem.getPoAccountAssigment().get(0).getOrderId());
								}
							}

							accountAssignmentArray.add(accountAssignment);

						}
						// For multiple with % distribution
						else if ("2".equals(poItem.getDistribution())) {
							int i = 0;
							for (InvoiceItemAcctAssignmentDto accountAssignment : itemReturn.getInvItemAcctDtoList()) {
								if (!ServiceUtil.isEmpty(itemReturn.getInvItemAcctDtoList().get(i).getDistPerc())
										&& !ServiceUtil.isEmpty(itemReturn.getInvQty())
										&& !ServiceUtil.isEmpty(itemReturn.getGrossPrice())) {
									Double qty = (itemReturn.getInvItemAcctDtoList().get(i).getDistPerc()
											* itemReturn.getInvQty()) / 100;
									Double netValue = (itemReturn.getInvItemAcctDtoList().get(i).getDistPerc()
											* itemReturn.getGrossPrice()) / 100;
									accountAssignment.setNetValue(netValue);
									accountAssignment.setQty(qty);
								}

								// if
								// (!ServiceUtil.isEmpty(itemReturn.getGrossPrice()))
								// {
								// accountAssignment.setNetValue(itemReturn.getGrossPrice());
								// }
								// if
								// (!ServiceUtil.isEmpty(itemReturn.getInvQty()))
								// {
								// accountAssignment.setQty(itemReturn.getInvQty());
								// }
								if (!ServiceUtil.isEmpty(itemReturn.getUom())) {
									accountAssignment.setQtyUnit(itemReturn.getUom());
								}
								// v. InvItem-ItemAccountAssignment-netValueOu =
								// fn(invItem-grossPrice)
								// TODO same field
								// vi. InvItem-ItemAccountAssignment-qtyOu =
								if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOU())) {
									accountAssignment.setAvlQtyOU((itemReturn.getAlvQtyOU()));
								} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAvlQtyOU(qtyOpuToOu(itemReturn.getConvNum1(),
											itemReturn.getConvDen1(), itemReturn.getAlvQtyOPU()));
								}
								// vii. InvItem-ItemAccountAssignment-OrderUnit/
								// =
								// poItem-OrderUnit//TODO
								if (!ServiceUtil.isEmpty(poItem.getPoUnit())) {
									// accountAssignment.setPoUnitPriceOU(poUnitPriceOU);
								}
								if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAlvQtyOPU((itemReturn.getAlvQtyOPU()));
								} else if (!ServiceUtil.isEmpty(itemReturn.getAlvQtyOPU())) {
									accountAssignment.setAlvQtyOPU(qtyOuToOpu(itemReturn.getConvNum1(),
											itemReturn.getConvDen1(), itemReturn.getAlvQtyOU()));
								}
								// ix. InvItem-ItemAccountAssignment-netValueOpu
								// =
								// fn(invItem-grossPrice)
								//
								// x.
								// InvItem-ItemAccountAssignment-OrderPriceUnit
								// poItem-OrderPriceUnit
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(i).getCostCenter())){
										accountAssignment
										.setCostCenter(poItem.getPoAccountAssigment().get(i).getCostCenter());
									}
									
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(i).getGlAccount())){
										accountAssignment
										.setGlAccount(poItem.getPoAccountAssigment().get(i).getGlAccount());
									}
									
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getCoArea())){
										accountAssignment.setCoArea(poItem.getPoAccountAssigment().get(0).getCoArea());
									}
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getProfitCtr())){
										accountAssignment.setProfitCtr(poItem.getPoAccountAssigment().get(0).getProfitCtr());
									}
								}
								if (!ServiceUtil.isEmpty(poItem.getPoAccountAssigment())) {
									if(!ServiceUtil.isEmpty(poItem.getPoAccountAssigment().get(0).getOrderId())){
										accountAssignment.setOrderId(poItem.getPoAccountAssigment().get(0).getOrderId());
									}
								}
								accountAssignmentArray.add(accountAssignment);
								i++;
							}
						}
						itemReturn.setInvItemAcctDtoList(accountAssignmentArray);
						itemReturn.setIsAccAssigned(true);
					}
				}
			}
		}
		System.out.println("No GRN TEST Outside" + dto.getPurchaseDocumentHeader().get(0));
		if (ServiceUtil.isEmpty(dto.getPurchaseDocumentHeader().get(0).getPoHistory())) {
			System.out.println("No GRN TEST" + dto.getPurchaseDocumentHeader().get(0));
			itemReturn.setItemStatusCode(ApplicationConstants.NO_GRN);
			itemReturn.setItemStatusText("No GRN");
			itemReturn.setIsSelected(false);
		}
		// Calculate sysSuggTaxAmount
		//
		// 1. SysSuggTaxAmount = )tax% of InvAmountBeforeTax.) +
		// InvAmountBeforeTax.
		// Double sysSuggTax = ((itemReturn.getGrossPrice() *
		// Double.valueOf(itemReturn.getTaxCode())) / 100)
		// + itemReturn.getGrossPrice();
		// itemReturn.setSysSuggTax(sysSuggTax);
		return itemReturn;

	}

	private Double qtyOuToOpu(Integer convNum, Integer convDen, Double qTyOu) {
		return (convNum / convDen) * qTyOu;
	}

	private Double qtyOpuToOu(Integer convNum, Integer convDen, Double qTyOpu) {
		return (convDen / convNum) * qTyOpu;
	}

	@SuppressWarnings("unused")
	private Double unitPriceOuToOpu(Integer convNum, Integer convDen, Double unitPriceOu) {
		return (convDen / convNum) * unitPriceOu;
	}

	@SuppressWarnings("unused")
	private Double unitPriceOpuToOu(Integer convNum, Integer convDen, Double unitPriceOpu) {
		return (convNum / convDen) * unitPriceOpu;
	}

	// public static void main(String[] args) {
	// Double b = 1.0;
	// Integer a = b.intValue();
	// System.out.println(a);
	// }

}
