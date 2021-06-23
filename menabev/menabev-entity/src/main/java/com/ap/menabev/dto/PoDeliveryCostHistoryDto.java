package com.ap.menabev.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoDeliveryCostHistoryDto {

	private String poDelivCostHistoryUuid;
	private String documentNumber;
	private String itemNo;
	private String stepNumber;
	private String conditionCounter;
	private String historyType;
	private String fiscalYear;
	private String accDoc;
	private String matDocItem;
	private String historyCategory;
	private Long postingDate;
	private Double quantity;
	private Double amtLC;
	private Double amtDC;
	private String currencyKey;
	private Double grIrClrValLC;
	private String drCrInd;
	private String refDoc;
	private String grBillLading;
	private String vendor;
	private Long accDocCreationDt;
	private Long accDocCreationTm;
	private Double invValLC;
	private Double invValFC;
	private String conditionType;
	private Double qtyOpu;
	private Double grIrClrValTC;
	private String locCurrency;
	private Double condNetVal;
	private String creatorName;
	private String drCrInd1;
	private Double grIrClrValPC;
	private Double invAmtPC;
	private Double exchgRateDiffAmt;
	private String mulAccAss;
	private Double exchgRate;
	private Long grBlkBaseUom;

}
