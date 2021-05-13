package com.ap.menabev.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "AP_TEAM_EMAIL")
@ToString
@Getter
@Setter
public class EmailTeamAPDo 
{
	@Column(name="EMAIL_ID")
	private String emailId;

	@Column(name = "ACTION_TYPE")
	private String actionType;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	@Id
	@Column(name = "EMAIL_TEAM_AP_ID")
	private String emailTeamApid;
	
	@Column(name = "CONFIGURATION_ID")
	private String configurationId;
		
}
