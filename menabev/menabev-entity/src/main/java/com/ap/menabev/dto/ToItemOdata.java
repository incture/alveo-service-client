package com.ap.menabev.dto;

import java.util.List;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class ToItemOdata {

	@Getter
	@Setter
	@ToString
	public class Results {
		private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String DeleteInd;

	    private String ShortText;

	    private String Material;

	    private String MaterialExternal;

	    private String MaterialGuid;

	    private String MaterialVersion;

	    private String Ematerial;

	    private String EmaterialExternal;

	    private String EmaterialGuid;

	    private String EmaterialVersion;

	    private String Plant;

	    private String StgeLoc;

	    private String Trackingno;

	    private String MatlGroup;

	    private String InfoRec;

	    private String VendMat;

	    private String Quantity;

	    private String PoUnit;

	    private String PoUnitIso;

	    private String OrderprUn;

	    private String OrderprUnIso;

	    private String ConvNum1;

	    private String ConvDen1;

	    private String NetPrice;

	    private String PriceUnit;

	    private String GrPrTime;

	    private String TaxCode;

	    private String BonGrp1;

	    private String QualInsp;

	    private String InfoUpd;

	    private boolean PrntPrice;

	    private boolean EstPrice;

	    private String Reminder1;

	    private String Reminder2;

	    private String Reminder3;

	    private String OverDlvTol;

	    private boolean UnlimitedDlv;

	    private String UnderDlvTol;

	    private String ValType;

	    private boolean NoMoreGr;

	    private boolean FinalInv;

	    private String ItemCat;

	    private String Acctasscat;

	    private String Distrib;

	    private String PartInv;

	    private boolean GrInd;

	    private boolean GrNonVal;

	    private boolean IrInd;

	    private boolean FreeItem;

	    private boolean GrBasediv;

	    private boolean AcknReqd;

	    private String AcknowlNo;

	    private String Agreement;

	    private String AgmtItem;

	    private String Shipping;

	    private String Customer;

	    private String CondGroup;

	    private boolean NoDisct;

	    private String PlanDel;

	    private String NetWeight;

	    private String Weightunit;

	    private String WeightunitIso;

	    private String Taxjurcode;

	    private String CtrlKey;

	    private String ConfCtrl;

	    private String RevLev;

	    private String Fund;

	    private String FundsCtr;

	    private String CmmtItem;

	    private String Pricedate;

	    private String PriceDate;

	    private String GrossWt;

	    private String Volume;

	    private String Volumeunit;

	    private String VolumeunitIso;

	    private String Incoterms1;

	    private String Incoterms2;

	    private String PreVendor;

	    private String VendPart;

	    private String HlItem;

	    private String GrToDate;

	    private String SuppVendor;

	    private boolean ScVendor;

	    private String KanbanInd;

	    private boolean Ers;

	    private String RPromo;

	    private String Points;

	    private String PointUnit;

	    private String PointUnitIso;

	    private String Season;

	    private String SeasonYr;

	    private String BonGrp2;

	    private String BonGrp3;

	    private boolean SettItem;

	    private String Minremlife;

	    private String RfqNo;

	    private String RfqItem;

	    private String PreqNo;

	    private String PreqItem;

	    private String RefDoc;

	    private String RefItem;

	    private String SiCat;

	    private boolean RetItem;

	    private String AtRelev;

	    private String OrderReason;

	    private String BrasNbm;

	    private String MatlUsage;

	    private String MatOrigin;

	    private boolean InHouse;

	    private String Indus3;

	    private String InfIndex;

	    private String UntilDate;

	    private boolean DelivCompl;

	    private String PartDeliv;

	    private boolean ShipBlocked;

	    private String PreqName;

	    private String PeriodIndExpirationDate;

	    private String IntObjNo;

	    private String PckgNo;

	    private String Batch;

	    private String Vendrbatch;

	    private String Calctype;

	    private String GrantNbr;

	    private String CmmtItemLong;

	    private String FuncAreaLong;

	    private boolean NoRounding;

	    private String PoPrice;

	    private String SupplStloc;

	    private boolean SrvBasedIv;

	    private String FundsRes;

	    private String ResItem;

	    private boolean OrigAccept;

	    private String AllocTbl;

	    private String AllocTblItem;

	    private String SrcStockType;

	    private String ReasonRej;

	    private String CrmSalesOrderNo;

	    private String CrmSalesOrderItemNo;

	    private String CrmRefSalesOrderNo;

	    private String CrmRefSoItemNo;

	    private String PrioUrgency;

	    private String PrioRequirement;

	    private String ReasonCode;

	    private String FundLong;

	    private String LongItemNumber;

	    private String ExternalSortNumber;

	    private String ExternalHierarchyType;

	    private String RetentionPercentage;

	    private String DownpayType;

	    private String DownpayAmount;

	    private String DownpayPercent;

	    private String DownpayDuedate;

	    private String ExtRfxNumber;

	    private String ExtRfxItem;

	    private String ExtRfxSystem;

	    private String SrmContractId;

	    private String SrmContractItm;

	    private String BudgetPeriod;

	    private String BlockReasonId;

	    private String BlockReasonText;

	    private String SpeCrmFkrel;

	    private String DateQtyFixed;

	    private boolean GiBasedGr;

	    private String Shiptype;

	    private String Handoverloc;

	    private String TcAutDet;

	    private String ManualTcReason;

	    private String FiscalIncentive;

	    private String FiscalIncentiveId;

	    private String TaxSubjectSt;

	    private String ReqSegment;

	    private String StkSegment;

	    private String SfTxjcd;

	    private String Incoterms2l;

	    private String Incoterms3l;

	    private String MaterialLong;

	    private String EmaterialLong;

	    private String Serviceperformer;

	    private String Producttype;

	    private String Startdate;

	    private String Enddate;

	    private String ReqSegLong;

	    private String StkSegLong;

	    private String ExpectedValue;

	    private String LimitAmount;

	    private String ExtRef;

	    private String GlAccount;

	    private String Costcenter;

	    private String WbsElement;

	    private String CommodityCode;

	    private String IntrastatServiceCode;

	    private String NetValue;

	    private String GrossValue;

	    private String InterArticleNum;

	    private String ItemChangeDate;

//	    private ToItemNote ToItemNote;

	    private ToItemAccountAssignment ToItemAccountAssignment;

//	    private ToItemPricingElement ToItemPricingElement;

	    private ToItemScheduleLine ToItemScheduleLine;

	    private ToItemService ToItemService;

//	    private ToItemHistory ToItemHistory;

//	    private ToItemHistoryTotal ToItemHistoryTotal;

//	    private ToItemDeliveryCostHistory ToItemDeliveryCostHistory;

//	    private ToHeader ToHeader;
	}
	
	List<Results> results;
}
