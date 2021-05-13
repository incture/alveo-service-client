package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.dto.SchedulerConfigurationDto;
import com.ap.menabev.entity.SchedulerConfigurationDo;
import com.ap.menabev.invoice.SchedulerConfigurationRepository;
import com.ap.menabev.service.SchedulerConfigurationService;
import com.ap.menabev.util.ApplicationConstants;


public class SchedulerConfigurationServiceImpl implements SchedulerConfigurationService
{
	@Autowired
	SchedulerConfigurationRepository schedulerconfigurationRepository;
	
     public ResponseDto save(SchedulerConfigurationDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			
			ModelMapper mapper = new ModelMapper();
			schedulerconfigurationRepository.save(mapper.map(dto, SchedulerConfigurationDo.class));
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.CREATED_SUCCESS);
			return response;
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.CREATE_FAILURE);
			return response;
		}

	}
	


	public List<SchedulerConfigurationDto> get() {
		List<SchedulerConfigurationDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<SchedulerConfigurationDo> doList =schedulerconfigurationRepository.findAll();
			for (SchedulerConfigurationDo schedulerconfigurationDo : doList) {
				list.add(modelMapper.map(schedulerconfigurationDo, SchedulerConfigurationDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			schedulerconfigurationRepository.deleteById(id);
			response.setCode(ApplicationConstants.CODE_SUCCESS);
			response.setStatus(ApplicationConstants.SUCCESS);
			response.setMessage(ApplicationConstants.DELETE_SUCCESS);
		} catch (Exception e) {
			response.setCode(ApplicationConstants.CODE_FAILURE);
			response.setStatus(ApplicationConstants.FAILURE);
			response.setMessage(ApplicationConstants.DELETE_FAILURE);
		}
		return response;
	}



}
