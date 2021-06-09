package com.ap.menabev.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ap.menabev.dto.ActivityLogDto;
import com.ap.menabev.entity.ActivityLogDo;
import com.ap.menabev.invoice.ActivityLogRepository;
import com.ap.menabev.service.ActivityLogService;
import com.ap.menabev.util.ObjectMapperUtils;

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
	
	
	

}
