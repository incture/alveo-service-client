package com.ap.menabev.entity;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "INVOICE_ITEM")
@Getter
@Setter
@ToString
public class InvoiceItemDo {

	
	@Id
	@Column(name = "ID")
	private String Id;
	
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InvoiceItem")
//	@GenericGenerator(name = "InvoiceItem", strategy = "com.incture.ap.sequences.InvoiceItemSequenceGenerator", parameters = {
//			@Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value = "1"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER, value = ""),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM, value = "INVOICE_ITEM_SEQ") })
	@Column(name = "ITEM_ID", nullable = false)
	private String itemId;
	
	@Column(name = "request_id", nullable = false)
	private String requestId;
	@Column(name = "ITEM_CODE")
	private String itemCode;
	@Column(name="ITEM_LIFECYCLE_STATUS")
	private String itemLifeCycleStatus;
	@Column(name = "ITEM_TEXT")
	private String itemText;
	@Column(name = "REF_DOC_CAT")
	private String refDocCat;
	@Column(name = "REF_DOC_NUM")
	private Long refDocNum;
	@Column(name = "EXT_ITEM_ID")
	private String extItemId;
	@Column(name = "CUSTOMER_ITEM_ID")
	private Integer customerItemId;
	@Column(name = "UPC_CODE")
	private String upcCode;
	@Column(name = "INV_QTY")
	private String invQty;
	@Column(name = "QTY_UOM")
	private String qtyUom;
	@Column(name = "PRICE")
	private String price;
	@Column(name = "CURRENCY")
	private String currency;
	@Column(name = "PRICING_UNIT")
	private Integer pricingUnit;
	@Column(name = "UNIT")
	private String unit;
	@Column(name = "DIS_AMT")
	private String disAmt;
	@Column(name = "DIS_PER")
	private String disPer;
	@Column(name = "DEPOSIT")
	private String deposit;
	@Column(name = "SHIPPING_AMT")
	private Integer shippingAmt;
	@Column(name = "SHIPPING_PER")
	private String shippingPer;
	@Column(name = "TAX_AMT")
	private Integer taxAmt;
	@Column(name = "TAX_PER")
	private Integer taxPer;
	@Column(name = "NET_WORTH")
	private String netWorth;
	@Column(name = "ITEM_COMMENT")
	private String itemComment;
	@Column(name = "IS_TWOWAY_MATCHED")
	private Boolean isTwowayMatched;
	@Column(name = "IS_THREEWAY_MATCHED")
	private Boolean isThreewayMatched;
	@Column(name = "MATCH_DOC_NUM")
	private Long matchDocNum;
	@Column(name = "MATCH_DOC_ITEM")
	private String matchDocItem;
	@Column(name = "MATCH_PARAM")
	private String matchParam;
	@Column(name = "UNUSED_FIELD1")
	private String unusedField1;
	@Column(name = "UNUSED_FIELD2")
	private String unusedField2;
	
	
	
	@Column(name="CREATED_BY_IN_DB")
 	private String createdByInDb;
 	
 	@Column(name="CREATED_AT_IN_DB")
	private Long createdAtInDB;
 	
 	@Column(name="UPDATED_BY")
	private String updatedBy;
 	
 	@Column(name="UPDATED_AT")
	private Long updatedAt;
 	
 	
 	@Column(name = "isSelected")
	private Boolean isSelected;
 	
 	/*PO Calculations*/
 	@Column(name="PO_AVL_QTY_OU")
	private BigDecimal poAvlQtyOU;
	
 	@Column(name="UNIT_PRICE_OPU")
	private BigDecimal unitPriceOPU;
	
 	@Column(name="PO_NET_PRICE")
	private BigDecimal poNetPrice;
	
 	@Column(name="PO_TAX_CODE")
	private String poTaxCode;
 	
 	@Column(name="PO_MATERIAL_NUM")
	private String poMaterialNum;
 	
 	@Column(name="PO_VEND_MAT")
	private String poVendMat;
 	
 	@Column(name="PO_UPC")
	private String poUPC;
 	
	@Column(name = "PO_QTY")
	private BigDecimal poQty;
	@Column(name = "PO_UOM")
	private String poUom;
 	
	
 	@Column(name="MATCHED_By")
	private String matchedBy;
 	
 	@Column(name="AMOUNT_DIFFERENCE")
 	private String amountDifference;
 	
 	@Column(name="IS_DELETED")
 	private Boolean isDeleted;

    @Column (name="IS_ACC_ASSIGNED",length=1)
    private String isAccAssigned;
//	//@Column(name = "UNUSED_FIELD1")
//	private String price2;
//	//@Column(name = "UNUSED_FIELD2")
//	private String invQty2;

}
