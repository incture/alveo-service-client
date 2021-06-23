package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_HISTORY_TOTALS")
@Getter
@Setter
public class PoHistoryTotalsDo {

	@Id
	@Column(name="PO_HISTORY_TOTAL_UUID")
	private String poHistoryTotalUuid = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name="DOCUMENT_ITEM",length=5)
	private String documentItem;
	@Column(name="SERIAL_NO",length=2)
	private String serialNo;
	@Column(name="WITHDR_QTY",length=13)
	private Double withdrQty;
	@Column(name="BLOCKED_QTY",length=13)
	private Double blockedQy;
	@Column(name="BL_QTY",length=13)
	private Double blQty;
	@Column(name="DELIV_QTY",length=13)
	private Double delivQty;
	@Column(name="PO_PR_QNT",length=13)
	private Double poPrQnt;
	@Column(name="VAL_GR_LOC",length=23)
	private Double valGrLoc;
	@Column(name="VAL_GR_FOR",length=23)
	private Double valGrFor;
	@Column(name="INV_QTY",length=13)
	private Double ivQty;
	@Column(name="INV_QTY_PO",length=13)
	private Double ivQtyPo;
	@Column(name="VAL_IV_LOC",length=23)
	private Double valIvLoc;
	@Column(name="VAL_IV_FOR",length=23)
	private Double valIvFor;
	@Column(name="CL_VAL_LOC",length=23)
	private Double clValLoc;
	@Column(name="CL_VAL_FOR",length=23)
	private Double clValFor;
	@Column(name="DOP_VL_LOC",length=23)
	private Double dopVlLoc;
	@Column(name="INV_VAL_LOC",length=23)
	private Double ivvalLoc;
	@Column(name="IV_VAL_FOR",length=23)
	private Double ivvalFor;
	@Column(name="DL_QTY_TRSP",length=13)
	private Double dlQtyTrsp;
	@Column(name="BL_QTY_TOTAL",length=13)
	private Double blQtyTotal;
	@Column(name="DL_QTY_TOTAL",length=13)
	private Double dlQtyTotal;
	@Column(name="INV_QTY_TOTAL",length=13)
	private Double ivQtyTotal;
	@Column(name="CURRENCY",length=5)
	private String currency;
	@Column(name="CURRENCY_ISO",length=3)
	private String currencyIso;
}
