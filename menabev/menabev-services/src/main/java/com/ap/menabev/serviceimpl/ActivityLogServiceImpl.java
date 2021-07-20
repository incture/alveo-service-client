package com.ap.menabev.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.AttachmentDto;
import com.ap.menabev.dto.CommentDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceSubmitDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.WorkflowTaskOutputDto;
import com.ap.menabev.entity.ActivityLogDo;
import com.ap.menabev.entity.AttachmentDo;
import com.ap.menabev.entity.CommentDo;
import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.invoice.ActivityLogRepository;
import com.ap.menabev.invoice.AttachmentRepository;
import com.ap.menabev.invoice.CommentRepository;
import com.ap.menabev.invoice.InvoiceHeaderRepository;
import com.ap.menabev.service.ActivityLogService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.DestinationReaderUtil;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.ServiceUtil;
import com.google.gson.Gson;

@Service
public class ActivityLogServiceImpl implements ActivityLogService{
	
	
	@Autowired
	private ActivityLogRepository repo ;
	
	@Autowired
	private InvoiceHeaderRepository invocieRepo;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	AttachmentRepository attachmentRepository;

	@Override
	public List<ActivityLogDto>  getLogs(String requestId) {

	List<ActivityLogDo>	 listOfActivityLog = repo.getAllActivityForRequestId(requestId);
	List<ActivityLogDto>	 listOfActivityLogDto = ObjectMapperUtils.mapAll(listOfActivityLog, ActivityLogDto.class);
	return listOfActivityLogDto;
		
	}

	@Override
	public ResponseEntity<?> save(ActivityLogDto dto) {
	ActivityLogDo activity =	ObjectMapperUtils.map(dto, ActivityLogDo.class);
	        activity   = repo.save(activity);
	       if(activity != null){
	        return new ResponseEntity<>(activity,HttpStatus.OK);}else {
	        	return new ResponseEntity<>("Failed ",HttpStatus.OK);
	        }
	}
	
	@Override
	public List<ActivityLogDto> createActivityLogForSubmit(InvoiceHeaderDto invoiceHeaderDto,String actionCode,String actionCodeText){
		     List<ActivityLogDto>   activitLogs     = invoiceHeaderDto.getActivityLog();
		if(!activitLogs.isEmpty()){
			//update activity log of the previous values
			activitLogs.stream().forEach(activity->{
				String uuid = repo.getUUID(invoiceHeaderDto.getRequestId(), invoiceHeaderDto.getTaskOwner());
				if(!ServiceUtil.isEmpty(uuid)){
					activity.setGuid(uuid);
				}
				activity.setActionCode(actionCode);
				activity.setActionCodeText(actionCodeText);
				if(ServiceUtil.isEmpty(activity.getCompletedAt())){
				activity.setCompletedAt(ServiceUtil.getEpocTime());
				}
				activity.setCreatedBy(invoiceHeaderDto.getTaskOwner());
				activity.setInvoiceStatusCode(invoiceHeaderDto.getInvoiceStatus());
				activity.setInvoiceStatusText(invoiceHeaderDto.getInvoiceStatusText());
				activity.setProcessor(invoiceHeaderDto.getProcessor());
				activity.setRequestId(invoiceHeaderDto.getRequestId());
				activity.setGuid(activity.getGuid());
				activity.setTaskCreatedAt(invoiceHeaderDto.getRequest_created_at());
				activity.setTaskId(invoiceHeaderDto.getTaskId());
				activity.setTaskOwner(invoiceHeaderDto.getTaskOwner());
				activity.setTaskStatus("COMPLETED");
				activity.setWorkflowCreateBy(invoiceHeaderDto.getRequest_created_by());
				activity.setWorkflowCreatedAt(invoiceHeaderDto.getRequest_created_at());
				activity.setWorkflowId(invoiceHeaderDto.getWorkflowId());
				activity.setWorkflowStatus("IN-PROGRESS");
			});	
		}
		ActivityLogDto activity = createActivityLogForPoOrNonPo(invoiceHeaderDto);
		activitLogs.add(activity);
	                 List<ActivityLogDo>  activitLogDos  =ObjectMapperUtils.mapAll(activitLogs, ActivityLogDo.class);
		repo.saveAll(activitLogDos);
	return activitLogs;
	}
	
