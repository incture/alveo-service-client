package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_HISTORY")
@Getter
@Setter
public class PoHistoryDo {

	@Id
	@Column(name="PO_ITEM_HISTORY_UUID")
	private String poItemHistoryUuid;
	@Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_ITEM",length=5)
	private String documentItem;
	@Column(name="ACCOUNT_ASSIGN_NUM",length=2)
	private String acctAssgnNum;
	@Column(name="HISTORY_TYPE",length=1)
	private String historyType;
	@Column(name="HISTORY_YEAR",length=4)
	private String historyYear;
	@Column(name="HISTORY_DOCUMENT",length=10)
	private String historyDocument;
	@Column(name="HISTORY_DOCUMENT_ITEM",length=4)
	private String historyDocumentItem;
	@Column(name="HISTORY_CATEGORY",length=1)
	private String historyCategory;
	@Column(name="GOODS_MOVEMENT_TYPE",length=3)
	private String goodsMvmtType;
	@Column(name="POSTING_DATE",length=8)
	private Long postingDate;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="AMOUNT_IN_LOC_CURR",length=13)
	private Double amountLocCurr;
	@Column(name="AMOUNT_FOR_CURR",length=13)
	private Double amountForCurr;
	@Column(name="CURRENCY",length=5)
	private String currency;
	@Column(name="ACCOUNT_CL_LOC_CURR",length=23)
	private Double amountClLocCurr;
	@Column(name="AMOUNT_CL_FOR_CURR",length=13)
	private Double amountClForCurr;
	@Column(name="BLOCKED_QTY_OU",length=13)
	private Double blockedQtyOU;
	@Column(name="BLOCKED_QTY_OPU",length=13)
	private Double blockedQtyOPU;
	@Column(name="DEBIT_CREDIT_IND",length=1)
	private String debitCreditInd;
	@Column(name="VALUATION_TYPE",length=10)
	private String valuationType;
	@Column(name="NO_MORE_GR",length=1)
	private String noMoreGR;
	@Column(name="REF_DOC_ID",length=16)
	private String refDocId;
	@Column(name="REF_DOC_NUM",length=10)
	private String refDocNum;
	@Column(name="REF_DOC_ITEM",length=4)
	private String refDocItem;
	@Column(name="REF_DOC_YEAR",length=4)
	private String refDocYear;
	@Column(name="ENTRY_DATE",length=8)
	private Long entryDate;
	@Column(name="INVOICE_AMOUNT_LOC_CURR",length=23)
	private Double invoiceAmountLocCurr;
	@Column(name="INVOICE_AMOUNT_FOR_CURR",length=23)
	private Double invoiceAmountForCurr;
	@Column(name="MATERIAL",length=18)
	private String material;
	@Column(name="PLANT",length=4)
	private String plant;
	@Column(name="CONDITION_DOC",length=10)
	private String contitionDoc;
	@Column(name="TAX_CODE",length=2)
	private String taxCode;
	@Column(name="DELIVERY_QTY",length=13)
	private Double delivQty;
	@Column(name="DELIVERY_UNIT",length=3)
	private String delivUnit;
	@Column(name="MANUF_MAT",length=18)
	private String manufMat;
	@Column(name="COMPANY_CODE_CURRENCY",length=5)
	private String companyCodeCurr;
	@Column(name="DOCUMENT_DATE",length=8)
	private Long documentDate;
	@Column(name="CURRENCY_ISO",length=3)
	private String currencyISO;
	@Column(name="LOCAL_CURRENCY_ISO",length=3)
	private String locCurrISO;
	@Column(name="DELIVERY_UNIT_ISO",length=3)
	private String delivUnitISO;
	@Column(name="EXTERNAL_MATERIAL",length=40)
	private String extenalMaterial;
	@Column(name="REF_DOC_NUM_LONG",length=35)
	private String refDocNumLong;
	@Column(name="ACCOUNTION_DOC_CREATION_TIME",length=6)
	private Long accountingDocCreationTime;
	@Column(name="SRV_POS",length=18)
	private String srvpos;
	@Column(name="PACK_NO",length=10)
	private String packNo;
	@Column(name="INT_ROW",length=10)
	private String intRow;
	@Column(name="PLN_PACK_NO",length=10)
	private String plnPackNo;
	@Column(name="PLN_INT_ROW",length=10)
	private String plnIntRow;
	@Column(name="EXT_ROW",length=10)
	private String extRow;
}
