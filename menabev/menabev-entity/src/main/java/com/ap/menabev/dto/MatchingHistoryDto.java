package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MatchingHistoryDto {

	private String uuid;

	private String vendorId;

	private String iMatNo;

	private String iUpcCode;

	private String iText;

	private String pMatNo;

	private String pServiceId;

	private String pUpcCode;

	private String pText;

	private String pVMN;

	private String lastMatchedBy;

	private Integer matchScore;
	
	private String customerItemIdVmn;
	
	private String wasCreateOrUpdateSuccesfull;
}
