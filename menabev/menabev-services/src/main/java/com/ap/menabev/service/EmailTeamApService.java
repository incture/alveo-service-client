package com.ap.menabev.service;

import java.util.List;

import com.ap.menabev.dto.EmailTeamAPDto;
import com.ap.menabev.dto.ResponseDto;

public interface EmailTeamApService 
{
	ResponseDto save(EmailTeamAPDto dto);

	List<EmailTeamAPDto > get();

	ResponseDto delete(String id);


}
