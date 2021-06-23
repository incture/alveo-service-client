package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToDeliveryCostHistoryOata {

	@Getter
	@Setter
	@ToString
	public class Results{
		private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String StepNumber;

	    private String ConditionCounter;

	    private String HistoryType;

	    private String FiscalYear;

	    private String AccDoc;

	    private String MatDocItem;

	    private String HistoryCategory;

	    private String PostingDate;

	    private String Quantity;

	    private String AmtLC;

	    private String AmtDC;

	    private String CurrencyKey;

	    private String GrIrClrValLC;

	    private String DrCrInd;

	    private String RefDoc;

	    private String GrBillLading;

	    private String Vendor;

	    private String AccDocCreationDt;

	    private String AccDocCreationTm;

	    private String InvValLC;

	    private String InvValFC;

	    private String ValuationType;

	    private String ConditionType;

	    private String QtyOpu;

	    private String GrIrClrValTC;

	    private String LocCurrency;

	    private String CondNetVal;

	    private String CreatorName;

	    private String DrCrInd1;

	    private String GrIrClrValPC;

	    private String InvAmtPC;

	    private String SapRelease;

	    private String Quantity1;

	    private String AmtLC1;

	    private String AmtDC1;

	    private String QtyOpu1;

	    private String GrIrClrValLC1;

	    private String ExchgRateDiffAmt;

	    private String MulAccAss;

	    private String ExchgRate;

	    private String DataFilter;

	    private String QtyParallelUom;

	    private String GrBlkBaseUom;

	    private String ParallelUomType;
	}
	
	List<Results> results;
}
