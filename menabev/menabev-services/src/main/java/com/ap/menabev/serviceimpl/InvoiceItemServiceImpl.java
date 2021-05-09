package com.ap.menabev.serviceimpl;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDashBoardDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ManualMatchingDto;
import com.ap.menabev.dto.PurchaseDocumentItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;
import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.service.PurchaseDocumentItemService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;



@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceItemServiceImpl.class);

	@Autowired
	InvoiceItemRepository invoiceItemRepository;

	@Autowired
	PurchaseDocumentItemService purchaseDocumentItemService;

	@Autowired
	InvoiceHeaderServiceImpl invoiceHeaderServiceImpl;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;

	@Override
	public ResponseDto save(InvoiceItemDto dto) {
		logger.error(dto.toString());
		ResponseDto response = new ResponseDto();
		ModelMapper mapper = new ModelMapper();

		try {
			if (dto != null)
				invoiceItemRepository.save(mapper.map(dto, InvoiceItemDo.class));
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}
	}

	@Override
	public DashBoardDetailsDto saveAll(List<InvoiceItemDto> dtoList) {

		DashBoardDetailsDto boardDetailsDto = new DashBoardDetailsDto();
		ResponseDto response = new ResponseDto();
		List<InvoiceItemDo> doList = new ArrayList<>();
		ModelMapper mapper = new ModelMapper();
		try {
			dtoList.forEach(dto -> doList.add(mapper.map(dto, InvoiceItemDo.class)));
			invoiceItemRepository.saveAll(doList);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			boardDetailsDto.setInvoiceItems(exportDashboradItemDtoList(dtoList));
			boardDetailsDto.setResponse(response);
			return boardDetailsDto;
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			boardDetailsDto.setInvoiceItems(new ArrayList<>());
			boardDetailsDto.setResponse(response);
			return boardDetailsDto;
		}
	}


	private List<InvoiceItemDashBoardDto> exportDashboradItemDtoList(List<InvoiceItemDto> dtoList) {
		ModelMapper mapper = new ModelMapper();
		List<InvoiceItemDashBoardDto> listDashBoardItemDto = new ArrayList<>();
		for (InvoiceItemDto dto : dtoList) {
			listDashBoardItemDto.add(mapper.map(dto, InvoiceItemDashBoardDto.class));
		}
		return listDashBoardItemDto;
	}

	@Override
	public List<InvoiceItemDto> getAll() {
		ModelMapper mapper = new ModelMapper();
		List<InvoiceItemDto> list = new ArrayList<>();
		try {
			List<InvoiceItemDo> entityList = invoiceItemRepository.findAll();
			for (InvoiceItemDo invoiceItemDo : entityList) {
				list.add(mapper.map(invoiceItemDo, InvoiceItemDto.class));
			}
			return list;
		} catch (Exception e) {
			return list;
		}
	}

	@Override
	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			invoiceItemRepository.deleteById(id);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.DELETE_SUCCESS);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
			return response;
		}
	}

	@Override
	public List<InvoiceItemDto> findAllByLimit(int limit, int offset) {
		if (limit >= 0 && offset >= 0) {
			List<InvoiceItemDto> dtoList = new ArrayList<>();
			List<InvoiceItemDo> entityList = invoiceItemRepository.findAllByLimit(limit, offset);
			ModelMapper mapper = new ModelMapper();
			for (InvoiceItemDo invoiceItemDo : entityList) {
				dtoList.add(mapper.map(invoiceItemDo, InvoiceItemDto.class));
			}
			return dtoList;
		} else {
			return getAll();
		}
	}

	public String getItemId() {
		try {
			String itemId = invoiceItemRepository.getItemId();
			if (ServiceUtil.isEmpty(itemId))
				return "000001";
			return String.format("%06d", Integer.parseInt(itemId) + 1);
		} catch (Exception e) {
			logger.error(
					"[ApAutomation][InvoiceHeaderServiceImpl][getDetailsForFilter][Exception] = " + e.getMessage());
		}
		return null;
	}

	/*
	 * Two Way Matching ----FUNCTION CREATED BY PRIYADHARSHINI CASE
	 * :VendorMaterial in InvoiceItem = VendorMaterial in PurchaseDocumentItem
	 * (OR) UPC code in InvoiCeItem= UPC code in PurchaseDocumentItem If matches
	 * Setting isTwoWayMatched in InvoiceItem to TRUE Storing DocumentNumber of
	 * PurchaseDocumentItem into MatchedDocumentNumber of InvoiceItem Storing
	 * DocumentItem of PurchaseDocumentItem into MatchedDocumentItem of
	 * InvoiceItem Else Setting isTwoWayMatched in InvoiceItem to FALSE
	 * 
	 */

	private static Double stringToDouble(String in) {
		if (ServiceUtil.isEmpty(in)) {
			in = "0";
		}

		if (in.matches("-?\\d+(\\.\\d+)?"))
			return new Double(in);
		else {
			return Double.valueOf(0.0);
		}
	}

	@Override
	public void update(InvoiceItemDashBoardDto dto) {
		// TODO Auto-generated method stub

		InvoiceItemDo itDo = new InvoiceItemDo();
		itDo.setAmountDifference(dto.getAmountDifference());
		itDo.setCreatedAtInDB(dto.getCreatedAtInDB());
		itDo.setCreatedByInDb(dto.getCreatedByInDb());
		itDo.setCurrency(dto.getCurrency());
		itDo.setCustomerItemId(Integer.valueOf(dto.getCustomerItemID() == null ? "0" : dto.getCustomerItemID()));
		itDo.setDisAmt(dto.getDisAmt());
		itDo.setDisPer(dto.getDisPer());
		itDo.setExtItemId(dto.getExtItemId());
		itDo.setId(dto.getId());
		itDo.setInvQty(dto.getInvQty());
		itDo.setIsAccAssigned(dto.getIsAccAssigned());
		itDo.setIsDeleted(dto.getIsDeleted());
		itDo.setIsSelected(dto.getIsSelected());
		itDo.setIsThreewayMatched(dto.getIsThreewayMatched());
		itDo.setIsTwowayMatched(dto.getIsTwowayMatched());
		itDo.setItemCode(dto.getItemCode());
		itDo.setItemComment(dto.getItemComment());
		itDo.setItemId(dto.getItemId());
		itDo.setItemLifeCycleStatus(dto.getItemLifeCycleStatus());
		itDo.setItemText(dto.getItemText());
		itDo.setMatchDocItem(dto.getMatchDocItem());
		itDo.setMatchDocNum(dto.getMatchDocNum());
		itDo.setMatchedBy(dto.getMatchedBy());
		itDo.setMatchParam(dto.getMatchParam());
		itDo.setNetWorth(dto.getNetWorth());
		itDo.setPoAvlQtyOU(dto.getPoAvlQtyOU());
		itDo.setPoMaterialNum(dto.getPoMaterialNum());
		itDo.setPoNetPrice(dto.getPoNetPrice());
		itDo.setPoTaxCode(dto.getPoTaxCode());
		itDo.setPoUPC(dto.getPoUPC());
		itDo.setPoVendMat(dto.getPoVendMat());
		itDo.setPrice(dto.getPrice());
		itDo.setPricingUnit(dto.getPricingUnit());
		itDo.setQtyUom(dto.getQtyUom());
		itDo.setPoQty(dto.getPoQty());
		itDo.setPoUom(dto.getPoUom());
		itDo.setRefDocCat(dto.getRefDocCat());
		itDo.setRefDocNum(Long.valueOf(dto.getRefDocNum() == null ? "0" : dto.getRefDocNum()));
		itDo.setRequestId(dto.getRequestId());
		itDo.setShippingAmt(dto.getShippingAmt());
		itDo.setShippingPer(dto.getShippingPer());
		itDo.setUnit(dto.getUnit());
		itDo.setUnitPriceOPU(dto.getUnitPriceOPU());
		itDo.setUnusedField1(dto.getUnusedField1());
		itDo.setUnusedField2(dto.getUnusedField2());
		itDo.setUpcCode(dto.getUpcCode());
		itDo.setUpdatedAt(dto.getUpdatedAt());
		itDo.setUpdatedBy(dto.getUpdatedBy());

		invoiceItemRepository.save(itDo);
		// Account Assignment Start
		if (!ServiceUtil.isEmpty(itDo.getIsAccAssigned())
				&& (itDo.getIsAccAssigned().equalsIgnoreCase("K") || itDo.getIsAccAssigned().equalsIgnoreCase("L"))) {
			ModelMapper m = new ModelMapper();
			int deleteCount = invoiceItemAcctAssignmentRepository.deleteByRequestIdItemId(itDo.getRequestId(),
					itDo.getItemId());
			if (deleteCount >= 0) {

				if (!ServiceUtil.isEmpty(dto.getInvItemAcctDtoList())) {

					for (InvoiceItemAcctAssignmentDto iDto : dto.getInvItemAcctDtoList()) {

						iDto.setInvAccAssId(UUID.randomUUID().toString());
					}
					invoiceItemAcctAssignmentServiceImpl.saveInvoiceItemAcctAssignment(dto.getInvItemAcctDtoList());
				}
			}
		}
		// Account Assignment End

	}

	@Override
	public DashBoardDetailsDto manualMatch(ManualMatchingDto manualMatchingDto) {
		return null;
	}

	@Override
	public DashBoardDetailsDto unMatch(DashBoardDetailsDto dashBoardDetailsDto) {
		return null;
	}

	@Override
	public int updateInvoiceItems(List<InvoiceItemDashBoardDto> itDtoList) {
		// TODO Auto-generated method stub
		int count = 0;
		for (InvoiceItemDashBoardDto dto : itDtoList) {
			InvoiceItemDo itDo = new InvoiceItemDo();
			itDo.setAmountDifference(dto.getAmountDifference());
			itDo.setCreatedAtInDB(dto.getCreatedAtInDB());
			itDo.setCreatedByInDb(dto.getCreatedByInDb());
			itDo.setCurrency(dto.getCurrency());
			itDo.setCustomerItemId(Integer.valueOf(dto.getCustomerItemID() == null ? "0" : dto.getCustomerItemID()));
			itDo.setDisAmt(dto.getDisAmt());
			itDo.setDisPer(dto.getDisPer());
			itDo.setExtItemId(dto.getExtItemId());
			itDo.setId(dto.getId());
			itDo.setInvQty(dto.getInvQty());
			itDo.setIsAccAssigned(dto.getIsAccAssigned());
			itDo.setIsDeleted(dto.getIsDeleted());
			itDo.setIsSelected(dto.getIsSelected());
			itDo.setIsThreewayMatched(dto.getIsThreewayMatched());
			itDo.setIsTwowayMatched(dto.getIsTwowayMatched());
			itDo.setItemCode(dto.getItemCode());
			itDo.setItemComment(dto.getItemComment());
			itDo.setItemId(dto.getItemId());
			itDo.setItemLifeCycleStatus(dto.getItemLifeCycleStatus());
			itDo.setItemText(dto.getItemText());
			itDo.setMatchDocItem(dto.getMatchDocItem());
			itDo.setMatchDocNum(dto.getMatchDocNum());
			itDo.setMatchedBy(dto.getMatchedBy());
			itDo.setMatchParam(dto.getMatchParam());
			itDo.setNetWorth(dto.getNetWorth());
			itDo.setPoAvlQtyOU(dto.getPoAvlQtyOU());
			itDo.setPoMaterialNum(dto.getPoMaterialNum());
			itDo.setPoNetPrice(dto.getPoNetPrice());
			itDo.setPoTaxCode(dto.getPoTaxCode());
			itDo.setPoQty(dto.getPoQty());
			itDo.setPoUom(dto.getPoUom());
			itDo.setPoUPC(dto.getPoUPC());
			itDo.setPoVendMat(dto.getPoVendMat());
			itDo.setPrice(dto.getPrice());
			itDo.setPricingUnit(dto.getPricingUnit());
			itDo.setQtyUom(dto.getQtyUom());
			itDo.setRefDocCat(dto.getRefDocCat());
			itDo.setRefDocNum(Long.valueOf(dto.getRefDocNum() == null ? "0" : dto.getRefDocNum()));
			itDo.setRequestId(dto.getRequestId());
			itDo.setShippingAmt(dto.getShippingAmt());
			itDo.setShippingPer(dto.getShippingPer());
			itDo.setUnit(dto.getUnit());
			itDo.setUnitPriceOPU(dto.getUnitPriceOPU());
			itDo.setUnusedField1(dto.getUnusedField1());
			itDo.setUnusedField2(dto.getUnusedField2());
			itDo.setUpcCode(dto.getUpcCode());
			itDo.setUpdatedAt(dto.getUpdatedAt());
			itDo.setUpdatedBy(dto.getUpdatedBy());

			invoiceItemRepository.save(itDo);
			// Account Assignment Start

			if (!ServiceUtil.isEmpty(itDo.getIsAccAssigned()) && (itDo.getIsAccAssigned().equalsIgnoreCase("K")
					|| itDo.getIsAccAssigned().equalsIgnoreCase("L"))) {
				ModelMapper m = new ModelMapper();
				int deleteCount = invoiceItemAcctAssignmentRepository.deleteByRequestIdItemId(itDo.getRequestId(),
						itDo.getItemId());
				if (deleteCount >= 0) {

					if (!ServiceUtil.isEmpty(dto.getInvItemAcctDtoList())) {

						for (InvoiceItemAcctAssignmentDto iDto : dto.getInvItemAcctDtoList()) {

							iDto.setInvAccAssId(UUID.randomUUID().toString());
						}
						invoiceItemAcctAssignmentServiceImpl.saveInvoiceItemAcctAssignment(dto.getInvItemAcctDtoList());
					}
				}
			}
			// Account Assignment End
			count++;
		}
		return count;
	}

	/*
	 * GRN Calculations Updating lifecycle status at Item level Item level : NO
	 * GRN -if no entry found for document item and document in number in PO
	 * History totals Item level : Partial GRN -if entry found for document item
	 * and document in number in PO History totals and (PoQty-DeliveredQty)>0
	 * Item level : Partial GRN -if entry found for document item and document
	 * in number in PO History totals and (PoQty-DeliveredQty)>0
	 * 
	 * GRN Calculations Updating lifecycle status at Header level GRN
	 * -----Calculations in header level will happen only if header lifecycle
	 * status is "Full Matched"----------------------------
	 * -------------------------------------------------------------------------
	 * 1. Header level :NO GRN - If all the items have No GRN in lifecycle
	 * status 2.Header Level :GR Complete-If all the items have GRN complete in
	 * lifecycle status. 3.Header Level:Partial GRN -Other cases [ex-If there
	 * are records with lifecycle status having both NO GRN or Partial GRN (or)
	 * -If there records with both "No GRN" and "GRN complete" (or) -If all
	 * records with "Partial GRn" (or) if there are records with
	 * "Partial GRn"and "GRN complete" (or) - having all three combinations]
	 */

	/*
	 * @Override public DashBoardDetailsDto grnCalculations(DashBoardDetailsDto
	 * dashBoardDetailsDto) {
	 * 
	 * // TODO Auto-generated method stub List<InvoiceItemDashBoardDto> itemDto
	 * = dashBoardDetailsDto.getInvoiceItems(); List<InvoiceItemDashBoardDto>
	 * grnCompleteList = new ArrayList<InvoiceItemDashBoardDto>(); int
	 * grnCalculationCount = 0; int partialGRcount = 0; int itemCount =
	 * dashBoardDetailsDto.getInvoiceItems().size(); InvoiceHeaderDashBoardDto
	 * invoiceHeaderDto = new InvoiceHeaderDashBoardDto(); String
	 * headerLifeCyclestatus = ""; for (int i = 0; i < itemDto.size(); i++) {
	 * String poNumber = itemDto.get(i).getMatchDocNum().toString();
	 * logger.error("PoNumber " + poNumber); String poItem =
	 * itemDto.get(i).getMatchDocItem(); logger.error("PoItem " + poItem);
	 * String itemLifeCycleStatus = ""; PurchaseDocumentHistoryTotalsDo
	 * poHistoryTotalsDo = purchaseDocumentHistoryTotalsRepository
	 * .getPurchaseDocumentHistoryTotals(poNumber, poItem); if
	 * (ServiceUtil.isEmpty(poHistoryTotalsDo)) { logger.error("Inside No GRN");
	 * itemLifeCycleStatus = ApplicationConstants.NO_GRN; } else {
	 * logger.error("GRN calculations"); Double PoQty = stringToDouble(
	 * poHistoryTotalsDo.getPoPrQnt() == null ? "0.00" :
	 * poHistoryTotalsDo.getPoPrQnt()); Double DeliveredQty = stringToDouble(
	 * poHistoryTotalsDo.getDelivQty() == null ? "0.00" :
	 * poHistoryTotalsDo.getDelivQty()); itemLifeCycleStatus = (PoQty -
	 * DeliveredQty) > 0 ? ApplicationConstants.PARTIAL_GRN :
	 * ApplicationConstants.GRNCOMPLETE; grnCalculationCount =
	 * grnCalculationCount + 1;
	 * 
	 * } // Updating item leveL
	 * itemDto.get(i).setItemLifeCycleStatus(itemLifeCycleStatus);
	 * update(itemDto.get(i)); String itemLifeCycleStatusText =
	 * statusConfigRepository.text(itemDto.get(i).getItemLifeCycleStatus(),
	 * "EN");
	 * itemDto.get(i).setItemLifeCycleStatusText(itemLifeCycleStatusText); if
	 * (itemDto.get(i).getItemLifeCycleStatus().equalsIgnoreCase(
	 * ApplicationConstants.GRNCOMPLETE)) { grnCompleteList.add(itemDto.get(i));
	 * }
	 * 
	 * } dashBoardDetailsDto.setInvoiceItems(grnCompleteList);
	 * 
	 * // GR calculations in header level // happens only if status is
	 * "Full Matched" if
	 * (dashBoardDetailsDto.getInvoiceHeader().getLifecycleStatus()
	 * .equalsIgnoreCase(ApplicationConstants.FULL_2_WAY_MATCHED)) { if
	 * (grnCalculationCount != 0) { if (itemCount - grnCalculationCount == 0) {
	 * headerLifeCyclestatus = ApplicationConstants.GRNCOMPLETE; } else if
	 * (itemCount - grnCalculationCount > 0) { headerLifeCyclestatus =
	 * ApplicationConstants.PARTIAL_GRN; } } else { headerLifeCyclestatus =
	 * ApplicationConstants.NO_GRN; }
	 * 
	 * invoiceHeaderDto.setLifecycleStatus(headerLifeCyclestatus);
	 * invoiceHeaderServiceImpl.updateLifeCycleStatus(headerLifeCyclestatus,
	 * invoiceHeaderDto.getRequestId(),
	 * stringToDouble(invoiceHeaderDto.getGrossAmount()),
	 * stringToDouble(invoiceHeaderDto.getBalance())); }
	 * dashBoardDetailsDto.setInvoiceHeader(invoiceHeaderDto); return
	 * dashBoardDetailsDto; }
	 */
	
	 public  ResponseDto odataPaymentStatus(String sapInvoiceNumber) {
	        ResponseDto response = new ResponseDto();
	        
	        
	        try {

	 

	            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
	            CloseableHttpClient httpClient = clientBuilder.build();
	            HttpResponse httpResponse = null;
	            HttpResponse httpResponse1 = null;
	            URIBuilder builder = new URIBuilder();
	            builder.setScheme("http")
	                .setHost("34.210.142.28:8080//sap/opu/odata/SAP/ZAP_AUTOMATION_SRV/InvoiceStatusReportSet")
	                .setParameter("$filter", "Invoice eq '" + sapInvoiceNumber + "'")
	                .setParameter("$format", "json");
	            HttpGet httpGet = new HttpGet(builder.build());
	            String userpass = "bopf1" + ":" + "Dec@200";
//	            String userpass = ApplicationConstants.ODATA_USERNAME + ":" + ApplicationConstants.ODATA_PASSWORD;
	            httpGet.setHeader(HttpHeaders.AUTHORIZATION,
	                    "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes()));
	            // ServicesUtil.unSetupSOCKS();
	            logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][sapInvoiceNo] = " + sapInvoiceNumber);
	            logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][httpGet] = " + httpGet);
	            httpResponse = httpClient.execute(httpGet); // , httpContext);
	            httpResponse1=httpClient.execute(httpGet);
	            logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][httpresponse] = " + httpResponse);
	            String respBody = "";
	            String result="";
	            
	            if (httpResponse != null) {
	            	
	                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		             result = EntityUtils.toString(httpResponse1.getEntity());
	            	InputStream inputStream = httpResponse.getEntity().getContent();
					byte[] data = new byte[1024];
					int length = 0;
					while ((length = inputStream.read(data,0,data.length))!=-1) {
						bytes.write(data, 0, length);
					}
					respBody = new String(bytes.toByteArray(), "UTF-8");
	                    
	               
	            }
	    
	       
	            logger.error(
	                    "[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][http Status] = " + httpResponse.getStatusLine());
	            logger.error(
	                    "[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][httpResponse] = " + httpResponse.getEntity());
	            logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][respBody]= " + respBody);
	            logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][result]= " + result);

	 

	            JSONObject node = new JSONObject(respBody);
	            JSONArray resultsArray = node.getJSONObject("d").getJSONArray("results");
	            	
	            for(int i=0; i<resultsArray.length(); i++){
	                InvoiceHeaderDo newDo = new InvoiceHeaderDo();
	                String input = resultsArray.getJSONObject(i).getJSONObject("__metadata").getString("id");
	               // String output = getStringBetweenTwoChars(input, "'", "'");
	                //newDo.setSapInvoiceNumber(Long.parseLong(output));
//	                String id = invoiceHeaderRepository.getUuidUsingSAPInvoiceNo(newDo.getSapInvoiceNumber());
//	                newDo.setId(id);
//	                logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][id]= " + id);
//	                newDo = invoiceHeaderRepository.getAllById(id);
	                newDo.setPaymentStatus( resultsArray.getJSONObject(i).getString("Status"));
	                newDo.setExtInvNum(resultsArray.getJSONObject(i).getString("Reference"));
	                String s=null;
	                if(resultsArray.getJSONObject(i).get("ClearingDate")!=null && !resultsArray.getJSONObject(i).get("ClearingDate").toString().equalsIgnoreCase("null")){
	                 String r=resultsArray.getJSONObject(i).get("ClearingDate").toString();	
	                 s=getStringBetweenTwoChars(r, "(", ")");
	                 s=s;
	                }
	            
	             
	                newDo.setClearingDate(s);
	                newDo.setPaymentBlock(resultsArray.getJSONObject(i).getString("PaymentBlock"));
	                newDo.setPaymentBlockDesc( resultsArray.getJSONObject(i).getString("PaymentBlockDesc"));
	                newDo.setClearingAccountingDocument(resultsArray.getJSONObject(i).getString("ClearingAccountingDoc"));
	                newDo.setAccountingDoc(resultsArray.getJSONObject(i).getString("AccountingDoc"));
	                logger.error("[ApAutomation][InvoiceHeaderServiceImpl][oDataPaymentStatus][result]= " + newDo);

	 

//	                invoiceHeaderRepository.save(newDo);
	            }
	            
	            response.setCode(ApplicationConstants.SUCCESS);
	            response.setStatus(ApplicationConstants.SUCCESS);
	            response.setMessage(" Payment Status Update "+ ApplicationConstants.SUCCESS);
	        } catch (Exception e) {
	            response.setCode(ApplicationConstants.CODE_FAILURE);
	            response.setStatus(ApplicationConstants.FAILURE);
	            e.printStackTrace();
	            response.setMessage("UPDATION " +ApplicationConstants.FAILURE + " due to " + e.getMessage());
	            System.out.println(response);
	        }
	        return response;
	    }
	 
	 public  String getStringBetweenTwoChars(String input, String startChar, String endChar) {
		    try {
		    	if(!ServiceUtil.isEmpty(input)){
		        int start = input.indexOf(startChar);
		        if (start != -1) {
		            int end = input.indexOf(endChar, start + startChar.length());
		            if (end != -1) {
		                return input.substring(start + startChar.length(), end);
		            }
		        }
		    	}
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return input; 
		}

	    
//	    public static void main(String[] args) {
//	    	InvoiceItemServiceImpl is =new InvoiceItemServiceImpl();
//	     is.   odataPaymentStatus("5105601792");
//	    }
}
