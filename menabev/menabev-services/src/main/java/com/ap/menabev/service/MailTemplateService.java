package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.MailTemplateDto;
import com.ap.menabev.dto.ResponseDto;

public interface MailTemplateService 
{
	ResponseDto saveMailTemplate(MailTemplateDto dto);

	List<MailTemplateDto> get();

	ResponseDto delete(String id);

}
