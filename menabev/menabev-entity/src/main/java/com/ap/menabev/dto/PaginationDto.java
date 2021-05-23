package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaginationDto {

	Integer limit;
	Integer offset;
}
