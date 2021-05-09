package com.ap.menabev.service;



import java.util.List;

import com.ap.menabev.dto.NonPoTemplateItemsDto;
import com.ap.menabev.dto.ResponseDto;


public interface NonPoTemplateItemsService {

	ResponseDto saveNonPoTemplateItems(NonPoTemplateItemsDto dto);

	List<NonPoTemplateItemsDto> get();

//	ResponseDto delete(Integer id);
	
	ResponseDto updateNonPoTemplateItems(NonPoTemplateItemsDto dto);

	ResponseDto delete(NonPoTemplateItemsDto nonPoTemplateItemDto);
	
	

}
