package com.ap.menabev.serviceimpl;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;



@Service
public class InvoiceItemServiceImpl implements InvoiceItemService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceItemServiceImpl.class);

	@Autowired
	InvoiceItemRepository invoiceItemRepository;

//	@Autowired(required=true)
//	PurchaseDocumentItemService purchaseDocumentItemService;

	/*@Autowired
	InvoiceHeaderServiceImpl invoiceHeaderServiceImpl;*/

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	/*@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;*/

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
		//	invoiceItemRepository.deleteById(id);
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
		/*//	List<InvoiceItemDo> entityList = invoiceItemRepository.findAllByLimit(limit, offset);
			ModelMapper mapper = new ModelMapper();
			for (InvoiceItemDo invoiceItemDo : entityList) {
				dtoList.add(mapper.map(invoiceItemDo, InvoiceItemDto.class));
			}*/
			return dtoList;
		} else {
			return getAll();
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String getItemId(String requestId) {
		try {
			String itemId = invoiceItemRepository.getItemId(requestId);
			if (ServiceUtil.isEmpty(itemId)){
				return "0001";
			}
			return String.format("%04d", Integer.parseInt(itemId) + 1);
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
