package com.ap.menabev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "MATCHING_HISTORY")
public class MatchingHistoryDo {
	@Id
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "VENDOR_ID")
	private String vendorId;

	@Column(name = "I_MAT_NO")
	private String iMatNo;

	@Column(name = "I_UPC_CODE")
	private String iUpcCode;

	@Column(name = "I_TEXT")
	private String iText;

	@Column(name = "P_MAT_NO")
	private String pMatNo;

	@Column(name = "P_SERVICE_ID")
	private String pServiceId;

	@Column(name = "P_UPC_CODE")
	private String pUpcCode;

	@Column(name = "P_TEXT")
	private String pText;

	@Column(name = "P_VMN")
	private String pVMN;

	@Column(name = "LAST_MATCHED_BY")
	private String lastMatchedBy;

	@Column(name = "MATCH_SCORE")
	private Integer matchScore;

}
