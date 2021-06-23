package com.ap.menabev.dto;

import java.util.List;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//Po Results Odata

@Getter
@Setter
@ToString
public class PoHeaderOdata {
	private __metadata __metadata;

	private String Number;

	private String CompCode;

	private String DocType;

	private String DocCategory;

	private String DeleteInd;

	private String Status;

	private String CreatDate;

	private String CreatedBy;

	private String ItemIntvl;

	private String Vendor;

	private String Langu;

	private String LanguIso;

	private String Pmnttrms;

	private String Dscnt1To;

	private String Dscnt2To;

	private String Dscnt3To;

	private String DsctPct1;

	private String DsctPct2;

	private String PurchOrg;

	private String PurGroup;

	private String Currency;

	private String CurrencyIso;

	private String ExchRate;

	private boolean ExRateFx;

	private String DocDate;

	private String VperStart;

	private String VperEnd;

	private String Warranty;

	private String Quotation;

	private String QuotDate;

	private String Ref1;

	private String SalesPers;

	private String Telephone;

	private String SupplVend;

	private String Customer;

	private String Agreement;

	private boolean GrMessage;

	private String SupplPlnt;

	private String Incoterms1;

	private String Incoterms2;

	private String CollectNo;

	private String DiffInv;

	private String OurRef;

	private String Logsystem;

	private String Subitemint;

	private String PoRelInd;

	private String RelStatus;

	private String VatCntry;

	private String VatCntryIso;

	private String ReasonCancel;

	private String ReasonCode;

	private String RetentionType;

	private String RetentionPercentage;

	private String DownpayType;

	private String DownpayAmount;

	private String DownpayPercent;

	private String DownpayDuedate;

	private boolean Memory;

	private String Memorytype;

	private String Shiptype;

	private String Handoverloc;

	private String Shipcond;

	private String Incotermsv;

	private String Incoterms2l;

	private String Incoterms3l;

	private String ExtSys;

	private String ExtRef;

	private boolean IntrastatRel;

	private boolean IntrastatExcl;

	private String ExtRevTmstmp;

	private String DocStatus;

	private String LastChangeDateTime;

	private ToHistoryOdata ToHistory;

	private ToHistoryTotalOdata ToHistoryTotal;

	private ToDeliveryCostHistoryOata ToDeliveryCostHistory;

	// private ToPartnerOdata ToPartner;

	private ToItemOdata ToItem;

	// private ToHeaderNoteOdata ToHeaderNote;

}