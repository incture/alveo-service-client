package com.ap.menabev.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.ap.menabev.dto.StatusPKId;

import lombok.Data;

@Entity
@Table(name = "STATUS_CONFIG")
@IdClass(StatusPKId.class)
public @Data class StatusConfigDo implements Serializable {

	@Id
	@Column(name="LIFECYCLESTATUS")
	private String lifeCycleStatus;
	
	@Id
	@Column(name="LANGUAGE_KEY")
	private String langKey;
	
	@Column(name="TEXT")
	private String text;
}
