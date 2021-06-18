package com.ap.menabev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SchedulerResponseDto {
	private String message;
	private int noOfEmailspicked;
	private int noOfAttachement;
	private int noOfEmailsReadSuccessfully;
	private int noOfPDFs;
}
