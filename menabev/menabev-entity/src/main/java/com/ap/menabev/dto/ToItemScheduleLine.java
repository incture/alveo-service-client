package com.ap.menabev.dto;

import java.sql.Date;
import java.util.List;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToItemScheduleLine {

	@Getter
	@Setter
	@ToString
	public class Results{
	    private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String SchedLine;

	    private String DelDatcatExt;

	    private String DeliveryDate;

	    private String Quantity;

	    private String DelivTime;

	    private String StatDate;

	    private String PreqNo;

	    private String PreqItem;

	    private String PoDate;

	    private String Routesched;

	    private String MsDate;

	    private String MsTime;

	    private String LoadDate;

	    private String LoadTime;

	    private String TpDate;

	    private String TpTime;

	    private String GiDate;

	    private String GiTime;

	    private String DeleteInd;

	    private boolean ReqClosed;

	    private String GrEndDate;

	    private String GrEndTime;

	    private String ComQty;

	    private String ComDate;

	    private String GeoRoute;

	    private String Handoverdate;

	    private String Handovertime;
	}
	
	private List<Results> results;
}
