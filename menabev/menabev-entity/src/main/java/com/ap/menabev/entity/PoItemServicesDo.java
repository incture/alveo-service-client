package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_ITEM_SERVICES")
@Getter
@Setter
public class PoItemServicesDo {
    @Id
    @Column(name="ITEM_SERVICE_UUID")
	private String itemServiceUuid = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name="DOCUMENT_ITEM",length=5)
	private String documentItem;
	@Column(name="PACKAGE_NO",length=10)
	private String pckgNo;
	@Column(name="LINE_NO",length=10)
	private String lineNo;
	@Column(name="EXT_LINE",length=10)
	private String extLine;
	@Column(name="OUTL_LEVEL",length=3)
	private Integer outlLevel;
	@Column(name="OUTL_NO",length=8)
	private String outlNo;
	@Column(name="OUTL_IND",length=1)
	private String outlInd;
	@Column(name="SUB_PKG_NO",length=10)
	private String subpckgNo;
	@Column(name="SERVICE",length=18)
	private String service;
	@Column(name="SERVICE_TYPE",length=3)
	private String servType;
	@Column(name="EDITION",length=4)
	private String edition;
	@Column(name="SSC_ITEM",length=18)
	private String sscItem;
	@Column(name="EXT_SERVICE",length=18)
	private String extServ;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="BASE_UOM",length=3)
	private String baseUom;
	@Column(name="UOM_ISO",length=3)
	private String uomIso;
	@Column(name="OVF_TOL",length=3)
	private Double ovfTol;
	@Column(name="OVF_UN_LIM",length=1)
	private String ovfUnlim;
	@Column(name="PRICE_UNIT",length=5)
	private Double priceUnit;
	@Column(name="GR_PRICE",length=23)
	private Double grPrice;
	@Column(name="FROM_LINE",length=6)
	private String fromLine;
	@Column(name="TO_LINE",length=6)
	private String toLine;
	@Column(name="SHORT_TEXT",length=40)
	private String shortText;
	@Column(name="DISTRIBUTION",length=1)
	private String distrib;
	@Column(name="TAX_CODE",length=2)
	private String taxCode;
	@Column(name="TAX_JUR_CODE",length=15)
	private String taxjurcode;
	@Column(name="NET_VALUE",length=23)
	private Double netValue;
}
