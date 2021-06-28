package com.ap.menabev.dto;


import javax.persistence.Column;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class PoItemAccountAssignDto {

	private String poAcctAssgnUuid;
	private String documentNumber;
	private String documentItem;
	private String serialNo;
	private Boolean deleteInd;//
	private Long creatDate;
	private Double quantity;
	private Double distPer;
	private Double poUnit;
	private Double poOrderPriceUnit;
	private Double netValue;
	private String glAccount;
	private String costCenter;
	private String assetNo;
	private String subNum;
	private String actType;
	private String serviceDoc;
	private String serviceDocItem;
	private String serviceDocType;
	private String coArea;
	private String profitCtr;
}
