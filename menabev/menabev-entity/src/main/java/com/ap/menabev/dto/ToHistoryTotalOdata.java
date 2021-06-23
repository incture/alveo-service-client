package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToHistoryTotalOdata {

	@Getter
	@Setter
	@ToString
	public class Results{
		private __metadata __metadata;

	    private String Number;

	    private String ItemNo;

	    private String SerialNo;

	    private String WithdrQty;

	    private String BlockedQy;

	    private String BlQty;

	    private String DelivQty;

	    private String PoPrQnt;

	    private String ValGrLoc;

	    private String ValGrFor;

	    private String IvQty;

	    private String IvQtyPo;

	    private String ValIvLoc;

	    private String ValIvFor;

	    private String ClValLoc;

	    private String ClValFor;

	    private String DopVlLoc;

	    private String IvvalLoc;

	    private String IvvalFor;

	    private String DlQtyTrsp;

	    private String BlQtyTotal;

	    private String DlQtyTotal;

	    private String IvQtyTotal;

	    private String Currency;

	    private String CurrencyIso;
	}
	
	List<Results> results;
	
}
