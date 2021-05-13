package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MAIL_TEMPLATE")
@ToString
@Getter
@Setter
public class MailTemplateDo
{
	@Column(name="SUBJECT")
	private String subject;

	@Column(name = "ACTION_TYPE")
	private String actionType;
	
	@Column(name = "BODY")
	private String body;
	
	@Id
	@Column(name = "MAIL_TEMPLATE_ID")
	private String mailTemplateId;
	
	@Column(name = "CONFIGURATION_ID")
	private String configurationId;
	
}
