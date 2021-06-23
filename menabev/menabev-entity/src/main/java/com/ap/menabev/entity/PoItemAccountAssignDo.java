package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_ITEM_ACCOUNT_ASSGN")
@Getter
@Setter
public class PoItemAccountAssignDo {
    @Id
    @Column(name="PO_ACCOUNT_ASSIGNMENT_UUID")
	private String poAcctAssgnUuid = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name="DOCUMENT_ITEM",length=5)
	private String documentItem;
	@Column(name="SERIAL_NO",length=2)
	private String serialNo;
	@Column(name="DELETE_IND",length=1)
	private String deleteInd;
	@Column(name="CREATION_DATE",length=8)
	private Long creatDate;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="DIST_PER",length=3)
	private Double distPer;
	@Column(name="PO_UNIT")
	private Double poUnit;
	@Column(name="PO_ORDER_PRICE_UNIT")
	private Double poOrderPriceUnit;
	@Column(name="NET_VALUE",length=13)
	private Double netValue;
	@Column(name="GL_ACCOUNT",length=10)
	private String glAccount;
	@Column(name="COST_CENTER",length=10)
	private String costCenter;
	@Column(name="ASSET_NO",length=12)
	private String assetNo;
	@Column(name="SUB_NUM",length=4)
	private String subNum;
	@Column(name="ACT_TYPE",length=6)
	private String actType;
	@Column(name="SERVICE_DOC",length=10)
	private String serviceDoc;
	@Column(name="SERVICE_DOC_ITEM",length=6)
	private String serviceDocItem;
	@Column(name="SERVICE_DOC_TYPE",length=4)
	private String serviceDocType;
}
