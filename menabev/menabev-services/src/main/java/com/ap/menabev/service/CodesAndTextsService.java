package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.CodesAndTextsDto;
import com.ap.menabev.dto.ResponseDto;

public interface CodesAndTextsService {

	ResponseDto saveOrUpdate(CodesAndTextsDto dto);
	List<CodesAndTextsDto> get(String uuId,String statusCode,String type,String language);
}
