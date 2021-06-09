package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class APScanningTeamDto {
	private String emailId;
	private String emailTeamApid;
	private String configurationId;
	private Boolean isActive;
}
