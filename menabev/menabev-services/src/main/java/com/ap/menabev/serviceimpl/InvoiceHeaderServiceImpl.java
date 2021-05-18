  package com.ap.menabev.serviceimpl;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.AcountOrProcessLeadDetermination;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.RuleInputDto;
import com.ap.menabev.dto.RuleInputProcessLeadDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.dto.TriggerWorkflowContext;
import com.ap.menabev.dto.WorkflowContextDto;
import com.ap.menabev.entity.CostAllocationDo;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;
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
import com.ap.menabev.service.RuleConstants;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.DestinationReaderUtil;
import com.ap.menabev.util.HelperClass;
import com.ap.menabev.util.MenabevApplicationConstant;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.ServiceUtil;
import com.ap.menabev.util.ResponseStatus;

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
	CostAllocationRepository costAllocationRepository;

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

	
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String AUTHORIZATION = "Authorization";

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

	
	
	// create invoice for PO/NON po 
	@Override
	public ResponseEntity<?> save(CreateInvoiceHeaderDto invoiceDto){
		try{
		// save invoice Header 
	    InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),InvoiceHeaderDo.class);
	   // non po than false
	    InvoiceHeaderDo invoiceSavedDo  = invoiceHeaderRepository.save(invoiceHeaderDo);
		// save invoice item 
		List<InvoiceItemDo> itemlists =  ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemDto(), InvoiceItemDo.class);
		itemlists.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		 invoiceItemRepository.saveAll(itemlists);
		// save invoice Account Assingment 
		List<InvoiceItemAcctAssignmentDo>  listAccountAssignement = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemAcctAssignmentDto(), InvoiceItemAcctAssignmentDo.class);
		listAccountAssignement.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		invoiceItemAcctAssignmentRepository.saveAll(listAccountAssignement);
		// save cost allocation
		List<CostAllocationDo>  listCostAllocation = ObjectMapperUtils.mapAll(invoiceDto.getCostAllocationDto(), CostAllocationDo.class);
		listCostAllocation.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		costAllocationRepository.saveAll(listCostAllocation);
		return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto,HttpStatus.OK);
		}catch (Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//Submit invoice 
	@Override
	public ResponseEntity<?> submitForNonPo(CreateInvoiceHeaderDto invoiceDto){
		try{
			// Flow is for Non PO Accountant created invoice 
			// Determine RUle for Process lead 
			AcountOrProcessLeadDetermination determination = new AcountOrProcessLeadDetermination();
			determination.setCompCode(invoiceDto.getInvoiceHeaderDto().getCompCode());
			determination.setProcessLeadCheck("Process Lead");
			determination.setVednorId(invoiceDto.getInvoiceHeaderDto().getVendorId());
			//triggerRuleService(determination);
			// save invoice header
			
    			// Trigger worklfow , which will direct to process lead 
			TriggerWorkflowContext context = new TriggerWorkflowContext();
			context.setRequestId("APA0001");
			context.setInvoiceReferenceNumber("5420001");
			context.setNonPo(true);
			context.setManualNonPo(true);
			context.setAccountantGroup("APADev@incture.com");
			context.setAccountantUser("arun.gauda@incture.com");
			context.setProcessLead("arun.gauda@incture.com");
		ResponseEntity<?> response  = triggerWorkflow((WorkflowContextDto)context,"triggerresolutionworkflow.triggerresolutionworkflow");
		System.err.println("response of workflow Trigger "+response);
		
		 InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),InvoiceHeaderDo.class);
		    InvoiceHeaderDo invoiceSavedDo  = invoiceHeaderRepository.save(invoiceHeaderDo);
		// save invoice item 
		List<InvoiceItemDo> itemlists =  ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemDto(), InvoiceItemDo.class);
		itemlists.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		 invoiceItemRepository.saveAll(itemlists);
		// save invoice Account Assingment 
		List<InvoiceItemAcctAssignmentDo>  listAccountAssignement = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemAcctAssignmentDto(), InvoiceItemAcctAssignmentDo.class);
		listAccountAssignement.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		invoiceItemAcctAssignmentRepository.saveAll(listAccountAssignement);
		// save cost allocation
		List<CostAllocationDo>  listCostAllocation = ObjectMapperUtils.mapAll(invoiceDto.getCostAllocationDto(), CostAllocationDo.class);
		listCostAllocation.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		costAllocationRepository.saveAll(listCostAllocation);
		// Update the Activity Log table 
		return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto,HttpStatus.OK);
		}catch (Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// getInvoiceDetails based on requestId
	public ResponseEntity<?> getInvoiceDetails(String requestId){
		try {
			CreateInvoiceHeaderDto invoiceHeadDto =new  CreateInvoiceHeaderDto();
		   // get InvoiceHeader
		  InvoiceHeaderDo invoiceHeaderDo = invoiceHeaderRepository.fetchInvoiceHeader(requestId);
		  InvoiceHeaderDto invoiceHeaderDto  =  ObjectMapperUtils.map(invoiceHeaderDo,InvoiceHeaderDto.class);   
		  // get InvoiceItem
		   List<InvoiceItemDo> invoiceItemDo = invoiceItemRepository.getInvoiceItemDos(requestId);
		   List<InvoiceItemDto> invoiceItemDtoList = ObjectMapperUtils.mapAll(invoiceItemDo, InvoiceItemDto.class);
	      // get AccountAssignment
		   List<InvoiceItemAcctAssignmentDo>  invoiceItemAcctAssignmentdoList = invoiceItemAcctAssignmentRepository.getByRequestId(requestId); 
		     List<InvoiceItemAcctAssignmentDto>    invoiceItemAcctAssignmentdtoList = ObjectMapperUtils.mapAll(invoiceItemAcctAssignmentdoList, InvoiceItemAcctAssignmentDto.class);
		   // get CostAllocation
		   List<CostAllocationDo> costAllocationDo = costAllocationRepository.getAllOnRequestId(requestId);
		        List<CostAllocationDto>  costAllocationDto = ObjectMapperUtils.mapAll(costAllocationDo, CostAllocationDto.class);                            
		  
		        invoiceHeadDto.setInvoiceHeaderDto(invoiceHeaderDto);
		        invoiceHeadDto.setInvoiceItemDto(invoiceItemDtoList);
		        invoiceHeadDto.setInvoiceItemAcctAssignmentDto(invoiceItemAcctAssignmentdtoList);
		        invoiceHeadDto.setCostAllocationDto(costAllocationDto);
		    return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceHeadDto,HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<String>("Failed due to "+e,HttpStatus.INTERNAL_SERVER_ERROR);
			
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
	
	
	// ----------   
	
public ResponseEntity<?> triggerRuleService(AcountOrProcessLeadDetermination determination) throws ClientProtocolException, IOException, URISyntaxException{
		
		RuleInputProcessLeadDto ruleInput = new RuleInputProcessLeadDto();
		
		ruleInput.setCompCode(determination.getCompCode());
		ruleInput.setProcessLeadCheck(determination.getProcessLeadCheck());
		ruleInput.setVendorId(determination.getVednorId());
		
		
		String outPut = execute((RuleInputDto) ruleInput ,RuleConstants.menaBevRuleService);
		
		
	   // call convert statement and than get the ouput and return it .
		
	
		
		
		
		return null;
		
	}

/*@Override
public List<RuleOutputDto> convertFromJSonNodeRo(String node){
	List<ApproverDataOutputDto> approverList = new ArrayList<>();
	JSONObject jObj = new JSONObject(node);
	JSONArray arr = jObj.getJSONArray("Result");
	JSONArray innerArray = null;
	if(!arr.isNull(0))
		innerArray = arr.getJSONObject(0).getJSONArray("ReturnOrderRuleOutput");
	
	if(innerArray!=null) {
	for (int i = 0; i < innerArray.length(); i++) {
		Map<String,String> userMap = new HashMap<String,String>();
		 form map to add userDetails

	}
	return approverList;
	}

return null;
}*/
	
	
	protected String execute(RuleInputDto input, String rulesServiceId) throws ClientProtocolException, IOException, URISyntaxException {

		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		httpClient = getHTTPClient();
		
		// need to set still the ruleServiceId and than , process the output.
		
		String jwToken = getJwtTokenForAuthenticationForRulesSapApi();
		System.err.println("map for rulesToken : " +jwToken);
		httpPost = new HttpPost(RuleConstants.RULE_BASE_URL);
		httpPost.addHeader(CONTENT_TYPE, "application/json");

		httpPost.addHeader(AUTHORIZATION, "Bearer " +jwToken); // header

		String ruleInputString = input.toRuleInputString(rulesServiceId);
		StringEntity stringEntity = new StringEntity(ruleInputString);

		httpPost.setEntity(stringEntity);

		response = httpClient.execute(httpPost);
		System.err.println("response : " +response);

		// process your response here
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		InputStream inputStream = response.getEntity().getContent();
		byte[] data = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(data)) > 0) {
			bytes.write(data, 0, length);
		}
		String respBody = new String(bytes.toByteArray(), StandardCharsets.UTF_8);
		// clean-up sessions
		if (httpPost != null) {
			httpPost.releaseConnection();
		}
		if (response != null) {
			response.close();
		}
		if (httpClient != null) {
			httpClient.close();
		}
		return respBody;
	}
	
	public static String getJwtTokenForAuthenticationForRulesSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(RuleConstants.RULE_BASE_URL);
		httpPost.addHeader("Content-Type", "application/json");
		// Encoding username and password
		String auth = HelperClass.encodeUsernameAndPassword(RuleConstants.RULE_CLIENT_ID,
				RuleConstants.RULE_CLIENT_SECRETE);
		httpPost.addHeader("Authorization", auth);
		HttpResponse res = client.execute(httpPost);
		System.err.println( " 92 rest" + res);
		String data = HelperClass.getDataFromStream(res.getEntity().getContent());
		if (res.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String jwtToken = new JSONObject(data).getString("access_token");
			System.err.println("jwtProxyToken "+jwtToken);
				return jwtToken;
			}
		return null;
	}
	
	private static CloseableHttpClient getHTTPClient() {
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		CloseableHttpClient httpClient = clientBuilder.build();
		return httpClient;
	}

	// Trigger Workflow
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity<?> triggerWorkflow(WorkflowContextDto dto, String definitionId){
		try {
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();

			HttpClient client = HttpClientBuilder.create().build();
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL +"/v1/workflow-instances";
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("Authorization", "Bearer " + token);
			httpPost.addHeader("Content-Type", "application/json");
			try {
				String worklfowInputString = dto.toRuleInputString(definitionId);
				StringEntity entity = new StringEntity(worklfowInputString);
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
				HttpResponse response = client.execute(httpPost);
				System.err.println("WorkflowTriggerResponse ="+response);
				if (response.getStatusLine().getStatusCode() == HttpStatus.ACCEPTED.value()) {
					return new ResponseEntity(response, HttpStatus.ACCEPTED);
				} else {
					return new ResponseEntity(getDataFromStream(response.getEntity().getContent()),
							HttpStatus.BAD_REQUEST);
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
				return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR
						);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static String getDataFromStream(InputStream stream) throws IOException {
		StringBuilder dataBuffer = new StringBuilder();
		BufferedReader inStream = new BufferedReader(new InputStreamReader(stream));
		String data = "";

		while ((data = inStream.readLine()) != null) {
			dataBuffer.append(data);
		}
		inStream.close();
		return dataBuffer.toString();
	}

	/*public ResponseEntity cancellingWorkflowUsingOauthClient(String workflowId) {

		try {
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();

			HttpClient client = HttpClientBuilder.create().build();

			Map<String, Object> map = DestinationReaderUtil
					.getDestination(DkshConstants.WORKFLOW_CLOSE_TASK_DESTINATION);
			
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/workflow-instances/" + workflowId;
			String payload = "{\"context\": {},\"status\":\"CANCELED\"}";

			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.addHeader("Authorization", "Bearer " + token);
			httpPatch.addHeader("Content-Type", "application/json");

			try {
				StringEntity entity = new StringEntity(payload);
				entity.setContentType("application/json");
				httpPatch.setEntity(entity);
				HttpResponse response = client.execute(httpPatch);

				if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
					return new ResponseEntity("", HttpStatus.NO_CONTENT, "Workflow Task is cancelled",
							ResponseStatus.SUCCESS);
				} else {
					return new ResponseEntity(getDataFromStream(response.getEntity().getContent()),
							HttpStatus.BAD_REQUEST, "Task updating is failed", ResponseStatus.FAILED);
				}

			} catch (IOException e) {
				logger.error(e.getMessage());
				return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR,
						DkshConstants.EXCEPTION_FAILED + e.getCause().getCause().getLocalizedMessage(),
						ResponseStatus.FAILED);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR, DkshConstants.EXCEPTION_FAILED + e,
					ResponseStatus.FAILED);
		}

	}*/
	
}
