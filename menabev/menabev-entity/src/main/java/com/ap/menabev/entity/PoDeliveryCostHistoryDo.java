package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_DELIVERY_COST_HISTORY")
@Getter
@Setter
public class PoDeliveryCostHistoryDo {

	@Id
	@Column(name="PO_DEL_COST_HIS_UUID")
	private String poDelivCostHistoryUuid = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_NUMBER",length=10  )
	private String documentNumber;
	@Column(name="ITEM_NO",length=5)
	private String itemNo;
	@Column(name="STEP_NUMBER",length=3)
	private String stepNumber;
	@Column(name="CONDITION_COUNTER",length=3)
	private String conditionCounter;
	@Column(name="HISTORY_TYPE",length=1)
	private String historyType;
	@Column(name="FISCAL_YEAR",length=4)
	private String fiscalYear;
	@Column(name="ACC_DOC",length=10)
	private String accDoc;
	@Column(name="MAT_DOC_ITEM",length=4)
	private String matDocItem;
	@Column(name="HISTORY_CATEGORY",length=1)
	private String historyCategory;
	@Column(name="POSTING_DATE",length=8)
	private Long postingDate;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="AMT_LC",length=13)
	private Double amtLC;
	@Column(name="AMT_DC",length=13)
	private Double amtDC;
	@Column(name="CURRENCY_KEY",length=5)
	private String currencyKey;
	@Column(name="GR_IR_CLR_VAL_LC",length=13)
	private Double grIrClrValLC;
	@Column(name="CR_DR_IND",length=1)
	private String drCrInd;
	@Column(name="REF_DOC",length=16)
	private String refDoc;
	@Column(name="GR_BILL_LANDING",length=16)
	private String grBillLading;
	@Column(name="VENDOR",length=10)
	private String vendor;
	@Column(name="ACC_DOC_CREATION_DATE",length=8)
	private Long accDocCreationDt;
	@Column(name="ACCOUNT_DOC_CREATION_TIME",length=6)
	private Long accDocCreationTm;
	@Column(name="INV_VAL_LC",length=13)
	private Double invValLC;
	@Column(name="INV_VAL_FC",length=13)
	private Double invValFC;
	@Column(name="CONDITION_TYPE",length=4)
	private String conditionType;
	@Column(name="QTY_OPU",length=13)
	private Double qtyOpu;
	@Column(name="GR_IR_CLR_VAL_TC",length=13)
	private Double grIrClrValTC;
	@Column(name="LOCAL_CURRENCY",length=5)
	private String locCurrency;
	@Column(name="COND_NET_VAL",length=13)
	private Double condNetVal;
	@Column(name="CREATOR_NAME",length=12)
	private String creatorName;
	@Column(name="DR_CR_IND1",length=1)
	private String drCrInd1;
	@Column(name="GR_IR_CLR_VAL_PC",length=13)
	private Double grIrClrValPC;
	@Column(name="INV_AMT_PC",length=13)
	private Double invAmtPC;
	@Column(name="EXCHANGE_RATE_DIFF_AMT",length=13)
	private Double exchgRateDiffAmt;
	@Column(name="MULTIPLE_ACCOUNT_ASSIGNMENT",length=1)
	private String mulAccAss;
	@Column(name="EXCHANGE_RATE",length=9)
	private Double exchgRate;
	@Column(name="GR_BLK_BASE_UOM",length=8)
	private Long grBlkBaseUom;

}
