package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.dto.InvoiceHeaderDto;
import com.ap.menabev.dto.InvoiceSubmitDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.ActivityLogDo;
import com.ap.menabev.invoice.ActivityLogRepository;
import com.ap.menabev.service.ActivityLogService;
import com.ap.menabev.util.ApplicationConstants;
import com.ap.menabev.util.ObjectMapperUtils;
import com.ap.menabev.util.ServiceUtil;

@Service
public class ActivityLogServiceImpl implements ActivityLogService{
	
	
	@Autowired
	private ActivityLogRepository repo ;

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
	
	public ResponseDto saveOrUpdateActivityLog(InvoiceSubmitDto invoiceSubmit,String actionCode,String actionCodeText){
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
			ActivityLogDo activityDo = ObjectMapperUtils.map(activity, ActivityLogDo.class);
			response.setObject(activity);
			repo.save(activityDo);
			
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setMessage("Creation or Update Failed due to "+ e.getMessage());
			response.setStatus(ApplicationConstants.CREATE_FAILURE);
			response.setObject(activity);
		}
		return response;
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
	

	
	
	

}
