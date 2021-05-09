package com.ap.menabev.serviceimpl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDashBoardDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.MessageDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.entity.AttachmentDo;
import com.ap.menabev.entity.CommentDo;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.entity.StatusConfigDo;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.ReasonForRejectionRepository;
import com.ap.menabev.invoice.StatusConfigRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ServiceUtil;

@Service
public class InvoiceHeaderServiceImpl implements InvoiceHeaderService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceHeaderServiceImpl.class);
	@PersistenceContext
	private EntityManager entityManager;

	// @Autowired
	// EmailRepository emailRepository;

	
	@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;
	@Autowired
	InvoiceItemRepository invoiceItemRepository;

	@Autowired
	EmailServiceImpl emailServiceImpl;

	@Autowired
	InvoiceItemService itemService;


	@Autowired
	ReasonForRejectionRepository reasonForRejectionRepository;
	

	@Autowired
	AttachmentRepository attachmentRepository;

	@Autowired
	AttachmentServiceImpl attachmentServiceImpl;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommentServiceImpl commentServiceImpl;

	@Autowired
	StatusConfigRepository statusConfigRepository;


	@Autowired
	InvoiceItemService invoiceItemService;


	@Autowired
	ReasonForRejectionRepository rejectionRepository;

	@Autowired
	InvoiceItemAcctAssignmentRepository invoiceItemAcctAssignmentRepository;
	@Autowired
	InvoiceItemAcctAssignmentServiceImpl invoiceItemAcctAssignmentServiceImpl;


	public InvoiceHeaderDo save(InvoiceHeaderDto dto) {
		InvoiceHeaderDo invoiceHeader = new InvoiceHeaderDo();
		try {
			ModelMapper mapper = new ModelMapper();
			if (ServiceUtil.isEmpty(dto.getRequestId())) {
				dto.setRequestId(getRequestId());
			}
			logger.error("dto" + dto);

			if (ServiceUtil.isEmpty(dto.getSubTotal())) {
				dto.setSubTotal("0.000");
			}
			if( stringToDouble(dto.getBalance())!=0.0){
				dto.setBalanceCheck(Boolean.FALSE);
			}
			else{
				dto.setBalanceCheck(Boolean.TRUE);	
			}
			invoiceHeader = invoiceHeaderRepository.save(mapper.map(dto, InvoiceHeaderDo.class));
			if (!ServiceUtil.isEmpty(dto.getInvoiceItems())) {
				for (InvoiceItemDto item : dto.getInvoiceItems()) {
					item.setRequestId(invoiceHeader.getRequestId());
					itemService.save(item);
				}
			}
			return invoiceHeader;
		} catch (Exception e) {
			e.printStackTrace();
			return invoiceHeader;
		}
	}

	@Override
	public List<InvoiceHeaderDto> getAll() {
		List<InvoiceHeaderDto> list = new ArrayList<>();
		String rejectionText = "";
		try {
			ModelMapper mapper = new ModelMapper();
			// Sort sort = new Sort(Sort.Direction.DESC, "requestId");
			// List<InvoiceHeaderDo> entityList =
			// invoiceHeaderRepository.findAll(sort);// .findAll();
			List<InvoiceHeaderDo> entityList = invoiceHeaderRepository.getAllInvoiceHeader("09");
			for (InvoiceHeaderDo invoiceHeaderDo : entityList) {
				if (invoiceHeaderDo.getLifecycleStatus().equalsIgnoreCase(ApplicationConstants.REJECTED)) {
					rejectionText = reasonForRejectionRepository
							.getRejectionTextbyRejectionId(invoiceHeaderDo.getReasonForRejection(), "EN");
				}
				String lifecycleStatusText = statusConfigRepository.text(invoiceHeaderDo.getLifecycleStatus(), "EN");
				logger.error("rejectionText : " + rejectionText);
				logger.error("lifecycleStatusText : " + lifecycleStatusText);
				InvoiceHeaderDto dto = (mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class));
				dto.setRejectionText(rejectionText);
				dto.setLifecycleStatusText(lifecycleStatusText);
				list.add(dto);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}

	@Override
	public ResponseDto delete(Integer id) {
		ResponseDto response = new ResponseDto();
		try {
			invoiceHeaderRepository.deleteById(id);
			response.setCode("200");
			response.setStatus("Success");
			response.setMessage("Deleted");
			return response;
		} catch (Exception e) {
			response.setCode("500");
			response.setStatus("Failed");
			response.setMessage("Error");
			return response;
		}
	}

	@Autowired
	CostAllocationRepository costAllocationRepository;

	@Override
	public InboxDto findPaginated(int pageNo, int limit) {
		InboxDto dto = new InboxDto();
		List<InvoiceHeaderDto> dtoList = new ArrayList<>();

		ModelMapper mapper = new ModelMapper();
		try {
			Long count = invoiceHeaderRepository.count();
			dto.setCount(count);
			int offset = 0;
			if (pageNo > 0) {
				offset = (limit * pageNo);
			}
			List<StatusConfigDo> statusList = statusConfigRepository.findAll();
			Map<String, String> statusMap = new HashMap<String, String>();
			for (StatusConfigDo statusConfigDo : statusList) {
				statusMap.put(statusConfigDo.getLifeCycleStatus(), statusConfigDo.getText());
			}
			List<InvoiceHeaderDo> doEntity = invoiceHeaderRepository.getAllByLimit(limit, offset);
			for (InvoiceHeaderDo invoiceHeaderDo : doEntity) {
				InvoiceHeaderDto headerDto = mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
				headerDto.setLifecycleStatusText(statusMap.get(invoiceHeaderDo.getLifecycleStatus()));
				dtoList.add(headerDto);
			}
			dto.setHeaderList(dtoList);
		} catch (Exception e) {
			dto.setHeaderList(dtoList);
			dto.setCount(0L);

		}
		return dto;

	}

	@Override
	public InvoiceHeaderDetailsDto getAllInvoiceDetailsOnRequestId(String requestId) {
		ModelMapper mapper = new ModelMapper();
		List<Object[]> objList = invoiceHeaderRepository.getAllInvoiceDetailsOnRequestId(requestId);
		InvoiceHeaderDetailsDto dto = new InvoiceHeaderDetailsDto();
		if (!ServiceUtil.isEmpty(objList)) {
			List<InvoiceItemDto> invoiceItemList = new ArrayList<>();
			InvoiceHeaderDo invoiceHeaderDo = null;
			InvoiceHeaderDto headerDto = null;
			try {
				for (int i = 0; i < objList.size(); i++) {
					Object[] objArr = objList.get(i);
					invoiceHeaderDo = (InvoiceHeaderDo) objArr[0];
					if (i <= 0)
						headerDto = mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class);
					InvoiceItemDo invoiceItemDo = (InvoiceItemDo) objArr[1];
					InvoiceItemDto itemDto = mapper.map(invoiceItemDo, InvoiceItemDto.class);
					invoiceItemList.add(itemDto);
				}
				if (!ServiceUtil.isEmpty(headerDto)) {
					dto.setChannelType(headerDto.getChannelType());
					dto.setClerkEmail(headerDto.getClerkEmail());
					dto.setClerkId(headerDto.getClerkId());
					dto.setCompCode(headerDto.getCompCode());
					dto.setCreatedAt(headerDto.getCreatedAt());
					dto.setCurrency(headerDto.getCurrency());
					dto.setEmailFrom(headerDto.getEmailFrom());
					dto.setExtInvNum(headerDto.getExtInvNum());
					dto.setFiscalYear(headerDto.getFiscalYear());
					dto.setInvoiceTotal(headerDto.getInvoiceTotal());
					dto.setInvoiceType(headerDto.getInvoiceType());
					dto.setLifecycleStatus(headerDto.getLifecycleStatus());
					dto.setPaymentTerms(headerDto.getPaymentTerms());
					dto.setRefDocCat(headerDto.getRefDocCat());
					dto.setRefDocNum(headerDto.getRefDocNum());
					dto.setRequestId(headerDto.getRequestId());
					dto.setId(headerDto.getId());
					dto.setSapInvoiceNumber(headerDto.getSapInvoiceNumber());
					dto.setShippingCost(headerDto.getShippingCost());
					dto.setTaskStatus(headerDto.getTaskStatus());
					dto.setTaxAmount(headerDto.getTaxAmount());
					dto.setVendorId(headerDto.getVendorId());
					dto.setVersion(headerDto.getVersion());
					dto.setBalance(headerDto.getBalance());
					dto.setGrossAmount(headerDto.getGrossAmount());
				}
				dto.setInvoiceItems(invoiceItemList);

			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return dto;

	}

	@Override
	public List<InvoiceHeaderDto> filterByKeys(InvoiceHeaderDto dto) {
		ModelMapper mapper = new ModelMapper();
		List<InvoiceHeaderDto> dtoList = new ArrayList<>();
		try {
			Example<InvoiceHeaderDo> example = Example.of(importDto(dto));
			List<InvoiceHeaderDo> entityList = invoiceHeaderRepository.findAll(example);
			for (InvoiceHeaderDo invoiceHeaderDo : entityList) {
				dtoList.add(mapper.map(invoiceHeaderDo, InvoiceHeaderDto.class));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dtoList;
	}

	private InvoiceHeaderDo importDto(InvoiceHeaderDto dto) {
		InvoiceHeaderDo entity = new InvoiceHeaderDo();
		if (!ServiceUtil.isEmpty(dto.getChannelType()))
			entity.setChannelType(dto.getChannelType());
		if (!ServiceUtil.isEmpty(dto.getClerkEmail()))
			entity.setClerkEmail(dto.getClerkEmail());
		if (!ServiceUtil.isEmpty(dto.getCurrency()))
			entity.setCurrency(dto.getCurrency());
		if (!ServiceUtil.isEmpty(dto.getEmailFrom()))
			entity.setEmailFrom(dto.getEmailFrom());
		if (!ServiceUtil.isEmpty(dto.getExtInvNum()))
			entity.setExtInvNum(dto.getExtInvNum());
		if (!ServiceUtil.isEmpty(dto.getFiscalYear()))
			entity.setFiscalYear(dto.getFiscalYear());
		if (!ServiceUtil.isEmpty(dto.getInvoiceTotal()))
			entity.setInvoiceTotal(dto.getInvoiceTotal());
		if (!ServiceUtil.isEmpty(dto.getInvoiceType()))
			entity.setInvoiceType(dto.getInvoiceType());
		if (!ServiceUtil.isEmpty(dto.getLifecycleStatus()))
			entity.setLifecycleStatus(dto.getLifecycleStatus());
		if (!ServiceUtil.isEmpty(dto.getPaymentTerms()))
			entity.setPaymentTerms(dto.getPaymentTerms());
		if (!ServiceUtil.isEmpty(dto.getRefDocCat()))
			entity.setRefDocCat(dto.getRefDocCat());
		if (!ServiceUtil.isEmpty(dto.getRequestId()))
			entity.setRequestId(dto.getRequestId());
		if (!ServiceUtil.isEmpty(dto.getTaskStatus()))
			entity.setTaskStatus(dto.getTaskStatus());
		if (!ServiceUtil.isEmpty(dto.getClerkId()))
			entity.setClerkId(dto.getClerkId());
		if (!ServiceUtil.isEmpty(dto.getCompCode()))
			entity.setCompCode(dto.getCompCode());
		if (!ServiceUtil.isEmpty(dto.getCreatedAt()))
			entity.setCreatedAt(dto.getCreatedAt());
		if (!ServiceUtil.isEmpty(dto.getRefDocNum()))
			entity.setRefDocNum(dto.getRefDocNum());
		if (!ServiceUtil.isEmpty(dto.getSapInvoiceNumber()))
			entity.setSapInvoiceNumber(dto.getSapInvoiceNumber());
		if (!ServiceUtil.isEmpty(dto.getShippingCost()))
			entity.setShippingCost(dto.getShippingCost());
		if (!ServiceUtil.isEmpty(dto.getTaxAmount()))
			entity.setTaxAmount(dto.getTaxAmount());
		if (!ServiceUtil.isEmpty(dto.getVendorId()))
			entity.setVendorId(dto.getTaxAmount());
		if (!ServiceUtil.isEmpty(dto.getVersion()))
			entity.setVersion(dto.getVersion());
		return entity;
	}

	private String getSubStringForQuery(InvoiceHeaderDto dto) {

		String subString = "";
		String str = "";

		if (!ServiceUtil.isEmpty(dto.getRequestId()))
			subString += "REQUEST_ID = '" + dto.getRequestId() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getExtInvNum()))
			subString += "EXT_INV_NUM = '" + dto.getExtInvNum() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getClerkEmail()))
			subString += "CLERK_EMAIL = '" + dto.getClerkEmail() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getVendorId()))
			subString += "VENDOR_ID LIKE '%" + dto.getVendorId() + "%' and ";

		if (!ServiceUtil.isEmpty(dto.getVendorName()))
			subString += "VENDOR_NAME = '" + dto.getVendorName() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getEmailFrom()))
			subString += "EMAIL_FROM = '" + dto.getEmailFrom() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getTaskStatus()))
			subString += "TASK_STATUS = '" + dto.getTaskStatus() + "' and "; //

		if (!ServiceUtil.isEmpty(dto.getInvoiceType()))
			subString += "INVOICE_TYPE = '" + dto.getInvoiceType() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getLifecycleStatus()))
			subString += "LIFECYCLE_STATUS = '" + dto.getLifecycleStatus() + "' and ";

		if ((!ServiceUtil.isEmpty(dto.getInvoiceTotalFrom())) && (!ServiceUtil.isEmpty(dto.getInvoiceTotalTo())))
			subString += "INVOICE_TOTAL BETWEEN '" + dto.getInvoiceTotalFrom() + "' and '" + dto.getInvoiceTotalTo()
					+ "' and ";

		if ((!ServiceUtil.isEmpty(dto.getCreatedAtFrom())) && (!ServiceUtil.isEmpty(dto.getCreatedAtTo()))) {
			subString += "CREATED_AT BETWEEN '" + dto.getCreatedAtFrom() + "' and '" + dto.getCreatedAtTo() + "' and ";
		}

		if ((!ServiceUtil.isEmpty(dto.getInvoiceDateFrom())) && (!ServiceUtil.isEmpty(dto.getInvoiceDateTo()))) {
			subString += "INVOICE_DATE BETWEEN '" + dto.getInvoiceDateFrom() + "' and '" + dto.getInvoiceDateTo()
					+ "' and ";
		}
		if ((!ServiceUtil.isEmpty(dto.getPostingDateFrom())) && (!ServiceUtil.isEmpty(dto.getPostingDateTo()))) {
			subString += "POSTING_DATE BETWEEN '" + dto.getPostingDateFrom() + "' and '" + dto.getPostingDateTo()
					+ "' and ";
		}

		if ((!ServiceUtil.isEmpty(dto.getDueDateFrom())) && (!ServiceUtil.isEmpty(dto.getDueDateTo()))) {
			subString += "DUE_DATE BETWEEN '" + dto.getDueDateFrom() + "' and '" + dto.getDueDateTo() + "' and ";
		}
		if (!ServiceUtil.isEmpty(dto.getAssignedTo()))
			subString += "UPPER(ASSIGNED_TO) = '" + dto.getAssignedTo().toUpperCase() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCompCode())) {
			subString += "COMP_CODE LIKE '%" + dto.getCompCode() + "' and ";
		}

		if (!ServiceUtil.isEmpty(dto.getClerkId()))
			subString += "CLERK_ID = '" + dto.getClerkId() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getChannelType()))
			subString += "CHANNEL_TYPE = '" + dto.getChannelType() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getRefDocCat()))
			subString += "REF_DOC_CAT = '" + dto.getRefDocCat() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getRefDocNum()))
			subString += "REF_DOC_NUM = '" + dto.getRefDocNum() + "' and ";


		if (!ServiceUtil.isEmpty(dto.getSubTotal()))
			subString += "SUB_TOTAL = '" + dto.getSubTotal() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getSapInvoiceNumber()))
			subString += "SAP_INVOICE_NUMBER = '" + dto.getSapInvoiceNumber() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getFiscalYear()))
			subString += "FISCAL_YEAR = '" + dto.getFiscalYear() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCurrency()))
			subString += "CURRENCY = '" + dto.getCurrency() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPaymentTerms()))
			subString += "PAYMENT_TERMS = '" + dto.getPaymentTerms() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getTaxAmount()))
			subString += "TAX_AMOUNT = '" + dto.getTaxAmount() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getShippingCost()))
			subString += "SHIPPING_COST = '" + dto.getShippingCost() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getVersion()))
			subString += "VERSION = '" + dto.getVersion() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getGrossAmount()))
			subString += "GROSS_AMOUNT = '" + dto.getGrossAmount() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getBalance()))
			subString += "BALANCE = '" + dto.getBalance() + "' and ";

		if (!ServiceUtil.isEmpty(dto.getReasonForRejection()))
			subString += "REASON_FOR_REJECTION = '" + dto.getReasonForRejection() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getDisAmt()))
			subString += "DIS_AMT = '" + dto.getDisAmt() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getDeposit()))
			subString += "DEPOSIT = '" + dto.getDeposit() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCreatedByInDb()))
			subString += "CREATED_BY_IN_DB = '" + dto.getCreatedByInDb() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getCreatedAtInDB()))
			subString += "CREATED_AT_IN_DB = '" + dto.getCreatedAtInDB() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getAccountNumber()))
			subString += "ACCOUNT_NUMBER = '" + dto.getAccountNumber() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getAccountingDoc()))
			subString += "ACCOUNTING_DOC = '" + dto.getAccountingDoc() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPostingDate()))
			subString += "POSTING_DATE = '" + dto.getPostingDate() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getClearingAccountingDocument()))
			subString += "CLEARING_ACCOUNTING_DOCUMENT = '" + dto.getClearingAccountingDocument() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getClearingDate()))
			subString += "CLEARING_DATE = '" + dto.getClearingDate() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPaymentStatus())){
			logger.error("Payment Status "+ dto.getPaymentStatus());
			subString += "PAYMENT_STATUS = '" + dto.getPaymentStatus() + "' and ";
			logger.error("substring after Payment Status "+ subString);

			}
		if (!ServiceUtil.isEmpty(dto.getPaymentBlock()))
			subString += "PAYMENT_BLOCK = '" + dto.getPaymentBlock() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getPaymentBlockDesc()))
			subString += "PAYMENT_BLOCK_DESC = '" + dto.getPaymentBlockDesc() + "' and ";
		if (!ServiceUtil.isEmpty(dto.getFilterFor())&& dto.getFilterFor().equalsIgnoreCase(ApplicationConstants.FILTER_FOR_ISR)){
		
			subString += "REASON_FOR_REJECTION IS NULL AND LIFECYCLE_STATUS != '00' and ";
		}

        
		if (subString.length() > 0)
			str = subString.substring(0, subString.length() - 4);
        logger.error("Final query "+ str);
		return str;
	}
	
	public String getRequestId() {
		try {
			String requestId = invoiceHeaderRepository.getRequestId();
			return "APA-" + String.format("%06d", Integer.parseInt(requestId));
		} catch (Exception e) {
			logger.error(
					"[ApAutomation][InvoiceHeaderServiceImpl][getDetailsForFilter][Exception] = " + e.getMessage());
		}
		return null;
	}

	public String getDraftRequestId() {
		try {
			String requestId = invoiceHeaderRepository.getDraftRequestId().toString();
			return "DRFT-" + String.format("%06d", Integer.parseInt(requestId));

		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public String getVendorId(String requestId) {
		return null;
		// return invoiceHeaderRepository.getVendorId(requestId);
	}

	@Override
	public ResponseDto reasonForRejection(InvoiceHeaderDto dto) {
		// TODO Auto-generated method stub
		ResponseDto response = new ResponseDto();

		InvoiceHeaderDo invoiceHeader = invoiceHeaderRepository.fetchInvoiceHeader(dto.getRequestId());
		// invoiceHeader.setRequestId(requestId);
		invoiceHeader.setLifecycleStatus(ApplicationConstants.REJECTED);
		invoiceHeader.setReasonForRejection(dto.getReasonForRejection());

		// if(ServiceUtil.isEmpty(reasonForRejection)){
		// response.setCode(ApplicationConstants.CODE_FAILURE);
		// response.setStatus(ApplicationConstants.FAILURE);
		// response.setMessage("Reason specified is not stored in DB");
		// return response;
		// }
		// invoiceHeader.setReasonForRejection(reasonForRejection);
		try {

			invoiceHeaderRepository.save(invoiceHeader);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.REJECT_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.REJECT_FAILURE);
			return response;
		}

	}

	@Override
	public InvoiceHeaderDashBoardDto headerCheck(HeaderCheckDto headerCheckDto) {
		return null;
	}

	@Override
	public ResponseDto saveOrUpdate(InvoiceHeaderDto dto) {
		return null;
	}

	private InvoiceItemDo getItemDoForUpdate(InvoiceItemDo invoiceItem, InvoiceItemDto item) {

		invoiceItem.setItemText(item.getItemText());

		invoiceItem.setIsDeleted(item.getIsDeleted());

		invoiceItem.setCurrency(item.getCurrency());

		invoiceItem.setDisAmt(item.getDisAmt());

		invoiceItem.setDisPer(item.getDisPer());

		invoiceItem.setDeposit(item.getDeposit());

		invoiceItem.setExtItemId(item.getExtItemId());

		invoiceItem.setInvQty(item.getInvQty());

		invoiceItem.setItemComment(item.getItemComment());

		invoiceItem.setItemId(item.getItemId());

		invoiceItem.setMatchDocItem(item.getMatchDocItem());

		invoiceItem.setMatchParam(item.getMatchParam());
		invoiceItem.setPoAvlQtyOU(item.getPoAvlQtyOU());
		invoiceItem.setPoMaterialNum(item.getPoMaterialNum());
		invoiceItem.setPoNetPrice(item.getPoNetPrice());
		invoiceItem.setPoTaxCode(item.getPoTaxCode());
		invoiceItem.setPoUPC(item.getPoUPC());
		invoiceItem.setPoVendMat(item.getPoVendMat());
		invoiceItem.setPoQty(item.getPoQty());
		invoiceItem.setPoUom(item.getPoUom());

		invoiceItem.setPrice(item.getPrice());

		invoiceItem.setQtyUom(item.getQtyUom());

		invoiceItem.setRefDocCat(item.getRefDocCat());

		invoiceItem.setRequestId(item.getRequestId());

		invoiceItem.setShippingPer(item.getShippingPer());

		invoiceItem.setUnit(item.getUnit());

		invoiceItem.setUpcCode(item.getUpcCode());

		invoiceItem.setUpdatedBy(item.getUpdatedBy());

		invoiceItem.setUpdatedAt(item.getUpdatedAt());

		invoiceItem.setCustomerItemId(item.getCustomerItemId());

		invoiceItem.setIsSelected(item.getIsSelected());

		invoiceItem.setItemCode(item.getItemCode());

		invoiceItem.setMatchDocNum(item.getMatchDocNum());

		invoiceItem.setNetWorth(item.getNetWorth());

		invoiceItem.setPricingUnit(item.getPricingUnit());

		invoiceItem.setRefDocNum(item.getRefDocNum());

		invoiceItem.setShippingAmt(item.getShippingAmt());

		invoiceItem.setTaxAmt(item.getTaxAmt());

		invoiceItem.setTaxPer(item.getTaxPer());

		invoiceItem.setAmountDifference(item.getAmountDifference());

		return invoiceItem;
	}

	private InvoiceHeaderDo getHeaderDoForUpdate(InvoiceHeaderDo invoiceHeader, InvoiceHeaderDto dto) {

		invoiceHeader.setCreatedAt(dto.getCreatedAt());
		
		invoiceHeader.setDocStatus(dto.getDocStatus());
		
		invoiceHeader.setInvoiceDate(dto.getInvoiceDate());

		invoiceHeader.setDueDate(dto.getDueDate());

		invoiceHeader.setDisAmt(dto.getDisAmt());

		invoiceHeader.setDeposit(dto.getDeposit());

		invoiceHeader.setBalance(dto.getBalance());

		invoiceHeader.setPostingDate(dto.getPostingDate());

		invoiceHeader.setChannelType(dto.getChannelType());

		invoiceHeader.setClerkEmail(dto.getClerkEmail());

		invoiceHeader.setCompCode(dto.getCompCode());

		invoiceHeader.setCurrency(dto.getCurrency());

		invoiceHeader.setExtInvNum(dto.getExtInvNum());

		invoiceHeader.setGrossAmount(dto.getGrossAmount());
		
		invoiceHeader.setManualpaymentBlock(dto.getManualpaymentBlock());

		invoiceHeader.setInvoiceTotal(dto.getInvoiceTotal());

		invoiceHeader.setInvoiceType(dto.getInvoiceType());

		invoiceHeader.setLifecycleStatus(dto.getLifecycleStatus());

		invoiceHeader.setPaymentTerms(dto.getPaymentTerms());

		invoiceHeader.setReasonForRejection(dto.getReasonForRejection());

		invoiceHeader.setRefDocCat(dto.getRefDocCat());

		invoiceHeader.setRequestId(dto.getRequestId());

		invoiceHeader.setTaskStatus(dto.getTaskStatus());

		invoiceHeader.setTaxAmount(dto.getTaxAmount());

		invoiceHeader.setUpdatedBy(dto.getUpdatedBy());

		invoiceHeader.setUpdatedAt(dto.getUpdatedAt());

		invoiceHeader.setVendorName(dto.getVendorName());

		invoiceHeader.setVendorId(dto.getVendorId());

		invoiceHeader.setRefDocNum(dto.getRefDocNum());

		invoiceHeader.setShippingCost(dto.getShippingCost());

		invoiceHeader.setSubTotal(dto.getSubTotal());

		invoiceHeader.setVersion(dto.getVersion());

		invoiceHeader.setSubTotal(dto.getSubTotal());
		invoiceHeader.setClearingAccountingDocument(dto.getClearingAccountingDocument());
		invoiceHeader.setClearingDate(dto.getClearingDate());
		invoiceHeader.setPaymentBlock(dto.getPaymentBlock());
		invoiceHeader.setPaymentBlockDesc(dto.getPaymentBlockDesc());
		invoiceHeader.setPaymentStatus(dto.getPaymentStatus());
		invoiceHeader.setAccountingDoc(dto.getAccountingDoc());
		if( stringToDouble(dto.getBalance())!=0.0){
			invoiceHeader.setBalanceCheck(Boolean.FALSE);
		}
		else{
			invoiceHeader.setBalanceCheck(Boolean.TRUE);	
		}
		

		return invoiceHeader;
	}

	

	

	@Override
	public ResponseDto updateHeader(InvoiceHeaderDto dto) {
		// TODO Auto-generated method stub
		InvoiceHeaderDo invoiceHeader = invoiceHeaderRepository.fetchInvoiceHeader(dto.getRequestId());
		invoiceHeader.setVendorId(dto.getVendorId());
		invoiceHeader.setVendorName(dto.getVendorName());
		invoiceHeader.setExtInvNum(dto.getExtInvNum());
		invoiceHeader.setCreatedAt(dto.getCreatedAt());
		invoiceHeader.setOcrBatchId(dto.getOcrBatchId());
		invoiceHeader.setCurrency(dto.getCurrency());
		invoiceHeader.setHeaderText(dto.getHeaderText());
		invoiceHeader.setGrossAmount(dto.getGrossAmount());
		invoiceHeader.setBalance(dto.getBalance());
		invoiceHeader.setPostingDate(dto.getPostingDate());
		invoiceHeader.setInvoiceDate(dto.getInvoiceDate());
		
		if( stringToDouble(invoiceHeader.getBalance())!=0.0){
			invoiceHeader.setBalanceCheck(Boolean.FALSE);
		}
		else{
			invoiceHeader.setBalanceCheck(Boolean.TRUE);	
		}

		invoiceHeaderRepository.save(invoiceHeader);
		return null;
	}

	

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
	public MasterResponseDto getDetailsForFilter(InvoiceHeaderDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DashBoardDetailsDto getDashBoardDetails(String requestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDto validateInvoiceDocument(String requestId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InvoiceHeaderDo updateLifeCycleStatus(String lifeCycleStatus, String requestId, Double balance,
			Double grossAmount, String docStatus, String manualpaymentBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatusCountDto> getStatusCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateInvoiceHeader(InvoiceHeaderDashBoardDto dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean checkDuplicateInvoice(InvoiceHeaderDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InboxDto findDraftPaginated(int pageNo, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InboxDto findEinvoicePaginated(int pageNo, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDto odataPaymentStatus(String sapInvoiceNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InboxDto findNonDraftPaginated(int pageNo, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	

//	 public static void main(String[] args) {
//	 InvoiceHeaderDto obj = new InvoiceHeaderDto();
//	 obj.setSapInvoiceNumber(null);
//	 EInvoiceHeaderDto obj2= new EInvoiceHeaderDto();
//	 ModelMapper m = new ModelMapper();
//	 obj2=m.map(obj, EInvoiceHeaderDto.class);
//	 System.out.println(obj2.getSapInvoiceNumber());
//	 }
}
