package com.ap.menabev.serviceimpl;

import java.util.ArrayList
;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ap.menabev.dto.ConfigurationDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.ConfigurationDo;
import com.ap.menabev.invoice.ConfigurationRepository;
import com.ap.menabev.service.ConfigurationService;
import com.ap.menabev.util.ApplicationConstants;

public class ConfigurationServiceImpl  implements ConfigurationService
{
	@Autowired
	ConfigurationRepository configurationRepository;
	
     public ResponseDto save(ConfigurationDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			
			ModelMapper mapper = new ModelMapper();
			configurationRepository.save(mapper.map(dto, ConfigurationDo.class));
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
	


	public List<ConfigurationDto> get() {
		List<ConfigurationDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<ConfigurationDo> doList =configurationRepository.findAll();
			for (ConfigurationDo configurationDo : doList) {
				list.add(modelMapper.map(configurationDo, ConfigurationDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			configurationRepository.deleteById(id);
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



	@Override
	public ResponseDto delete(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}


}
