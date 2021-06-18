package com.ap.menabev.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ap.menabev.service.TestService;

@Service
public class TestServiceImpl implements TestService {

	@Override
	public String test() {
		return "Hello World";
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
	/*@Autowired
	InvoiceHeaderRepository invoiceHeaderRepository;
	
	@Autowired
	InvoiceHeaderRepoFilter invoiceHeaderRepoFilter;
	*/

	@Override
	public String getCurrent() {
		// TODO Auto-generated method stub
		return null;
	}
	

	/*
	@Async
	@Override
	public CompletableFuture<ResponseEntity<?>> testAsyncForTaskApiCount(String  dto) throws InterruptedException{
		Thread.sleep(TimeUnit.SECONDS.toMillis(10));	
		System.err.println("get Count task detail of the user ----1 "+ new Date());
			
	          ResponseEntity<String> response  = new ResponseEntity<String>("Count Service ",	 HttpStatus.OK);
			return CompletableFuture.completedFuture(response);
	}
	
	@Async
	@Override
	public CompletableFuture<ResponseEntity<?>> testAsyncForTaskApiList(String  dto) throws InterruptedException{
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));	
		System.err.println("get  taskList of the user---2 "+new Date());
	          ResponseEntity<String> response  = new ResponseEntity<String>("List Service ",	 HttpStatus.OK);
			return CompletableFuture.completedFuture(response);
			
		}

	@Override
	public CompletableFuture<ResponseEntity<?>> getInboxTaskWithMultipleSearch(
			FilterMultipleHeaderSearchDto filterDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<ResponseEntity<?>> getInboxTaskCounnt(FilterMultipleHeaderSearchDto filterDto) {
		// TODO Auto-generated method stub
		return null;
	}*/

/*	
	

	@Async
	@Override
	public CompletableFuture<ResponseEntity<?>>  getInboxTaskWithMultipleSearch(FilterMultipleHeaderSearchDto filterDto){
		try {
			if (filterDto.getIndexNum() != null && filterDto.getPageCount() != null && filterDto.getIndexNum() != 0 && filterDto.getPageCount() != 0) {
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
								if(filterDto.getRoleOfUser().equals("Accountant")&&filterDto.getTab().equals("DRAFT")){
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
							
								if(!listOfWorkflowTasks.isEmpty()&& listOfWorkflowTasks!=null){
							
									System.err.println("list of workflowIDs  for accountant = " + listOfWorkflowTasks );
									
									ResponseEntity<?> response = new ResponseEntity<>(fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(
											listOfWorkflowTasks,filterDto,listOfWorkflowTasks.size()),
											HttpStatus.OK);
									return CompletableFuture.completedFuture(response);
									
								} else {
									ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available.",HttpStatus.OK);
									return CompletableFuture.completedFuture(response);
								}
							}else {
								ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available.",HttpStatus.OK);
								return CompletableFuture.completedFuture(response);
							}
							
						} else {
							return CompletableFuture.completedFuture(responseFromSapApi);
						}
				} else {
					
					ResponseEntity<?> response = new ResponseEntity<>(
							"INVALID_INPUT" + "Please provide login in user id ",
							HttpStatus.BAD_REQUEST);
					return CompletableFuture.completedFuture(response);
				}
			} else {
				ResponseEntity<?> response = new ResponseEntity<>("INVALID_INPUT" + " Provide valid index num and count.",
						HttpStatus.BAD_REQUEST);
				return CompletableFuture.completedFuture(response);
			}
		} catch (Exception e) {
			ResponseEntity<?> response = new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
			 return CompletableFuture.completedFuture(response);
		}
		
	}

	
	@Async
	@Override
	public  CompletableFuture<ResponseEntity<?>>  getInboxTaskCounnt(FilterMultipleHeaderSearchDto filterDto){
		try {
			if (filterDto.getIndexNum() != null && filterDto.getPageCount() != null && filterDto.getIndexNum() != 0 && filterDto.getPageCount() != 0) {
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
								if(filterDto.getRoleOfUser().equals("Accountant")&&filterDto.getTab().equals("DRAFT")){
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
											workflow.setStatus(lists.get(i).getValidationStatus());
											listOfWorkflowTasks.add(workflow);
										}
									}
								}
								
								if(listOfWorkflowTasks!=null && !listOfWorkflowTasks.isEmpty()){
									
									System.err.println("count ListOfWrokflowTasks -"+listOfWorkflowTasks);
									//  form a ouput Payload 
								InboxCountReponseDto countResponse  = new InboxCountReponseDto();
                                    // get a count of each tasks								
								long countOfOpen = 	listOfWorkflowTasks.parallelStream().filter(w->w.getStatus().equals("Ready")).count();
								long countOfMytask = listOfWorkflowTasks.parallelStream().filter(w->w.getStatus().equals("Reserved")).count();
								long countOfDraft = listOfWorkflowTasks.parallelStream().filter(w->w.getStatus().equals("Draft")).count();
                                    // set the each count 								
								countResponse.setIndex(filterDto.getIndexNum());
								countResponse.setPageCount(filterDto.getPageCount());
								countResponse.setCountOfOpenTask(countOfOpen);
								countResponse.setCountOfMyTask(countOfMytask);
								countResponse.setCountOfDraft(countOfDraft);
								
								
							ResponseEntity<?> response =  new ResponseEntity<InboxCountReponseDto>(countResponse,HttpStatus.OK);
							return CompletableFuture.completedFuture(response);
							
								}else {
									
									ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available."
													,HttpStatus.OK);
									return CompletableFuture.completedFuture(response);
								}
							} else {
								ResponseEntity<?> response = new ResponseEntity<String>("No tasks are available."
									, HttpStatus.OK);
								return CompletableFuture.completedFuture(response);
							}
						} else {
							 
							 return CompletableFuture.completedFuture(responseFromSapApi);
						}
				} else {
					ResponseEntity<?> response = 	 new ResponseEntity<>(
							"INVALID_INPUT" + "Please provide login in user id ",
							HttpStatus.BAD_REQUEST);
					return CompletableFuture.completedFuture(response);
				}
			} else {
				ResponseEntity<?> response =  new ResponseEntity<>("INVALID_INPUT" + " Provide valid index num and count.",
						HttpStatus.BAD_REQUEST);
				return CompletableFuture.completedFuture(response);
			}
			
		} catch (Exception e) {
			ResponseEntity<?> response =  new ResponseEntity<>("EXCEPTION_POST_MSG" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
			return CompletableFuture.completedFuture(response);
		}
	
		
	}
	
	
	
	
	public ResponseEntity<?> fetchWorkflowUserTaksList(FilterMultipleHeaderSearchDto filter){
		try {
			if (!checkString(filter.getUserId())) {
				List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<>();
				
				String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
				
				HttpClient client = HttpClientBuilder.create().build();
				
				StringBuilder url = new StringBuilder();
				
				
				appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
						WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
				//if(filter.getMyTask().equals("MYTASK")){
				    appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
						 WorkflowConstants.STATUS_OF_APPROVAL_TASKS_VALUE);
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_RESERVED_VALUE);
					appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, filter.getUserId());
					appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, filter.getUserId());
				//}/*else{
					appendParamInUrl(url, WorkflowConstants.STATUS_OF_APPROVAL_TASKS_KEY,
							WorkflowConstants.STATUS_OF_APPROVAL_TASKS_VALUE);
				appendParamInUrl(url, WorkflowConstants.RECIPIENT_USER_KEY, filter.getUserId());
				appendParamInUrl(url, WorkflowConstants.PROCESSOR_KEY, "");
				//
				//}
				//if(filter.getRequestId()!=null && !filter.getRequestId().isEmpty() ){
				//	appendParamInUrl(url, WorkflowConstants.SUBJECT,filter.getRequestId() );
					//}	
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
				                        System.err.println("getAllHeaders "+ response + "responseHeaders"+response.getAllHeaders() );
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
				if(!filter.getTab().equals("DRAFT")){
				String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
				
				HttpClient client = HttpClientBuilder.create().build();
				
				StringBuilder url = new StringBuilder();
				
				
				appendParamInUrl(url, WorkflowConstants.WORKFLOW_DEFINATION_ID_KEY,
						WorkflowConstants.WORKFLOW_DEFINATION_ID_VALUE);
				if(filter.getTab().equals("MYTASK")){
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
        List<InvoiceHeaderDo> listOfInvoiceOrders = invoiceHeaderRepository
				.findByRequestIdIn(requestId);
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
		//DecimalFormat df = new DecimalFormat("##.00");
		//inbox.setInvoiceTotal(df.format(invoiceHeaderList.get(i).getInvoiceTotal()));
		double  invoiceTotal =  invoiceHeaderList.get(i).getInvoiceTotal();
		System.err.println("eror double "+String.format("%.2f",invoiceTotal));
		inbox.setInvoiceTotal(String.format("%.2f",invoiceTotal));
	    inboxOutputList.add(inbox);
		 }
		 InboxResponseOutputDto response = new InboxResponseOutputDto();
	      
		 response.setPageCount(inboxOutputList.size());
		 response.setListOfTasks(inboxOutputList);
		return  new ResponseEntity<InboxResponseOutputDto>(response,HttpStatus.OK);
		
          } else {
        	  return new ResponseEntity<String>("No Data Found For the Filter Query ",HttpStatus.NO_CONTENT); 
          }
	}

	
	@SuppressWarnings({ "unchecked", "unused" })
	private ResponseEntity<?> fetchInvoiceDocHeaderDtoListFromRequestNumberMultiple(
			List<WorkflowTaskOutputDto> invoiceRequestIdListPaginated,FilterMultipleHeaderSearchDto filterDto,int totalCount) {
		
		List<InvoiceHeaderDo> listOfInvoiceOrders = null;
		ResponseEntity<?> responseFromFilter = null;
		// workflowResponse with list of requestIds
        List<String> requestIds =  invoiceRequestIdListPaginated.stream().map(WorkflowTaskOutputDto::getSubject).collect(Collectors.toList());
        System.err.println("requestId "+requestIds);
        List<InvoiceHeaderDo> listOfInvoiceOrders = invoiceHeaderRepository
				.findByRequestIdIn(requestId);
        // List of requestIds fetched for user from user task  
           responseFromFilter = filterInvoicesMultiple(requestIds,filterDto);
          if(responseFromFilter.getStatusCodeValue()==200){
        	 listOfInvoiceOrders=  (List<InvoiceHeaderDo>) responseFromFilter.getBody();
		System.err.println("lisOFInvoice by requestIds "+listOfInvoiceOrders);
		
		long min = (filterDto.getIndexNum()  * filterDto.getPageCount()) - filterDto.getPageCount();
		long max = (filterDto.getIndexNum()  * filterDto.getPageCount());
		if(listOfInvoiceOrders!=null && !listOfInvoiceOrders.isEmpty()){
		if (min < listOfInvoiceOrders.size()) {
			if (max > listOfInvoiceOrders.size()) {
				max = listOfInvoiceOrders.size();
			}
		
		
		List<InvoiceHeaderDo> paginatedInvoiceList = listOfInvoiceOrders.stream().skip(min).limit(max)
		.collect(Collectors.toList());
		// formMapfor claim
		Map<String,WorkflowTaskOutputDto> map = checkForClaim(invoiceRequestIdListPaginated);
		// filter of the invoice. create a method at night for filter of invoice
		List<InvoiceHeaderDto> invoiceHeaderList  = ObjectMapperUtils.mapAll(paginatedInvoiceList, InvoiceHeaderDto.class);
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
		inbox.setInvoiceTotal(String.format("%.2f",invoiceHeaderList.get(i).getInvoiceTotal()));
	    inboxOutputList.add(inbox);
		 }
		 InboxResponseOutputDto response = new InboxResponseOutputDto();
		// if(inboxOutputList.size() <= totalCount ){
		 //response.setPageCount(inboxOutputList.size());
		 //}else{
			 response.setPageCount(listOfInvoiceOrders.size()); 
		 //}
		 response.setListOfTasks(inboxOutputList);
		return  new ResponseEntity<InboxResponseOutputDto>(response,HttpStatus.OK);
		
		     }
		     else{
			return new ResponseEntity<String>("No Data Found for index and Count ",HttpStatus.NO_CONTENT); 
			}
	    }else{
			return new ResponseEntity<String>("No Data Found For the Filter Query ",HttpStatus.NO_CONTENT);
		}
	}else{
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
		
		
*/

}
	


