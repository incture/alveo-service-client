package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class EmailTeamAPDto {

	private List<String> emailId;

	
	private String actionType;
	

	private Boolean isActive;
	

	private String emailTeamApid;
	

	private String configurationId;
}
