package com.ap.menabev.service;



import java.io.IOException;
import java.util.List;

import com.ap.menabev.dto.AllocationDto;
import com.ap.menabev.dto.NonPoTemplateDto;
import com.ap.menabev.dto.NonPoTemplateHIDto;
import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.dto.ResponseDto;



public interface NonPoTemplateService {
	ResponseDto saveNonPoTemplate(NonPoTemplateHIDto dto);

	List<NonPoTemplateHIDto> get(int limit,int offset);
	List<NonPoTemplateHIDto> get();
//	ResponseDto delete(Integer id);
	ResponseDto updateNonPoTemplate(NonPoTemplateDto dto);
//	NonPoTemplateHIDto get
	List<NonPoTemplateItemsDto>getNonPoTemplateItems(String templateId);
	ResponseDto updateNonPoTemplateHI(NonPoTemplateHIDto dto);

	ResponseDto delete(List<String> templateId);

	List<AllocationDto> selectNonPoTemplate();

	ResponseDto postNonPoItemsToSAP() throws IOException;

}
