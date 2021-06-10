package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.MatchingHistoryDto;

public interface MatchingHistoryService {

	List<MatchingHistoryDto> saveOrUpdate(List<MatchingHistoryDto> dto);

	List<MatchingHistoryDto> get(MatchingHistoryDto dto);

}
