  package com.ap.menabev.serviceimpl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
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
import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.CostAllocationDto;
import com.ap.menabev.dto.CostAllocationsDto;
import com.ap.menabev.dto.CreateInvoiceHeaderChangeDto;
import com.ap.menabev.dto.CreateInvoiceHeaderDto;
import com.ap.menabev.dto.DashBoardDetailsDto;
import com.ap.menabev.dto.FilterHeaderDto;
import com.ap.menabev.dto.HeaderCheckDto;
import com.ap.menabev.dto.HeaderMessageDto;
import com.ap.menabev.dto.InboxDto;
import com.ap.menabev.dto.InboxOutputDto;
import com.ap.menabev.dto.InboxResponseOutputDto;
import com.ap.menabev.dto.InvResponseHeaderDto;
import com.ap.menabev.dto.InvoiceHeaderChangeDto;
import com.ap.menabev.dto.InvoiceHeaderDashBoardDto;
import com.ap.menabev.dto.InvoiceHeaderDetailsDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceItemAcctAssignmentDto;
import com.ap.menabev.dto.InvoiceItemDto;
import com.ap.menabev.dto.InvoiceItemsDto;
import com.ap.menabev.dto.ItemMessageDto;
import com.ap.menabev.dto.MasterResponseDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.RuleInputDto;
import com.ap.menabev.dto.RuleInputProcessLeadDto;
import com.ap.menabev.dto.StatusCountDto;
import com.ap.menabev.dto.TriggerWorkflowContext;
import com.ap.menabev.dto.WorkflowContextDto;
import com.ap.menabev.dto.WorkflowTaskOutputDto;
import com.ap.menabev.entity.AttachmentDo;
import com.ap.menabev.entity.CommentDo;
import com.ap.menabev.entity.CostAllocationDo;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;
import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.entity.StatusConfigDo;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.CostAllocationRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepoFilter;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.invoice.InvoiceItemAcctAssignmentRepository;
import com.ap.menabev.invoice.InvoiceItemRepository;
import com.ap.menabev.invoice.ReasonForRejectionRepository;
import com.ap.menabev.invoice.StatusConfigRepository;
import com.ap.menabev.service.InvoiceHeaderService;
import com.ap.menabev.service.InvoiceItemService;
import com.ap.menabev.service.RuleConstants;
import com.ap.menabev.service.SequenceGeneratorService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.DestinationReaderUtil;
import com.ap.menabev.util.HelperClass;
import com.ap.menabev.util.MenabevApplicationConstant;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.ServiceUtil;
import com.ap.menabev.util.WorkflowConstants;
import com.google.gson.Gson;

