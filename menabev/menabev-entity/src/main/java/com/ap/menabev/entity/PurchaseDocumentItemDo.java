package com.ap.menabev.entity;


import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PO_ITEM")
@Getter
@Setter
@ToString
public class PurchaseDocumentItemDo{
    @Id
	@Column(name="ITEM_UUID")
	private String itemUuid = UUID.randomUUID().toString();
    @Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name="DOCUMENT_ITEM",length=5)
	private String documentItem;
	@Column(name="DELETE_IND",length=1)
	private String deleteInd;
	@Column(name="SHORT_TEXT",length=40)
	private String shortText;
	@Column(name="MATERIAL",length=18)
	private String material;
	@Column(name="MATERIAL_EXT",length=40)
	private String materialExt;
	@Column(name="E_MATERIAL",length=18)
	private String eMaterial;
	@Column(name="PLANT",length=4)
	private String plant;
	@Column(name="STORAGE_LOCATION",length=4)
	private String storageLoc;
	@Column(name="TRAKING_NO",length=10)
	private String trackingNo;
	@Column(name="MATERIAL_GROUP",length=9)
	private String materialGrp;
	@Column(name="INFO_RECORD",length=10)
	private String infoRecord;
	@Column(name="VENDOR_MATERIAL",length=35)
	private String vendMat;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="PO_UNIT",length=3)
	private String poUnit;
	@Column(name="PO_UNIT_ISO",length=3)
	private String poUnitISO;
	@Column(name="ORDER_PRICE_UNIT",length=3)
	private String orderPriceUnit;
	@Column(name="ORDER_PRICE_UNIT_ISO",length=3)
	private String orderPriceUnitISO;
	@Column(name="CONV_NUM1",length=5)
	private Double convNum1;
	@Column(name="CONV_DEN1",length=5)
	private Double convDen1;
	@Column(name="NET_PRICE",length=11)
	private Double netPrice;
	@Column(name="PRICE_UNIT",length=5)
	private Double priceUnit;
	@Column(name="GR_PR_TIME",length=3)
	private Double grPrTime;
	@Column(name="TAX_CODE",length=2)
	private String taxCode;
	@Column(name="NO_MORE_GR",length=1)
	private String noMoreGr;
	@Column(name="FINAL_INV_IND",length=1)
	private String finalInvInd;
	@Column(name="ITEM_CATEGORY",length=1)
	private String itemCategory;
	@Column(name="ACCOUNT_ASS_CAT",length=1)
	private String accountAssCat;
	@Column(name="DISTRIBUTION",length=1)
	private String distribution;
	@Column(name="PART_INV",length=1)
	private String partInv;
	@Column(name="GR_IND",length=1)
	private String grInd;
	@Column(name="GR_NON_VAL",length=1)
	private String gr_non_val;
	@Column(name="IR_IND",length=1)
	private String irInd;
	@Column(name="GR_BSD-IV-IND",length=1)
	private String grBsdIVInd;
	@Column(name="SRV_BSD_IV_IND",length=1)
	private String srvBsdIVInd;
	@Column(name="AGREEMENT",length=10)
	private String agreement;
	@Column(name="AGREEMENT_ITM",length=5)
	private String agreeemntItm;
	@Column(name="TAX_JUR_CODE",length=15)
	private String taxJurCode;
	@Column(name="SUPPL_VENDOR",length=10)
	private String supplVendor;
	@Column(name="PREQ_NUM",length=10)
	private String preqNum;
	@Column(name="PREQ_ITEM",length=5)
	private String preqItem;
	@Column(name="CONTRACT_NUM",length=10)
	private String contractNum;
	@Column(name="CONTRACT_ITM",length=10)
	private String contractItm;
	@Column(name="REF_DOC_NUM",length=10)
	private String refDocNum;
	@Column(name="REF_DOC_ITEM",length=5)
	private String refDocItem;
	@Column(name="DELI_COMPLETE",length=1)
	private String delivComplete;
	@Column(name="PART_DELV",length=1)
	private String partDelv;
	@Column(name="ITEM_CHANGED_AT",length=8)
	private Long itemChangedAt;
	@Column(name="PRODUCT_TYPE", length=2)
	private String productType;

}
