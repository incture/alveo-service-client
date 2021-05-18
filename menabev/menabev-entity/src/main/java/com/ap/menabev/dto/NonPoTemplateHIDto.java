package com.ap.menabev.dto;



import java.util.List;

import lombok.Data;

public @Data class NonPoTemplateHIDto {
	NonPoTemplateDto  nonPoTemplate;
	String accountNo;
	List<NonPoTemplateItemsDto> nonPoTemplateItems;

	public NonPoTemplateHIDto(NonPoTemplateDto nonPoTemplate, List<NonPoTemplateItemsDto> nonPoTemplateItems) {
		super();
		this.nonPoTemplate = nonPoTemplate;
		this.nonPoTemplateItems = nonPoTemplateItems;
	}

	public NonPoTemplateHIDto() {
		
		// TODO Auto-generated constructor stub
	}
}
