package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NonPoTemplateFilterDto {

	String templateName;
	String accountNo;
	PaginationDto pagination;
}