	// create activity for po and nonPo 
	@Override
	public ActivityLogDto createActivityLogForPoOrNonPo(InvoiceHeaderDto invoiceHeader){
		ActivityLogDto activity = new ActivityLogDto();
		activity.setGuid(UUID.randomUUID().toString());
		activity.setActionCode("C");// set the action code
		activity.setActionCodeText("C");
		activity.setCreatedAt(ServiceUtil.getEpocTime());
		activity.setCreatedBy(invoiceHeader.getTaskOwner());
		activity.setInvoiceStatusCode(invoiceHeader.getInvoiceStatus());
		activity.setInvoiceStatusText(invoiceHeader.getInvoiceStatusText());
		activity.setProcessor(invoiceHeader.getProcessor());
		activity.setRequestId(invoiceHeader.getRequestId());
		activity.setTaskCreatedAt(invoiceHeader.getRequest_created_at());
		activity.setTaskId(invoiceHeader.getTaskId());
		activity.setTaskOwner(invoiceHeader.getTaskOwner());
		activity.setTaskStatus("READY");
		activity.setWorkflowCreateBy(invoiceHeader.getRequest_created_by());
		activity.setWorkflowCreatedAt(invoiceHeader.getRequest_created_at());
		activity.setWorkflowId(invoiceHeader.getWorkflowId());
		activity.setWorkflowStatus("IN-PROGRESS");
	    return activity;
	}

