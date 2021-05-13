package com.ap.menabev.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ap.menabev.dto.MailTemplateDto;
import com.ap.menabev.dto.ResponseDto;
import com.ap.menabev.entity.MailTemplateDo;
import com.ap.menabev.invoice.MailTemplateRepository;
import com.ap.menabev.service.MailTemplateService;
import com.ap.menabev.util.ApplicationConstants;

public class MailTemplateServiceImpl implements MailTemplateService
{

	@Autowired
	MailTemplateRepository mailTemplateRepository;
	
     public ResponseDto saveMailTemplate(MailTemplateDto dto) {
		ResponseDto response = new ResponseDto();
		try {
			
			ModelMapper mapper = new ModelMapper();
			mailTemplateRepository.save(mapper.map(dto, MailTemplateDo.class));
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
	


	public List<MailTemplateDto> get() {
		List<MailTemplateDto> list = new ArrayList<>();

		ModelMapper modelMapper = new ModelMapper();
		try {
			List<MailTemplateDo> doList = mailTemplateRepository.findAll();
			for (MailTemplateDo mailTemplateDo : doList) {
				list.add(modelMapper.map(mailTemplateDo, MailTemplateDto.class));
			}
		} catch (Exception e) {
			System.err.println("Exception :" + e.getMessage());
		}
		return list;
	}

	public ResponseDto delete(String id) {
		ResponseDto response = new ResponseDto();
		try {
			mailTemplateRepository.deleteById(id);
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
