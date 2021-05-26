package com.ap.menabev.entity;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "SEQUENCE_TRACKER")
public class SequenceGeneratorEntity {
	
	@Id
	@Column(name = "SL_NO")
	private String slNo = UUID.randomUUID().toString();
	
	@Column(name = "SEQ_CODE")
	private String seqCode;
	
	@Column(name = "COUNTER")
	private int counter;
	
	@Column(name = "DATE")
	private LocalDate date;
	
	@Column(name = "MAPPED_ID")
	private String mappedId;
}