@Service
public class InvoiceHeaderServiceImpl implements InvoiceHeaderService {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceHeaderServiceImpl.class);
	
	
	
	@Autowired
	InvoiceHeaderRepoFilter invoiceHeaderRepoFilter;
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
	
	@Autowired
	private SequenceGeneratorService seqService;


	
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

	
	@Override
	public ResponseEntity<?>  deleteDraft(List<String> requestId){
		
		try{
			requestId.stream().forEach(item -> {
		invoiceHeaderRepository.deleteById(item);
		invoiceItemRepository.deleteTotalItems(item);
		costAllocationRepository.deleteCostAllocationDo(item);
		invoiceItemAcctAssignmentRepository.deleteByRequestIdItemId(item);
			});
		
		ResponseDto reponse = new ResponseDto();
		reponse.setCode("200");
		reponse.setMessage("Deleted Draft SucessFully");
		reponse.setStatus("Sucess");
		return new ResponseEntity<ResponseDto>(reponse,HttpStatus.OK);
		}catch(Exception e){
			
			ResponseDto reponse = new ResponseDto();
			reponse.setCode("500");
			reponse.setMessage("Deleted Failed "+ e.toString());
			reponse.setStatus("Sucess");
			return new ResponseEntity<ResponseDto>(reponse,HttpStatus.OK);
		}
		
		
	}
	
	
	// create invoice for PO/NON po 
	@Override
	public ResponseEntity<?> save(CreateInvoiceHeaderDto invoiceDto){
		try{
			
			String requestId = null;
			// Generate Request Id 
			if(invoiceDto.getInvoiceHeaderDto().getRequestId()!=null && !invoiceDto.getInvoiceHeaderDto().getRequestId().isEmpty()){
				requestId = invoiceDto.getInvoiceHeaderDto().getRequestId();
			}else{
				requestId  =  seqService.getSequenceNoByMappingId(
					MenabevApplicationConstant.INVOICE_SEQUENCE,"INV"
					);}
		    // save header
			 InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),InvoiceHeaderDo.class);
			    invoiceHeaderDo.setRequestId(requestId);
			    if(!invoiceDto.getInvoiceHeaderDto().getValidationStatus().isEmpty() && invoiceDto.getInvoiceHeaderDto().getValidationStatus()!=null){
				invoiceHeaderDo.setValidationStatus("Draft");
			    }
			    if(!invoiceDto.getInvoiceHeaderDto().getDocStatus().isEmpty() && invoiceDto.getInvoiceHeaderDto().getDocStatus()!=null)
				{invoiceHeaderDo.setDocStatus("Draft");}
			 InvoiceHeaderDo invoiceSavedDo  = invoiceHeaderRepository.save(invoiceHeaderDo);
			    // save invoice item
			    if(invoiceDto.getInvoiceHeaderDto().getInvoiceItems()!=null && !invoiceDto.getInvoiceHeaderDto().getInvoiceItems().isEmpty()){
			List<InvoiceItemDo> itemlists =  ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getInvoiceItems(), InvoiceItemDo.class);
			itemlists.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
			 invoiceItemRepository.saveAll(itemlists);
			    }
			    if(invoiceDto.getInvoiceItemAcctAssignmentDto()!=null && !invoiceDto.getInvoiceItemAcctAssignmentDto().isEmpty()){
					// save invoice Account Assingment 
					List<InvoiceItemAcctAssignmentDo>  listAccountAssignement = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemAcctAssignmentDto(), InvoiceItemAcctAssignmentDo.class);
					listAccountAssignement.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
					item.setAccountAssgnGuid(UUID.randomUUID().toString());});
					
					invoiceItemAcctAssignmentRepository.saveAll(listAccountAssignement);
					    }
					    if(invoiceDto.getCostAllocationDto()!=null && !invoiceDto.getCostAllocationDto().isEmpty()){
					// save cost allocation
					List<CostAllocationDo>  listCostAllocation = ObjectMapperUtils.mapAll(invoiceDto.getCostAllocationDto(), CostAllocationDo.class);
					listCostAllocation.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
					item.setCostAllocationId(UUID.randomUUID().toString());
					});
					costAllocationRepository.saveAll(listCostAllocation);
					    }
					    if(invoiceDto.getInvoiceHeaderDto().getAttachments()!=null && !invoiceDto.getInvoiceHeaderDto().getAttachments().isEmpty()){
					    	List<AttachmentDo> attachementList = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getAttachments(), AttachmentDo.class);
					    	attachementList.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
					    	attachmentRepository.saveAll(attachementList);
					    }
					    if(invoiceDto.getInvoiceHeaderDto().getComments()!=null && !invoiceDto.getInvoiceHeaderDto().getComments().isEmpty()){
					    	List<CommentDo> commentList = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getComments(), CommentDo.class);
					    	commentList.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
					    	item.setCommentId(UUID.randomUUID().toString());});
					    	
					    	commentRepository.saveAll(commentList);
					    }
					    invoiceDto.getInvoiceHeaderDto().setRequestId(requestId);
			    invoiceDto.setResponseStatus("Invoice "+invoiceSavedDo.getRequestId() +" saved as draft");
		return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto,HttpStatus.OK);
		} catch (Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//Submit invoice 
	@Override
	public ResponseEntity<?> submitForNonPo(CreateInvoiceHeaderDto invoiceDto){
		try{
			// Flow is for Non PO Accountant created invoice 
			// Determine RUle for Process lead 
			 String requestId = null;
			AcountOrProcessLeadDetermination determination = new AcountOrProcessLeadDetermination();
			determination.setCompCode("1010");
			determination.setProcessLeadCheck("Process Lead");
			determination.setVednorId(invoiceDto.getInvoiceHeaderDto().getVendorId());
		            ResponseEntity<?> responseRules =    triggerRuleService(determination);
		            System.err.println("responseFrom Rules "+ responseRules);
		            if(responseRules.getStatusCodeValue()==200){
		            	@SuppressWarnings("unchecked")
						List<ApproverDataOutputDto> lists =(List<ApproverDataOutputDto>) responseRules.getBody();
			
		            	
		            	// Generate Request Id 
			if(invoiceDto.getInvoiceHeaderDto().getRequestId()!=null && !invoiceDto.getInvoiceHeaderDto().getRequestId().isEmpty()){
				requestId = invoiceDto.getInvoiceHeaderDto().getRequestId();
			}else{
				requestId  =  seqService.getSequenceNoByMappingId(
					MenabevApplicationConstant.INVOICE_SEQUENCE,"INV"
					);}
			 // Trigger worklfow , which will direct to process lead 
			    TriggerWorkflowContext context = new TriggerWorkflowContext();
			context.setRequestId(requestId);
			context.setInvoiceReferenceNumber(invoiceDto.getInvoiceHeaderDto().getExtInvNum());
			context.setNonPo(true);
			context.setManualNonPo(true);
			context.setAccountantGroup(invoiceDto.getInvoiceHeaderDto().getTaskOwner());
			context.setAccountantUser(invoiceDto.getInvoiceHeaderDto().getTaskOwnerId());
			context.setProcessLead(lists.get(0).getUserOrGroup());
			
		ResponseEntity<?> response  = triggerWorkflow((WorkflowContextDto)context,"triggerresolutionworkflow.triggerresolutionworkflow");
		System.err.println("response of workflow Trigger "+response);
		WorkflowTaskOutputDto  taskOutputDto= (WorkflowTaskOutputDto) response.getBody();
		// save invoice header
		InvoiceHeaderDo invoiceHeaderDo = ObjectMapperUtils.map(invoiceDto.getInvoiceHeaderDto(),InvoiceHeaderDo.class);
		invoiceHeaderDo.setRequestId(requestId);
		invoiceHeaderDo.setValidationStatus("Open");
		invoiceHeaderDo.setDocStatus("Created");
		invoiceHeaderDo.setWorkflowId(taskOutputDto.getId());
		invoiceHeaderDo.setCreatedAt(LocalDateTime.parse(taskOutputDto.getStartedAt().subSequence(0,taskOutputDto.getStartedAt().length()-5)));
	   invoiceHeaderDo.setAssignedTo(lists.get(0).getUserOrGroup());
		InvoiceHeaderDo 	invoiceSavedDo = invoiceHeaderRepository.save(invoiceHeaderDo);
		    // save invoice item 
	    if(invoiceDto.getInvoiceHeaderDto().getInvoiceItems()!=null &&!invoiceDto.getInvoiceHeaderDto().getInvoiceItems().isEmpty()){
			List<InvoiceItemDo> itemlists =  ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getInvoiceItems(), InvoiceItemDo.class);
			itemlists.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
			   item.setId(UUID.randomUUID().toString());});
			 invoiceItemRepository.saveAll(itemlists);
			    }
		    if(invoiceDto.getInvoiceItemAcctAssignmentDto()!=null && !invoiceDto.getInvoiceItemAcctAssignmentDto().isEmpty()){
		// save invoice Account Assingment 
		List<InvoiceItemAcctAssignmentDo>  listAccountAssignement = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceItemAcctAssignmentDto(), InvoiceItemAcctAssignmentDo.class);
		listAccountAssignement.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
		item.setAccountAssgnGuid(UUID.randomUUID().toString());});
		
		invoiceItemAcctAssignmentRepository.saveAll(listAccountAssignement);
		    }
		    if(invoiceDto.getCostAllocationDto()!=null && !invoiceDto.getCostAllocationDto().isEmpty()){
		// save cost allocation
		List<CostAllocationDo>  listCostAllocation = ObjectMapperUtils.mapAll(invoiceDto.getCostAllocationDto(), CostAllocationDo.class);
		listCostAllocation.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
		item.setCostAllocationId(UUID.randomUUID().toString());
		});
		costAllocationRepository.saveAll(listCostAllocation);
		    }
		    if(invoiceDto.getInvoiceHeaderDto().getAttachments()!=null && !invoiceDto.getInvoiceHeaderDto().getAttachments().isEmpty()){
		    	List<AttachmentDo> attachementList = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getAttachments(), AttachmentDo.class);
		    	System.err.println("attachementList "+attachementList);
		    	attachementList.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());});
		    	attachmentRepository.saveAll(attachementList);
		    }
		    if(invoiceDto.getInvoiceHeaderDto().getComments()!=null && !invoiceDto.getInvoiceHeaderDto().getComments().isEmpty()){
		    	List<CommentDo> commentList = ObjectMapperUtils.mapAll(invoiceDto.getInvoiceHeaderDto().getComments(), CommentDo.class);
		    	commentList.stream().forEach(item->{item.setRequestId(invoiceSavedDo.getRequestId());
		    	item.setCommentId(UUID.randomUUID().toString());});
		    	
		    	commentRepository.saveAll(commentList);
		    }
		// Update the Activity Log table 
		    updateActivityLog();
		    if(lists.get(0).getUserType().equals("Default")){
		    invoiceDto.setResponseStatus("Invoice "+invoiceSavedDo.getRequestId()+" has been created & task is available to Default User "+lists.get(0).getUserOrGroup());
		    }else{
		    	 invoiceDto.setResponseStatus("Invoice "+invoiceSavedDo.getRequestId()+" has been created & task is available to User "+lists.get(0).getUserOrGroup());	
		    }
		    return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceDto,HttpStatus.OK);
		            }else{
		            	return new ResponseEntity<>(responseRules ,HttpStatus.OK);
		            }
		            
		            }catch (Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public ResponseEntity<?> updateActivityLog(){
		

		return null;
	}
	
	@Override
	public ResponseEntity<?>  getInboxTask(FilterHeaderDto filterDto){
		try {
			if (filterDto.getIndexNum() != null && filterDto.getCount() != null && filterDto.getIndexNum() != 0 && filterDto.getCount() != 0) {
				if (!HelperClass.checkString(filterDto.getUserId())) {
					List<InvoiceHeaderDo>  lists = null;
						ResponseEntity<?> responseFromSapApi = fetchWorkflowUserTaksList(filterDto);
						if (responseFromSapApi.getStatusCodeValue() == HttpStatus.OK.value()) {
							@SuppressWarnings("unchecked")
							List<WorkflowTaskOutputDto> listOfWorkflowTasks = (List<WorkflowTaskOutputDto>) responseFromSapApi
									.getBody();
							logger.error("listOfWorkflowTasks : " + listOfWorkflowTasks);
							System.err.println("listOfWorkflowTasks :" +listOfWorkflowTasks.size());
							// check condition for accountant role to fetch draft along with his user tasks 
							if (!listOfWorkflowTasks.isEmpty()  || (listOfWorkflowTasks.isEmpty() && filterDto.getRoleOfUser().equals("Accountant")) ) {
								if(filterDto.getRoleOfUser().equals("Accountant")&&filterDto.getMyTask().equals("DRAFT")){
									// than add the requesId with Draft as docStatus 
									if(filterDto.getRequestId()!=null && !filterDto.getRequestId().isEmpty() ){
										lists = invoiceHeaderRepository.getInvoiceHeaderDocStatusByUserIdAndRequestId(filterDto.getUserId(),filterDto.getRequestId());
									}else {
										lists = invoiceHeaderRepository.getInvoiceHeaderDocStatusByUserId(filterDto.getUserId());
										System.err.println("list of drafts for accountant = " + lists );
									}
									if(lists!=null && !lists.isEmpty()){
										for(int i=0;i<lists.size();i++){
											
											WorkflowTaskOutputDto workflow = new WorkflowTaskOutputDto();
											workflow.setSubject(lists.get(i).getRequestId());
											workflow.setProcessor(filterDto.getUserId());
											listOfWorkflowTasks.add(workflow);
										}
									}
								}
								
								long min = (filterDto.getIndexNum()  * filterDto.getCount()) - filterDto.getCount();
								long max = (filterDto.getIndexNum()  * filterDto.getCount());
								if(listOfWorkflowTasks!=null && !listOfWorkflowTasks.isEmpty()){
								if (min < listOfWorkflowTasks.size()) {
									if (max > listOfWorkflowTasks.size()) {
										max = listOfWorkflowTasks.size();
									}
									System.err.println("list of workflowIDs  for accountant = " + listOfWorkflowTasks );
									return new ResponseEntity<>(fetchInvoiceDocHeaderDtoListFromRequestNumber(
											listOfWorkflowTasks.stream().skip(min).limit(max)
													.collect(Collectors.toList()),filterDto),
											HttpStatus.OK);
								} else {
									return new ResponseEntity<String>(
											"DATA_NOT_FOUND"
													+ " Select different Index number or Change the count number.",
											HttpStatus.OK);
								}
								}else {
									return new ResponseEntity<String>("No tasks are available."
													,HttpStatus.OK);
								}
							} else {
								return new ResponseEntity<String>("No tasks are available."
									, HttpStatus.OK);
							}
						} else {
							return responseFromSapApi;
						}
				} else {
					return new ResponseEntity<>(
							"INVALID_INPUT" + "Please provide login in user id ",
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>("INVALID_INPUT" + " Provide valid index num and count.",
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	@Override
	public ResponseEntity<?>  getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto){
		try {
			if (filterDto.getIndexNum() != null && filterDto.getCount() != null && filterDto.getIndexNum() != 0 && filterDto.getCount() != 0) {
				if (!HelperClass.checkString(filterDto.getUserId())) {
					List<InvoiceHeaderDo>  lists = null;
						ResponseEntity<?> responseFromSapApi = fetchWorkflowUserTaksListMultiple(filterDto);
						if (responseFromSapApi.getStatusCodeValue() == HttpStatus.OK.value()) {
							@SuppressWarnings("unchecked")
							List<WorkflowTaskOutputDto> listOfWorkflowTasks = (List<WorkflowTaskOutputDto>) responseFromSapApi
									.getBody();
							logger.error("listOfWorkflowTasks : " + listOfWorkflowTasks);
							System.err.println("listOfWorkflowTasks :" +listOfWorkflowTasks.size());
							// check condition for accountant role to fetch draft along with his user tasks 
							if (!listOfWorkflowTasks.isEmpty()  || (listOfWorkflowTasks.isEmpty() && filterDto.getRoleOfUser().equals("Accountant")) ) {
								if(filterDto.getRoleOfUser().equals("Accountant")&&filterDto.getMyTask().equals("DRAFT")){
									// than add the requesId with Draft as docStatus 
									if(filterDto.getRequestId()!=null && !filterDto.getRequestId().isEmpty() ){
										lists = invoiceHeaderRepository.getInvoiceHeaderDocStatusByUserIdAndRequestId(filterDto.getUserId(),filterDto.getRequestId());
									}else {
										lists = invoiceHeaderRepository.getInvoiceHeaderDocStatusByUserId(filterDto.getUserId());
										System.err.println("list of drafts for accountant = " + lists );
									}
									if(lists!=null && !lists.isEmpty()){
										for(int i=0;i<lists.size();i++){
											
											WorkflowTaskOutputDto workflow = new WorkflowTaskOutputDto();
											workflow.setSubject(lists.get(i).getRequestId());
											workflow.setProcessor(filterDto.getUserId());
											listOfWorkflowTasks.add(workflow);
										}
									}
								}
								
								long min = (filterDto.getIndexNum()  * filterDto.getCount()) - filterDto.getCount();
								long max = (filterDto.getIndexNum()  * filterDto.getCount());
								if(listOfWorkflowTasks!=null && !listOfWorkflowTasks.isEmpty()){
								if (min < listOfWorkflowTasks.size()) {
									if (max > listOfWorkflowTasks.size()) {
										max = listOfWorkflowTasks.size();
									}
									System.err.println("list of workflowIDs  for accountant = " + listOfWorkflowTasks );
									return new ResponseEntity<>(fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(
											listOfWorkflowTasks.stream().skip(min).limit(max)
													.collect(Collectors.toList()),filterDto),
											HttpStatus.OK);
								} else {
									return new ResponseEntity<String>(
											"DATA_NOT_FOUND"
													+ " Select different Index number or Change the count number.",
											HttpStatus.OK);
								}
								}else {
									return new ResponseEntity<String>("No tasks are available."
													,HttpStatus.OK);
								}
							} else {
								return new ResponseEntity<String>("No tasks are available."
									, HttpStatus.OK);
							}
						} else {
							return responseFromSapApi;
						}
				} else {
					return new ResponseEntity<>(
							"INVALID_INPUT" + "Please provide login in user id ",
							HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>("INVALID_INPUT" + " Provide valid index num and count.",
						HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	public ResponseEntity<?> fetchWorkflowUserTaksList(FilterHeaderDto filter){
		try {
			if (!checkString(filter.getUserId())) {
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<>();
				if(!filter.getMyTask().equals("DRAFT")){
				String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
				
				HttpClient client = HttpClientBuilder.create().build();
				
				StringBuilder url = new StringBuilder();
				
				
				appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
						WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
				if(filter.getMyTask().equals("MYTASK")){
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_RESERVED_VALUE);
					appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, filter.getUserId());
				}else{
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_VALUE);
				appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, filter.getUserId());
				appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, "");
				}
				if(filter.getRequestId()!=null && !filter.getRequestId().isEmpty() ){
					appendParamInUrl(url, WorkflowConstants.SUBJECT,filter.getRequestId() );
					}	
				appendParamInUrl(url, WorkflowConstants.FIND_COUNT_OF_TASKS_KEY,
						WorkflowConstants.FIND_COUNT_OF_TASKS_VALUE);
				appendParamInUrl(url, WorkflowConstants.TOP_KEY, WorkflowConstants.TOP_VALUE);
				
				url.insert(0, (MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances?"));

				System.err.println("URL : " + url);

				HttpGet httpGet = new HttpGet(url.toString());
				
				httpGet.addHeader("Content-Type", "application/json");
				// Encoding username and password
				//String auth = encodeUsernameAndPassword((String) map.get("User"), (String) map.get("Password"));
				httpGet.addHeader("Authorization", "Bearer "+jwToken);

				HttpResponse response = client.execute(httpGet);
				String dataFromStream = getDataFromStream(response.getEntity().getContent());
				if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {

					JSONArray jsonArray = new JSONArray(dataFromStream);

					

					jsonArray.forEach(jsonObject -> {
						WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
								WorkflowTaskOutputDto.class);
						taskDto.setRequestIds(taskDto.getDescription());
						//taskDto.setRoType(taskDto.getDescription().split("\\|")[8]);
						listOfWorkflowTasks.add(taskDto);

					});
					if (!listOfWorkflowTasks.isEmpty()) {
						return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
					} else {
						return new ResponseEntity<>(listOfWorkflowTasks ,
								HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<String>("FETCHING FAILED", HttpStatus.CONFLICT);

				}
			
		}else{
			return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
		}
			}else {
				return new ResponseEntity<>("INVALID_INPUT_PLEASE_RETRY" + " with provide USER ID.",
						HttpStatus.BAD_REQUEST);
			}
			}catch (Exception e) {
				return new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
	}
	
	
	
	// for multiple
	
	public ResponseEntity<?> fetchWorkflowUserTaksListMultiple(FilterMultipleHeaderSearchDto filter){
		try {
			if (!checkString(filter.getUserId())) {
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<>();
				if(!filter.getMyTask().equals("DRAFT")){
				String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
				
				HttpClient client = HttpClientBuilder.create().build();
				
				StringBuilder url = new StringBuilder();
				
				
				appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
						WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
				if(filter.getMyTask().equals("MYTASK")){
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_RESERVED_VALUE);
					appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, filter.getUserId());
				}else{
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_VALUE);
				appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, filter.getUserId());
				appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, "");
				}
				if(filter.getRequestId()!=null && !filter.getRequestId().isEmpty() ){
					appendParamInUrl(url, WorkflowConstants.SUBJECT,filter.getRequestId() );
					}	
				appendParamInUrl(url, WorkflowConstants.FIND_COUNT_OF_TASKS_KEY,
						WorkflowConstants.FIND_COUNT_OF_TASKS_VALUE);
				appendParamInUrl(url, WorkflowConstants.TOP_KEY, WorkflowConstants.TOP_VALUE);
				
				url.insert(0, (MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances?"));

				System.err.println("URL : " + url);

				HttpGet httpGet = new HttpGet(url.toString());
				
				httpGet.addHeader("Content-Type", "application/json");
				// Encoding username and password
				//String auth = encodeUsernameAndPassword((String) map.get("User"), (String) map.get("Password"));
				httpGet.addHeader("Authorization", "Bearer "+jwToken);

				HttpResponse response = client.execute(httpGet);
				String dataFromStream = getDataFromStream(response.getEntity().getContent());
				if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {

					JSONArray jsonArray = new JSONArray(dataFromStream);

					

					jsonArray.forEach(jsonObject -> {
						WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
								WorkflowTaskOutputDto.class);
						taskDto.setRequestIds(taskDto.getDescription());
						//taskDto.setRoType(taskDto.getDescription().split("\\|")[8]);
						listOfWorkflowTasks.add(taskDto);

					});
					if (!listOfWorkflowTasks.isEmpty()) {
						return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
					} else {
						return new ResponseEntity<>(listOfWorkflowTasks ,
								HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<String>("FETCHING FAILED", HttpStatus.CONFLICT);

				}
			
		}else{
			return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
		}
			}else {
				return new ResponseEntity<>("INVALID_INPUT_PLEASE_RETRY" + " with provide USER ID.",
						HttpStatus.BAD_REQUEST);
			}
			}catch (Exception e) {
				return new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);

			}
	}
	
	// add to this Helper Class 
	public static boolean checkString(String s) {
		if (s == null || s.equals("") || s.trim().isEmpty() || s.matches("") || s.equals(null)) {
			return true;
		}
		return false;
	}
	
	public static void appendParamInUrl(StringBuilder url, String key, String value) {
		if (url.length() > 0) {
			url.append("&" + key + "=" + value);
		} else {
			url.append(key + "=" + value);
		}

	}
	
	@SuppressWarnings("unchecked")
	private ResponseEntity<?> fetchInvoiceDocHeaderDtoListFromRequestNumber(
			List<WorkflowTaskOutputDto> invoiceRequestIdListPaginated,FilterHeaderDto filterDto) {
		
		List<InvoiceHeaderDo> listOfInvoiceOrders = null;
		ResponseEntity<?> responseFromFilter = null;
		// workflowResponse with list of requestIds
        List<String> requestIds =  invoiceRequestIdListPaginated.stream().map(WorkflowTaskOutputDto::getSubject).collect(Collectors.toList());
        System.err.println("requestId "+requestIds);
       /* List<InvoiceHeaderDo> listOfInvoiceOrders = invoiceHeaderRepository
				.findByRequestIdIn(requestId);*/
        // List of requestIds fetched for user from user task  
           responseFromFilter = filterInvoices(requestIds,filterDto);
          if(responseFromFilter.getStatusCodeValue()==200){
        	 listOfInvoiceOrders=  (List<InvoiceHeaderDo>) responseFromFilter.getBody();
		System.err.println("lisOFInvoice by requestIds "+listOfInvoiceOrders);
		// check for claim 
		Map<String,WorkflowTaskOutputDto> map = checkForClaim(invoiceRequestIdListPaginated);
		// filter of the invoice. create a method at night for filter of invoice
		List<InvoiceHeaderDto> invoiceHeaderList  = ObjectMapperUtils.mapAll(listOfInvoiceOrders, InvoiceHeaderDto.class);
		logger.error("Before sorting : " + invoiceHeaderList);
		invoiceHeaderList.sort(Comparator.comparing(InvoiceHeaderDto::getCreatedAt,
				Comparator.nullsLast((d1, d2) -> d2.compareTo(d1))));
		logger.error("After sorting : " + invoiceHeaderList);
		List<InboxOutputDto> inboxOutputList = new ArrayList<InboxOutputDto>();
		 for(int i = 0 ; i<invoiceHeaderList.size();i++){
			 InboxOutputDto inbox = new InboxOutputDto();
			 inbox = ObjectMapperUtils.map(invoiceHeaderList.get(i),InboxOutputDto.class);
			 WorkflowTaskOutputDto workflowOutPut  = map.get(inbox.getRequestId());
		// if process exist or not null for the filtered requestid 
		if(workflowOutPut.getProcessor() != null  && !workflowOutPut.getProcessor().isEmpty()){
			inbox.setClaimed(true);
		}else {
			inbox.setClaimed(false); 
		}
		
		inbox.setTaskStatus(workflowOutPut.getStatus());
		inbox.setTaskId(workflowOutPut.getId());
	    inboxOutputList.add(inbox);
		 }
		 InboxResponseOutputDto response = new InboxResponseOutputDto();
	      
		 response.setCount(inboxOutputList.size());
		 response.setListOfTasks(inboxOutputList);
		return  new ResponseEntity<InboxResponseOutputDto>(response,HttpStatus.OK);
		
          } else {
        	  return new ResponseEntity<String>("No Data Found For the Filter Query ",HttpStatus.NO_CONTENT); 
          }
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private ResponseEntity<?> fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(
			List<WorkflowTaskOutputDto> invoiceRequestIdListPaginated,FilterMultipleHeaderSearchDto filterDto) {
		
		List<InvoiceHeaderDo> listOfInvoiceOrders = null;
		ResponseEntity<?> responseFromFilter = null;
		// workflowResponse with list of requestIds
        List<String> requestIds =  invoiceRequestIdListPaginated.stream().map(WorkflowTaskOutputDto::getSubject).collect(Collectors.toList());
        System.err.println("requestId "+requestIds);
       /* List<InvoiceHeaderDo> listOfInvoiceOrders = invoiceHeaderRepository
				.findByRequestIdIn(requestId);*/
        // List of requestIds fetched for user from user task  
           responseFromFilter = filterInvoicesMultiple(requestIds,filterDto);
          if(responseFromFilter.getStatusCodeValue()==200){
        	 listOfInvoiceOrders=  (List<InvoiceHeaderDo>) responseFromFilter.getBody();
		System.err.println("lisOFInvoice by requestIds "+listOfInvoiceOrders);
		
		// check for claim 
		Map<String,WorkflowTaskOutputDto> map = checkForClaim(invoiceRequestIdListPaginated);
		// filter of the invoice. create a method at night for filter of invoice
		List<InvoiceHeaderDto> invoiceHeaderList  = ObjectMapperUtils.mapAll(listOfInvoiceOrders, InvoiceHeaderDto.class);
		logger.error("Before sorting : " + invoiceHeaderList);
		invoiceHeaderList.sort(Comparator.comparing(InvoiceHeaderDto::getCreatedAt,
				Comparator.nullsLast((d1, d2) -> d2.compareTo(d1))));
		logger.error("After sorting : " + invoiceHeaderList);
		List<InboxOutputDto> inboxOutputList = new ArrayList<InboxOutputDto>();
		 for(int i = 0 ; i<invoiceHeaderList.size();i++){
			 InboxOutputDto inbox = new InboxOutputDto();
			 inbox = ObjectMapperUtils.map(invoiceHeaderList.get(i),InboxOutputDto.class);
			 WorkflowTaskOutputDto workflowOutPut  = map.get(inbox.getRequestId());
		// if process exist or not null for the filtered requestid 
		if(workflowOutPut.getProcessor() != null  && !workflowOutPut.getProcessor().isEmpty()){
			inbox.setClaimed(true);
		}else {
			inbox.setClaimed(false); 
		}
		
		inbox.setTaskStatus(workflowOutPut.getStatus());
		inbox.setTaskId(workflowOutPut.getId());
	    inboxOutputList.add(inbox);
		 }
		 InboxResponseOutputDto response = new InboxResponseOutputDto();
	      
		 response.setCount(inboxOutputList.size());
		 response.setListOfTasks(inboxOutputList);
		return  new ResponseEntity<InboxResponseOutputDto>(response,HttpStatus.OK);
		
          } else {
        	  return new ResponseEntity<String>("No Data Found For the Filter Query ",HttpStatus.NO_CONTENT); 
          }
	}

	
	
	
	
	private ResponseEntity<?> filterInvoices(List<String> requestId,FilterHeaderDto dto){
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (requestId != null && !requestId.isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < requestId.size(); i++) {
				if (i < requestId.size() - 1) {
					rqstId.append("'" + requestId.get(i) + "'" + ",");
				} else {
					rqstId.append("'" + requestId.get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.REQUEST_ID IN", "(" + rqstId + ")");
		}
		if (dto.getVendorName() != null && !dto.getVendorName().isEmpty()) {
			filterQueryMap.put(" RR.VENDOR_NAME =", "'" + dto.getVendorName()+ "'");
		}
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			filterQueryMap.put(" RR.VENDOR_ID =", "'" + dto.getVendorId()+ "'");
		} 
		if (dto.getDueDateFrom() != null && dto.getDueDateTo()!=null&& dto.getDueDateFrom()!=0&&dto.getDueDateTo()!=00) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ",  + dto.getDueDateFrom() + " AND "+ dto.getDueDateTo());
		 // is empty 
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateFrom() != 0) {
			filterQueryMap.put(" RR.DUE_DATE =", dto.getDueDateFrom() + "");
		//correct 
		}
		
		if (dto.getCreatedAtFrom() != null && dto.getCreatedAtFrom()!=null && dto.getCreatedAtFrom() != 0 && dto.getCreatedAtFrom()!= 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ", dto.getCreatedAtFrom() + " AND "+ dto.getCreatedAtTo());
		//correct 
		}
		if (dto.getInvoiceDateFrom() != null && dto.getInvoiceDateFrom() != 0  ) {
			filterQueryMap.put(" RR.INVOICE_DATE =",  dto.getCreatedAtFrom()+"");
		//correct 
		}
		if (dto.getInvoiceType() != null && !dto.getInvoiceType().isEmpty()) {
			filterQueryMap.put(" RR.INVOICE_TYPE =", "'" + dto.getInvoiceType() + "'");
		//checked 
		}
		
		if (dto.getAssignedTo() != null && !dto.getAssignedTo().isEmpty()) {
			filterQueryMap.put(" RR.ASSIGNED_TO =", "'" + dto.getAssignedTo() + "'");
		// is EMpty 
		}
		if (dto.getValidationStatus() != null && !dto.getValidationStatus().isEmpty()) {
			
			StringBuffer status = new StringBuffer();
			for (int i = 0; i < dto.getValidationStatus().size(); i++) {
				if (i < dto.getValidationStatus().size() - 1) {
					status.append("'" + dto.getValidationStatus().get(i) + "'" + ",");
				} else {
					status.append("'" + dto.getValidationStatus().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VALIDATION_STATUS IN", "(" + status + ")");//procss status
		}
		if (dto.getExtInvNum() != null && !dto.getExtInvNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "'" + dto.getExtInvNum() + "'");
		}
		if (dto.getInvoiceValueFrom()!=0.0) {
			filterQueryMap.put(" RR.INVOICE_TOTAL =",  "" + dto.getInvoiceValueFrom() + "");
		}
		if (dto.getInvoiceValueFrom() !=0 && dto.getInvoiceValueTo()!=0.0) {
			filterQueryMap.put(" RR.INVOICE_TOTAL BETWEEN ", dto.getInvoiceValueFrom() + " AND "+ dto.getInvoiceValueTo());
		//correct 
		}
		
		
		
		int lastAppendingAndIndex = filterQueryMap.size() - 1;

		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilterDetails(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return new ResponseEntity<List<InvoiceHeaderDo>>(invoiceOrderList, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("No Data Found For Searched Criteria", HttpStatus.NO_CONTENT);
		}
	}
	
	
	
	private ResponseEntity<?> filterInvoicesMultiple(List<String> requestId,FilterMultipleHeaderSearchDto dto){
		List<InvoiceHeaderDo> invoiceOrderList = null;
		StringBuffer query = new StringBuffer();
		Map<String, String> filterQueryMap = new HashMap<String, String>();

		query.append("SELECT * FROM INVOICE_HEADER RR WHERE");
		if (requestId != null && !requestId.isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < requestId.size(); i++) {
				if (i < requestId.size() - 1) {
					rqstId.append("'" + requestId.get(i) + "'" + ",");
				} else {
					rqstId.append("'" + requestId.get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.REQUEST_ID IN", "(" + rqstId + ")");
		}
		if (dto.getVendorName() != null && !dto.getVendorName().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorName().size(); i++) {
				if (i < dto.getVendorName().size() - 1) {
					rqstId.append("'" + dto.getVendorName().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorName().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_NAME IN", "(" + rqstId + ")");
		}
		if (dto.getVendorId() != null && !dto.getVendorId().isEmpty()) {
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getVendorId().size(); i++) {
				if (i < dto.getVendorId().size() - 1) {
					rqstId.append("'" + dto.getVendorId().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getVendorId().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VENDOR_ID IN",  "(" + rqstId + ")");
		} 
		if (dto.getDueDateFrom() != null && dto.getDueDateTo()!=null && dto.getDueDateFrom()!=0 && dto.getDueDateTo()!=0) {
			filterQueryMap.put(" RR.DUE_DATE BETWEEN ",  + dto.getDueDateFrom() + " AND "+ dto.getDueDateTo());
		 // is empty 
		}
		if (dto.getDueDateFrom() != null && dto.getDueDateFrom()!=0) {
			filterQueryMap.put(" RR.DUE_DATE =", dto.getDueDateFrom() + "");
		//correct 
		}
		if (dto.getCreatedAtFrom() != null && dto.getCreatedAtFrom()!=null && dto.getCreatedAtFrom() !=0 && dto.getCreatedAtTo() != 0) {
			filterQueryMap.put(" RR.INVOICE_DATE BETWEEN ", dto.getCreatedAtFrom() + " AND "+ dto.getCreatedAtTo());
		//correct 
		}
		if (dto.getCreatedAtFrom() != null ) {
			filterQueryMap.put(" RR.INVOICE_DATE =",  dto.getCreatedAtFrom()+"");
		//correct 
		}
		if (dto.getInvoiceType() != null && !dto.getInvoiceType().isEmpty()) {
			
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getInvoiceType().size(); i++) {
				if (i < dto.getInvoiceType().size() - 1) {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getInvoiceType().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.INVOICE_TYPE IN", "'" +rqstId+ "'");
		//checked 
		}
		if (dto.getAssignedTo() != null && !dto.getAssignedTo().isEmpty()) {
			
			StringBuffer rqstId = new StringBuffer();
			for (int i = 0; i < dto.getAssignedTo().size(); i++) {
				if (i < dto.getAssignedTo().size() - 1) {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'" + ",");
				} else {
					rqstId.append("'" + dto.getAssignedTo().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.ASSIGNED_TO IN", "(" + rqstId + ")");
		// is EMpty 
		}
		if (dto.getValidationStatus() != null && !dto.getValidationStatus().isEmpty()) {
			StringBuffer status = new StringBuffer();
			for (int i = 0; i < dto.getValidationStatus().size(); i++) {
				if (i < dto.getValidationStatus().size() - 1) {
					status.append("'" + dto.getValidationStatus().get(i) + "'" + ",");
				} else {
					status.append("'" + dto.getValidationStatus().get(i) + "'");
				}
			}
			filterQueryMap.put(" RR.VALIDATION_STATUS IN", "(" + status + ")");//procss status
		}
		if (dto.getExtInvNum() != null && !dto.getExtInvNum().isEmpty()) {
			filterQueryMap.put(" RR.EXT_INV_NUM =", "'" + dto.getExtInvNum() + "'");
		}
		if (dto.getInvoiceValueFrom()!=0.0) {
			filterQueryMap.put(" RR.INVOICE_TOTAL =", dto.getInvoiceValueFrom() + "");
		}
		if (dto.getInvoiceValueFrom() !=0.0 && dto.getInvoiceValueTo()!=0.0) {
			filterQueryMap.put(" RR.INVOICE_TOTAL BETWEEN ", dto.getInvoiceValueFrom() + " AND "+ dto.getInvoiceValueTo());
		//correct 
		}
		int lastAppendingAndIndex = filterQueryMap.size() - 1;
		AtomicInteger count = new AtomicInteger(0);
		System.err.println("lastAppendingAndIndex " + lastAppendingAndIndex);

		filterQueryMap.forEach((k, v) -> {
			System.out.println("label : " + k + " value : " + v);
			query.append(k);
			query.append(v);
			if (filterQueryMap.size() > 1) {
				if (count.getAndIncrement() < filterQueryMap.size() - 1) {
					query.append(" AND ");
				}
			} else {
				query.append(" ORDER BY RR.CREATED_AT DESC");
				query.append(";");
			}
		});
		if (filterQueryMap.size() > 1) {
			query.append(" ORDER BY RR.CREATED_AT DESC");
			query.append(";");
		}
		System.err.println("Query : Check " + query.toString());
		invoiceOrderList = invoiceHeaderRepoFilter.getFilterDetails(query.toString());
		if (invoiceOrderList != null && !invoiceOrderList.isEmpty()) {
			return new ResponseEntity<List<InvoiceHeaderDo>>(invoiceOrderList, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("No Data Found For Searched Criteria", HttpStatus.NO_CONTENT);
		}
	}
	private Map<String, WorkflowTaskOutputDto> checkForClaim(
			List<WorkflowTaskOutputDto> invoiceRequestIdListPaginated) {
		 Map<String, WorkflowTaskOutputDto> map = new HashMap<String , WorkflowTaskOutputDto>();

		    for(WorkflowTaskOutputDto w : invoiceRequestIdListPaginated)
		    {
		        map.put(w.getSubject(), w);
		    }

		    return map;
	}



	// getInvoiceDetails based on requestId
	@Override
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
		    // get Attachements 
		        List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
		        List<AttachmentDto>  AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);    
		    // get Comments
		        List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId);
		        List<CommentDto>    commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);  
		        
		        invoiceHeadDto.setInvoiceHeaderDto(invoiceHeaderDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setInvoiceItems(invoiceItemDtoList);
		        invoiceHeadDto.getInvoiceHeaderDto().setAttachments(AttachementDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setComments(commentDto);
		        invoiceHeadDto.setInvoiceItemAcctAssignmentDto(invoiceItemAcctAssignmentdtoList);
		        invoiceHeadDto.setCostAllocationDto(costAllocationDto);
		        invoiceHeadDto.setResponseStatus("Success");
		    return new ResponseEntity<CreateInvoiceHeaderDto>(invoiceHeadDto,HttpStatus.OK);
		}catch(Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e,HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		        
	}
	
	@Override
	public ResponseEntity<?> getInvoiceDetailChanged(String requestId){
		try {
			CreateInvoiceHeaderChangeDto invoiceHeadDto =new  CreateInvoiceHeaderChangeDto();
			 // get InvoiceHeader
		  InvoiceHeaderDo invoiceHeaderDo = invoiceHeaderRepository.fetchInvoiceHeader(requestId);
		  InvResponseHeaderDto invoiceHeaderDto  =  ObjectMapperUtils.map(invoiceHeaderDo,InvResponseHeaderDto.class);   
		  // get InvoiceItem
		   List<InvoiceItemDo> invoiceItemDo = invoiceItemRepository.getInvoiceItemDos(requestId);
		   List<InvoiceItemsDto> invoiceItemDtoList = ObjectMapperUtils.mapAll(invoiceItemDo, InvoiceItemsDto.class);
		   for(int i =0;i<invoiceItemDtoList.size();i++){
			// get AccountAssignment
		     List<InvoiceItemAcctAssignmentDo>  invoiceItemAcctAssignmentdoList = invoiceItemAcctAssignmentRepository.get(requestId,invoiceItemDtoList.get(0).getItemId()); 
		     List<InvoiceItemAcctAssignmentDto>    invoiceItemAcctAssignmentdtoList = ObjectMapperUtils.mapAll(invoiceItemAcctAssignmentdoList, InvoiceItemAcctAssignmentDto.class);
		     invoiceItemDtoList.get(i).setInvoiceItemAccAssgn(invoiceItemAcctAssignmentdtoList);
		   
		     // get InvoiceItem Message
		     List<ItemMessageDto> itemMessage = new ArrayList<ItemMessageDto>();
		     invoiceItemDtoList.get(i).setItemMessages(itemMessage);
		   }
		   // get CostAllocation
		        List<CostAllocationDo> costAllocationDo = costAllocationRepository.getAllOnRequestId(requestId);
		        List<CostAllocationsDto>  costAllocationDto = ObjectMapperUtils.mapAll(costAllocationDo, CostAllocationsDto.class);                            
		    // get Attachements 
		        List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
		        List<AttachmentDto>  AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);    
		    // get Comments
		        List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId);
		        List<CommentDto>    commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);  
		    // get Message
		        List<HeaderMessageDto>  messageLists = new ArrayList<HeaderMessageDto>();
		        
		        invoiceHeadDto.setInvoiceHeaderDto(invoiceHeaderDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setInvoiceItems(invoiceItemDtoList);
		        invoiceHeadDto.getInvoiceHeaderDto().setCostAllocation(costAllocationDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setAttachment(AttachementDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setComment(commentDto);
		        invoiceHeadDto.getInvoiceHeaderDto().setHeaderMessages(messageLists);
		        invoiceHeadDto.setResponseStatus("Success");
		    return new ResponseEntity<CreateInvoiceHeaderChangeDto>(invoiceHeadDto,HttpStatus.OK);
		}catch(Exception e){
			
			return new ResponseEntity<String>("Failed due to "+e,HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		        
	}
	
	
	@Override
	public ResponseEntity<?> getInvoiceItemDetail(String requestId) {
		
		CreateInvoiceHeaderChangeDto invoiceItemAndHeader = new CreateInvoiceHeaderChangeDto();
		  InvoiceHeaderDo invoiceHeaderDo = invoiceHeaderRepository.fetchInvoiceHeader(requestId);
		  InvResponseHeaderDto invoiceHeaderDto  =  ObjectMapperUtils.map(invoiceHeaderDo,InvResponseHeaderDto.class);   
		  // get InvoiceItem
		   List<InvoiceItemDo> invoiceItemDo = invoiceItemRepository.getInvoiceItemDos(requestId);
		   List<InvoiceItemsDto> invoiceItemDtoList = ObjectMapperUtils.mapAll(invoiceItemDo, InvoiceItemsDto.class);
		   invoiceItemAndHeader.setInvoiceHeaderDto(invoiceHeaderDto);
		   invoiceItemAndHeader.getInvoiceHeaderDto().setInvoiceItems(invoiceItemDtoList);
		return new  ResponseEntity<>(invoiceItemAndHeader,HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getCostAllocationDetail(String requestId) {
		  List<CostAllocationDo> costAllocationDo = costAllocationRepository.getAllOnRequestId(requestId);
	        List<CostAllocationDto>  costAllocationDto = ObjectMapperUtils.mapAll(costAllocationDo, CostAllocationDto.class);                            
		return new  ResponseEntity<>(costAllocationDto,HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceAcctAssinment(String requestId) {
		  List<InvoiceItemAcctAssignmentDo>  invoiceItemAcctAssignmentdoList = invoiceItemAcctAssignmentRepository.getByRequestId(requestId); 
		     List<InvoiceItemAcctAssignmentDto>    invoiceItemAcctAssignmentdtoList = ObjectMapperUtils.mapAll(invoiceItemAcctAssignmentdoList, InvoiceItemAcctAssignmentDto.class);
		return new  ResponseEntity<>(invoiceItemAcctAssignmentdtoList,HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceAttachment(String requestId) {
		// get Attachements 
        List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
        List<AttachmentDto>  AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);    
		return new  ResponseEntity<>(AttachementDto,HttpStatus.OK);
	}
	@Override
	public ResponseEntity<?> getInvoiceComments(String requestId) {
		// get Comments
        List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId);
        List<CommentDto>    commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);  
		return new  ResponseEntity<>(commentDto,HttpStatus.OK);
	}
	
	
	@Override
	public ResponseEntity<?> getActivityLog(String requestId) {
		List<ActivityLogDto> activity =  new ArrayList<ActivityLogDto>();
		return new  ResponseEntity<>(activity,HttpStatus.OK);
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
	public ResponseDto delete(String id) {
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
			entity.setInvoiceTotal(new Double(dto.getInvoiceTotal()));
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
			//entity.setSapInvoiceNumber(dto.getSapInvoiceNumber());
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

		/*invoiceItem.setDisAmt(item.getDisAmt());

		invoiceItem.setDisPer(item.getDisPer());

		invoiceItem.setDeposit(item.getDeposit());*/

		invoiceItem.setExtItemId(item.getExtItemId());

		invoiceItem.setInvQty(item.getInvQty());

		/*invoiceItem.setItemComment(item.getItemComment());*/

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

		/*invoiceItem.setPrice(item.getPrice());

		invoiceItem.setQtyUom(item.getQtyUom());

		invoiceItem.setRefDocCat(item.getRefDocCat());

		invoiceItem.setRequestId(item.getRequestId());

		invoiceItem.setShippingPer(item.getShippingPer());

		invoiceItem.setUnit(item.getUnit());

		invoiceItem.setUpcCode(item.getUpcCode());

		invoiceItem.setUpdatedBy(item.getUpdatedBy());

		invoiceItem.setUpdatedAt(item.getUpdatedAt());

		invoiceItem.setCustomerItemId(item.getCustomerItemId());
*/
		invoiceItem.setIsSelected(item.getIsSelected());

		invoiceItem.setItemCode(item.getItemCode());

		invoiceItem.setMatchDocNum(item.getMatchDocNum());

		invoiceItem.setNetWorth(item.getNetWorth());

		invoiceItem.setPricingUnit(item.getPricingUnit());

		invoiceItem.setRefDocNum(item.getRefDocNum());

	/*	invoiceItem.setShippingAmt(item.getShippingAmt());

		invoiceItem.setTaxAmt(item.getTaxAmt());*/

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

		invoiceHeader.setInvoiceTotal(new Double(dto.getInvoiceTotal()));

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
	 List<ApproverDataOutputDto>  lists = null;	
	 try{
	RuleInputProcessLeadDto ruleInput = new RuleInputProcessLeadDto();
		ruleInput.setCompCode(determination.getCompCode());
		ruleInput.setProcessLeadCheck(determination.getProcessLeadCheck());
		ruleInput.setVendorId(determination.getVednorId());
		System.err.println("ruleInput "+ ruleInput.toRuleInputString(RuleConstants.menaBevRuleService) );
		ResponseEntity<?> response  = execute((RuleInputDto) ruleInput ,RuleConstants.menaBevRuleService);
	  System.err.println("1477 responseFromRulesTrigger"+response);
		// call convert statement and than get the ouput and return it .
		if(response.getStatusCodeValue() == 201){
		String node = (String)	response.getBody();
		     lists =  convertFromJSonNodeRo(node,"Not-Default");
		     
		     if(lists.isEmpty()||lists==null){
		     RuleInputProcessLeadDto Default = new RuleInputProcessLeadDto();
		     Default.setCompCode("Default");
		     Default.setProcessLeadCheck("Default");
		     Default.setVendorId("Default");
		     
		     ResponseEntity<?> responseDefault  = execute((RuleInputDto) Default ,RuleConstants.menaBevRuleService);
		     if(responseDefault.getStatusCodeValue() == 201){
		 		String nodeDefault = (String)	responseDefault.getBody();
		 		     lists =  convertFromJSonNodeRo(nodeDefault,"Default");
		 		     if(!lists.isEmpty() && lists!=null){
		 		    return new ResponseEntity<>(lists,HttpStatus.OK);
		 		     }else {
		 		    	 return new ResponseEntity<>("Defualt Reccord is missing in Rule file",HttpStatus.CONFLICT);	 
		 		     }
		 		     }else{
		 		    	return new ResponseEntity<>(response,HttpStatus.CONFLICT);	 
		 		     }
		     } else{
		    		return new ResponseEntity<>(lists,HttpStatus.OK); 
		     }
		}else {
			return new ResponseEntity<>(response,HttpStatus.CONFLICT);
		}
	 }catch(Exception e){
		 return new ResponseEntity<>(e.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
	 }
	}


public List<ApproverDataOutputDto> convertFromJSonNodeRo(String node,String checkDefault){
	List<ApproverDataOutputDto> approverList = new ArrayList<>();
	JSONObject jObj = new JSONObject(node);
	JSONArray arr = jObj.getJSONArray("Result");
	JSONObject innerObject = null;
	if(!arr.isNull(0))
		innerObject = arr.getJSONObject(0).getJSONObject("AcctOrProcessLeadDeterminationResult");
	System.err.println("innerObj "+innerObject);
	if(!innerObject.isEmpty()&&innerObject!=null) {
	//for (int i = 0; i < innerArray.length(); i++) {
		ApproverDataOutputDto approverDto = new ApproverDataOutputDto();
		approverDto.setUserOrGroup(innerObject.get("UserOrGroup").toString());
		approverDto.setUserType(checkDefault);
		approverList.add(approverDto);

	//}
		System.err.println("list of approveDto "+approverList);
	return approverList;
	}
	System.err.println("list of approveDto "+approverList);
return approverList;
}
	
	
	protected ResponseEntity<?> execute(RuleInputDto input, String rulesServiceId) throws ClientProtocolException, IOException, URISyntaxException {

		
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, new BasicCookieStore());
		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = null;
		httpClient = getHTTPClient();
		System.err.println("1561 RuleInput "+input);
		// need to set still the ruleServiceId and than , process the output.
		
		String jwToken = getJwtTokenForAuthenticationForRulesSapApi();
		System.err.println("map for rulesToken : " +jwToken);
		httpPost = new HttpPost(RuleConstants.RULE_BASE_URL);
		httpPost.addHeader(CONTENT_TYPE, "application/json");

		httpPost.addHeader(AUTHORIZATION, "Bearer " +jwToken); // header

		String ruleInputString = input.toRuleInputString(rulesServiceId);
		StringEntity stringEntity = new StringEntity(ruleInputString);

		System.err.println("stringEntity "+ ruleInputString);
		httpPost.setEntity(stringEntity);

		response = httpClient.execute(httpPost);
		System.err.println("response : " +response);

		// process your response here
		
		System.err.println("RuleTriggerResponse ="+response);
		if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
			String dataFromStream = getDataFromStream(response.getEntity().getContent());
			
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
			return new ResponseEntity<>(dataFromStream, HttpStatus.CREATED);
		} else {
			
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (response != null) {
				response.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
			return new ResponseEntity<>(getDataFromStream(response.getEntity().getContent()),
					HttpStatus.BAD_REQUEST);
		}

		// clean-up sessions
		
		
		
		
	}
	
	public static String getJwtTokenForAuthenticationForRulesSapApi() throws URISyntaxException, IOException {
		System.err.println("77 destination");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(RuleConstants.OAUTH_TOKEN_URL);
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

			System.err.println("1125 "+token);
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
				if (response.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
					String dataFromStream = getDataFromStream(response.getEntity().getContent());
					System.err.println("dataStream ="+dataFromStream);
					JSONObject jsonObject = new JSONObject(dataFromStream);
					System.err.println(" data stram JsonObject "+jsonObject);
					WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
							WorkflowTaskOutputDto.class);
					return new ResponseEntity(taskDto, HttpStatus.CREATED);
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

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity<?> claimTaskOfUser(String taskId,String userId,boolean isClaim) {

		try {
			String token = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();

			HttpClient client = HttpClientBuilder.create().build();

			/*Map<String, Object> map = DestinationReaderUtil
					.getDestination(DkshConstants.WORKFLOW_CLOSE_TASK_DESTINATION);*/
			
			String payload =null;
			String url = MenabevApplicationConstant.WORKFLOW_REST_BASE_URL + "/v1/task-instances/" + taskId;
			if(isClaim){
			 payload = "{\"processor\":\""+userId+"\"}";
			}else{
				payload = "{\"processor\":\""+""+"\"}";
				 System.err.println("payload "+payload);
			}

			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.addHeader("Authorization", "Bearer " + token);
			httpPatch.addHeader("Content-Type", "application/json");

			try {
				StringEntity entity = new StringEntity(payload);
				System.err.println("inputEntity "+entity );
				entity.setContentType("application/json");
				httpPatch.setEntity(entity);
				HttpResponse response = client.execute(httpPatch);

				
				if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
					if(isClaim){
					return new ResponseEntity<String>("Task has been claimed by user "+userId, HttpStatus.CREATED);
					}else {
						return new ResponseEntity<String>("Task has been released by user "+userId, HttpStatus.CREATED);
						} 
				}else {
					
					return new ResponseEntity<String>("Failed due "+getDataFromStream(response.getEntity().getContent()),
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



	@Override
	public ResponseDto delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}



	
}
