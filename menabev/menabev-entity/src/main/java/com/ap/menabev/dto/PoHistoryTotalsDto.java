package com.ap.menabev.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoHistoryTotalsDto {

	private String poHistoryTotalUuid;
	private String documentNumber;
	private String documentItem;
	private String serialNo;
	private Double withdrQty;
	private Double blockedQy;
	private Double blQty;
	private Double delivQty;
	private Double poPrQnt;
	private Double valGrLoc;
	private Double valGrFor;
	private Double ivQty;
	private Double ivQtyPo;
	private Double valIvLoc;
	private Double valIvFor;
	private Double clValLoc;
	private Double clValFor;
	private Double dopVlLoc;
	private Double ivvalLoc;
	private Double ivvalFor;
	private Double dlQtyTrsp;
	private Double blQtyTotal;
	private Double dlQtyTotal;
	private Double ivQtyTotal;
	private String currency;
	private String currencyIso;
}
