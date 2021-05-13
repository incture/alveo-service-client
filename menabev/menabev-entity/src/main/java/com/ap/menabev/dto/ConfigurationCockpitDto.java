package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ConfigurationCockpitDto 
{	
	private List<VendorDetailsDto> vendorDetailsDto;
	
	private ConfigurationDto configurationDto;
	
	private List<EmailTeamAPDto> emailTeamDto;
	
	
	private List<MailTemplateDto> mailTemplateDto;
	
	private List<SchedulerConfigurationDto> schedulerConfigurationdto;
	

}
