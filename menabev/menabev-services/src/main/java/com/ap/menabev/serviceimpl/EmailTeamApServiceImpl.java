package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.menabev.dto.EmailTeamAPDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.EmailTeamAPDo;
import com.ap.menabev.invoice.EmailTeamApRepository;
import com.ap.menabev.service.EmailTeamApService;
import com.ap.menabev.util.ApplicationConstants;

@Service
public class EmailTeamApServiceImpl implements EmailTeamApService
{
	@Autowired
	EmailTeamApRepository emailteamRepository;
	
     public ResponseDto save(EmailTeamAPDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			
			System.out.println("Hi"+dto);
			
			ModelMapper mapper = new ModelMapper();
			emailteamRepository.save(mapper.map(dto, EmailTeamAPDo.class));
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
	


	public List<EmailTeamAPDto> get() {
		List<EmailTeamAPDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<EmailTeamAPDo> doList = emailteamRepository.findAll();
			for (EmailTeamAPDo emailTeamApDo : doList) {
				list.add(modelMapper.map(emailTeamApDo, EmailTeamAPDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			emailteamRepository.deleteById(id);
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
