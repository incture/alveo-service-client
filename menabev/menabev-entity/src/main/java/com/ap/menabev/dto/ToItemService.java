package com.ap.menabev.dto;

import java.util.List;

import org.apache.axis2.databinding.types.xsd.DateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToItemService {

	@Getter
	@Setter
	public class Results{
	    private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String PckgNo;

	    private String LineNo;

	    private String ExtLine;

	    private int OutlLevel;

	    private String OutlNo;

	    private String OutlInd;

	    private String SubpckgNo;

	    private String ServiceNo;

	    private String ServType;

	    private String Edition;

	    private String SscItem;

	    private String ExtServ;

	    private String Quantity;

	    private String BaseUom;

	    private String UomIso;

	    private String OvfTol;

	    private boolean OvfUnlim;

	    private String PriceUnit;

	    private String GrPrice;

	    private String FromLine;

	    private String ToLine;

	    private String ShortText;

	    private String Distrib;

	    private String PersNo;

	    private String Wagetype;

	    private String PlnPckg;

	    private String PlnLine;

	    private String ConPckg;

	    private String ConLine;

	    private String TmpPckg;

	    private String TmpLine;

	    private boolean SscLim;

	    private String LimitLine;

	    private String TargetVal;

	    private String BaslineNo;

	    private String BasicLine;

	    private String Alternat;

	    private String Bidder;

	    private String SuppLine;

	    private String OpenQty;

	    private String Inform;

	    private String Blanket;

	    private String Eventual;

	    private String TaxCode;

	    private String Taxjurcode;

	    private boolean PriceChg;

	    private String MatlGroup;

	    private String Date;

	    private String Begintime;

	    private String Endtime;

	    private String ExtpersNo;

	    private String Formula;

	    private String FormVal1;

	    private String FormVal2;

	    private String FormVal3;

	    private String FormVal4;

	    private String FormVal5;

	    private String Userf1Num;

	    private String Userf2Num;

	    private String Userf1Txt;

	    private String Userf2Txt;

	    private String HiLineNo;

	    private String Extrefkey;

	    private String DeleteInd;

	    private String PerSdate;

	    private String PerEdate;

	    private String ExternalItemId;

	    private String ServiceItemKey;

	    private String NetValue;
	}
	
	private List<Results> results;
 }
