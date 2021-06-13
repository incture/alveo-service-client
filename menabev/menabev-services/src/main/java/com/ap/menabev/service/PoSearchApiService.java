package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.PoSearchApiDto;

public interface PoSearchApiService {

	List<PoSearchApiDto> poSearch(PoSearchApiDto dto);

}