	// use for both completi
	public ActivityLogDto saveOrUpdateActivityLog(InvoiceSubmitDto invoiceSubmit,String actionCode,String actionCodeText){
		ResponseDto response = new ResponseDto();
		ActivityLogDto activity = new ActivityLogDto();
		try {
			String uuid = repo.getUUID(invoiceSubmit.getInvoice().getRequestId(), invoiceSubmit.getInvoice().getTaskOwner());
			
			if(!ServiceUtil.isEmpty(uuid)){
				activity.setGuid(uuid);
				activity.setTaskStatus("COMPLETED");
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.UPDATE_SUCCESS);
				response.setMessage(invoiceSubmit.getInvoice().getRequestId() + " "+ ApplicationConstants.UPDATE_SUCCESS);
			}else{
				activity.setTaskStatus("READY");
				response.setCode(ApplicationConstants.CODE_SUCCESS);
				response.setStatus(ApplicationConstants.CREATED_SUCCESS);
				response.setMessage(invoiceSubmit.getInvoice().getRequestId() + " " + ApplicationConstants.CREATED_SUCCESS);
			}
			activity.setActionCode(actionCode);
			activity.setActionCodeText(actionCodeText);
			if(ServiceUtil.isEmpty(activity.getCompletedAt())){
			activity.setCompletedAt(ServiceUtil.getEpocTime());
			}
			activity.setCreatedBy(invoiceSubmit.getInvoice().getTaskOwner());
			activity.setInvoiceStatusCode(invoiceSubmit.getInvoice().getInvoiceStatus());
			activity.setInvoiceStatusText(invoiceSubmit.getInvoice().getInvoiceStatusText());
			activity.setProcessor(invoiceSubmit.getInvoice().getProcessor());
			activity.setRequestId(invoiceSubmit.getInvoice().getRequestId());
			activity.setTaskCreatedAt(invoiceSubmit.getInvoice().getRequest_created_at());
			activity.setTaskId(invoiceSubmit.getInvoice().getTaskId());
			activity.setTaskOwner(invoiceSubmit.getInvoice().getTaskOwner());
			activity.setWorkflowCreateBy(invoiceSubmit.getInvoice().getRequest_created_by());
			activity.setWorkflowCreatedAt(invoiceSubmit.getInvoice().getRequest_created_at());
			activity.setWorkflowId(invoiceSubmit.getInvoice().getWorkflowId());
			activity.setWorkflowStatus("IN-PROGRESS");
			//ActivityLogDo activityDo = ObjectMapperUtils.map(activity, ActivityLogDo.class);
			response.setObject(activity);
			//repo.save(activityDo);
			return activity;
		} catch (Exception e) {
		       System.err.println("Exception activityLog "+e);
			return activity;
		}
	
	}
	
	@Override
	public List<ActivityLogDto> getByRequestId(InvoiceHeaderDto dto){
		List<ActivityLogDto> activityDto = new ArrayList<>();
		List<ActivityLogDo> activityDo = new ArrayList<>();
		if(!ServiceUtil.isEmpty(dto.getRequestId())){
			activityDo = repo.getAllActivityForRequestId(dto.getRequestId());
			for(ActivityLogDo oneActivityDo : activityDo){
				ActivityLogDto oneActivityDto = new ActivityLogDto();
				oneActivityDto = ObjectMapperUtils.map(oneActivityDo, ActivityLogDto.class);
				activityDto.add(oneActivityDto);
			}
		}
		
		
		return activityDto;
		
	}
	
	@Override
	// get activityLog from the workflow api using requestId and WorkfowId
	public ActivityLogResponseDto getWorkflowActivityLog(String requestId){
		ActivityLogResponseDto activity = new ActivityLogResponseDto();   
		try{
		// call invoice header Table get workflowId  and comments and attachement details
		   InvoiceHeaderDo invoice = invocieRepo.getInvoiceHeader(requestId);
             // get attachment 	 
		    	List<AttachmentDto> AttachementDto = getInvoiceAttachment(requestId);

		    	InvoiceHeaderDto invoiceDto = ObjectMapperUtils.map(invoice, InvoiceHeaderDto.class);
	               if(ServiceUtil.isEmpty(invoiceDto)){
		   InvoiceReceivedDto received = new InvoiceReceivedDto();
		   received.setInvocieChannelType(invoiceDto.getChannelType());
		   received.setInvocieReceived(invoiceDto.getRequest_created_at());
		   activity.setInvoiceReceived(received);
		// call worklfow api to get the acivity log from worklfow 
		   ResponseEntity<?>  workflowResponse = getWorkflowActivityTaskDetails(invoiceDto.getWorkflowId());
		  if( workflowResponse.getStatusCodeValue()==200){
			   @SuppressWarnings("unchecked")
			List<WorkflowTaskOutputDto> listOfWorkflwoTask = (List<WorkflowTaskOutputDto>) workflowResponse.getBody();
			   listOfWorkflwoTask.stream().forEach(w->{
                    List<String> workflwoUser = w.getRecipientUsers();		
                         // get the comment for each activityId
                  List<CommentDto> commentList =  getInvoiceComments(requestId,workflwoUser);
                    w.setComment(commentList);
			   });         
			   activity.setApproval(listOfWorkflwoTask);
			   activity.setStatusCode("200");
			   activity.setMessage("SUCCESS");
			   activity.setAttachment(AttachementDto);
			   return activity;
		  }else {
			  activity.setApproval(null);
			   activity.setStatusCode("400");
			   activity.setMessage(workflowResponse.toString());
			  return activity;
		  } 
	    }
		}catch (Exception e){
	    	System.err.println("ActityLog Exception "+ e);
	    	activity.setApproval(null);
			   activity.setStatusCode("500");
			   activity.setMessage("Failed due to "+e.getMessage());
	    }
		// form the payload respectively
		  activity.setApproval(null);
		   activity.setStatusCode("404");
		   activity.setMessage("Activity log for Request Id ="+requestId+" not found ");
		  return activity;   
	}
	
	
	public static ResponseEntity<?> getWorkflowActivityTaskDetails(String workflowInstanceId) {

		try {
			List<WorkflowTaskOutputDto> listOfWorkflowTasks = new ArrayList<WorkflowTaskOutputDto>();
			HttpClient client = HttpClientBuilder.create().build();
			String jwToken = DestinationReaderUtil.getJwtTokenForAuthenticationForSapApi();
			String url = ApplicationConstants.WORKFLOW_REST_BASE_URL + "/v1/task-instances?workflowInstanceId="+workflowInstanceId;
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "Bearer " + jwToken);
			httpGet.addHeader("Content-Type", "application/json");
				HttpResponse response = client.execute(httpGet);
				String dataFromStream = getDataFromStream(response.getEntity().getContent());
				if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
					JSONArray jsonArray = new JSONArray(dataFromStream);
					for (int i = 0; i < jsonArray.length(); i++) {
						Object jsonObject = jsonArray.get(i);
						WorkflowTaskOutputDto taskDto = new Gson().fromJson(jsonObject.toString(),
								WorkflowTaskOutputDto.class);
						taskDto.setRequestIds(taskDto.getSubject());
						listOfWorkflowTasks.add(taskDto);
					}
					if (!listOfWorkflowTasks.isEmpty()) {
						return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
					} else {
						return new ResponseEntity<>(listOfWorkflowTasks, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<String>("FETCHING FAILED", HttpStatus.CONFLICT);
				}
		} catch (Exception e) {
			return new ResponseEntity<>("EXCEPTION_GET_MSG" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
	
	public List<CommentDto> getInvoiceComments(String requestId,List<String> emailId) {
		// get Comments
		List<CommentDo> commentDo = commentRepository.getCommentsByRequestIdAndUser(requestId,emailId);
		List<CommentDto> commentDto = ObjectMapperUtils.mapAll(commentDo, CommentDto.class);
		return commentDto;
	}

	public List<AttachmentDto> getInvoiceAttachment(String requestId) {
		// get Attachements
		List<AttachmentDo> attachementDo = attachmentRepository.getAllAttachmentsForRequestId(requestId);
		List<AttachmentDto> AttachementDto = ObjectMapperUtils.mapAll(attachementDo, AttachmentDto.class);
	   return AttachementDto;
	}



}
	


