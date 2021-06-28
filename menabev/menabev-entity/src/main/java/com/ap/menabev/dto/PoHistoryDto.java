package com.ap.menabev.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoHistoryDto {

	private String poItemHistoryUuid;
	private String documentNumber;
	private String documentItem;
	private String acctAssgnNum;
	private String historyType;
	private String historyYear;
	private String historyDocument;
	private String historyDocumentItem;
	private String historyCategory;
	private String goodsMvmtType;
	private Long postingDate;
	private Double quantity;
	private Double amountLocCurr;
	private Double amountForCurr;
	private String currency;
	private Double amountClLocCurr;
	private Double amountClForCurr;
	private Double blockedQtyOU;
	private Double blockedQtyOPU;
	private String debitCreditInd;
	private String valuationType;
	private Boolean noMoreGR;//
	private String refDocId;
	private String refDocNum;
	private String refDocItem;
	private String refDocYear;
	private Long entryDate;
	private Double invoiceAmountLocCurr;
	private Double invoiceAmountForCurr;
	private String material;
	private String plant;
	private String contitionDoc;
	private String taxCode;
	private Double delivQty;
	private String delivUnit;
	private String manufMat;
	private String companyCodeCurr;
	private Long documentDate;
	private String currencyISO;
	private String locCurrISO;
	private String delivUnitISO;
	private String extenalMaterial;
	private String refDocNumLong;
	private Long accountingDocCreationTime;
	private String srvpos;
	private String packNo;
	private String intRow;
	private String plnPackNo;
	private String plnIntRow;
	private String extRow;
}
