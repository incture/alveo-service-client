package com.ap.menabev.dto;

import java.util.List;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToItemAccountAssignment {

	@Getter
	@Setter
	public class Results{
		private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String SerialNo;

	    private boolean DeleteInd;

	    private String CreatDate;

	    private String Quantity;

	    private String DistrPerc;

	    private String NetValue;

	    private String GlAccount;

	    private String BusArea;

	    private String Costcenter;

	    private String SdDoc;

	    private String ItmNumber;

	    private String SchedLine;

	    private String AssetNo;

	    private String SubNumber;

	    private String Orderid;

	    private String GrRcpt;

	    private String UnloadPt;

	    private String CoArea;

	    private String Costobject;

	    private String ProfitCtr;

	    private String WbsElement;

	    private String Network;

	    private String RlEstKey;

	    private String PartAcct;

	    private String CmmtItem;

	    private String RecInd;

	    private String FundsCtr;

	    private String Fund;

	    private String FuncArea;

	    private String RefDate;

	    private String TaxCode;

	    private String Taxjurcode;

	    private String NondItax;

	    private String Acttype;

	    private String CoBusproc;

	    private String ResDoc;

	    private String ResItem;

	    private String Activity;

	    private String GrantNbr;

	    private String CmmtItemLong;

	    private String FuncAreaLong;

	    private String BudgetPeriod;

	    private boolean FinalInd;

	    private String FinalReason;

	    private String ServiceDoc;

	    private String ServiceItem;

	    private String ServiceDocType;
	}
	
	private List<Results> results;
}
