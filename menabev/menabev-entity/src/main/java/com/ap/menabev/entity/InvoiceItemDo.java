package com.ap.menabev.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "INVOICE_ITEM")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@IdClass(InvoiceItemPkDo.class)
public class InvoiceItemDo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID")
	private String guid;
	@Id
	@Column(name = "ITEM_CODE")
	private String itemCode;
	@Id
	@Column(name = "REQUEST_ID", nullable = false)
	private String requestId;
	@Column(name = "EXT_ITEM_ID", length = 10)
	private String extItemId;
	@Column(name = "ITEM_TEXT", length = 50)
	private String itemText;
	@Column(name = "REF_DOC_CAT", length = 2)
	private String refDocCat;// ref_purchasedoc_category
	@Column(name = "REF_DOC_NUM", length = 10)
	private Long refDocNum;// ref_purchase_num
	@Column(name = "ARTICLE_NUM", length = 20)
	private String articleNum;
	@Column(name = "CUSTOMER_ITEM_ID", length = 20)
	private String customerItemId;
	@Column(name = "UPC_CODE", length = 50)
	private String upcCode;
	@Column(name = "INV_QTY")
	private Double invQty;
	@Column(name = "UOM", length = 5)
	private String uom;
	@Column(name = "UNIT_PRICE")
	private Double unitPrice;
	@Column(name = "CURRENCY", length = 5)
	private String currency;
	@Column(name = "PRICING_UNIT", length = 5)
	private Integer pricingUnit;
	@Column(name = "ORDER_PRICE_UNIT", length = 5)
	private String orderPriceUnit;
	@Column(name = "GROSS_PRICE")
	private Double grossPrice;
	@Column(name = "DISCOUNT_VALUE")
	private Double discountValue;
	@Column(name = "DISCOUNT_PERCENTAGE")
	private Double disPerentage;
	@Column(name = "TAX_CODE", length = 5)
	private String taxCode;
	@Column(name = "TAX_VALUE")
	private Double taxValue;
	@Column(name = "TAX_PERCENTAGE")
	private Double taxPercentage;
	@Column(name = "NET_WORTH")
	private Double netWorth;
	@Column(name = "IS_TWOWAY_MATCHED")
	private Boolean isTwowayMatched;
	@Column(name = "IS_THREEWAY_MATCHED")
	private Boolean isThreewayMatched;
	@Column(name = "MATCH_DOC_NUM", length = 20)
	private Long matchDocNum;
	@Column(name = "MATCH_DOC_ITEM", length = 10)
	private String matchDocItem;
	@Column(name = "MATCH_PARAM", length = 20)
	private String matchParam;
	@Column(name = "MATCH_SERVICE_NUM", length = 10)
	private String matchserviceNumber;
	@Column(name = "MATCH_PACKAGE_NUMBER", length = 10)
	private String matchpackageNumber;
	@Column(name = "MATCH_TYPE")
	private String matchType;// manuall or Auto posting
	@Column(name = "UPDATED_BY", length = 100)
	private String updatedBy;
	@Column(name = "UPDATED_AT")
	private Long updatedAt;
	@Column(name = "isSelected")
	private Boolean isSelected;
	@Column(name = "MATCHED_By")
	private String matchedBy;
	@Column(name = "IS_ACC_ASSIGNED")
	private Boolean isAccAssigned;
	@Column(name = "ITEM_REQUISATION_NUM", length = 10)
	private String itemRequisationNum;
	@Column(name = "REQUISATION_NUM", length = 10)
	private String requisationNum;
	@Column(name = "CONTRACT_NUM", length = 10)
	private String contractNum;
	@Column(name = "CONTRACT_ITEM", length = 10)
	private String contractItem;
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;

	// Added by Dipanjan on 21/06/2021 from Menabev AP DB Tables sheet shared by
	// Prashant Kumar
	@Column(name = "ACC_ASSIGNMENT_CAT")
	private String accAssignmentCat;
	@Column(name = "ITEM_STATUS_CODE")
	private String itemStatusCode;
	@Column(name = "ITEM_STATUS_TEXT")
	private String itemStatusText;
	@Column(name = "AVL_QTY_UOM")
	private Double alvQtyUOM;
	@Column(name = "PO_UNIT_PRICE_UOM")
	private Double poUnitPriceUOM;
	@Column(name = "AVL_QTY_OPU")
	private Double alvQtyOPU;
	@Column(name = "AVL_QTY_OU")
	private Double alvQtyOU;
	@Column(name = "PO_UNIT_PRICE_OPU")
	private Double poUnitPriceOPU;
	@Column(name = "PO_UNIT_PRICE_OU")
	private Double poUnitPriceOU;
	@Column(name = "ORDER_UNIT")
	private String orderUnit;
	// @Column(name = "IS_DELETED")
	// private String orderPriceUnit;
	@Column(name = "ORDER_UNIT_ISO")
	private String orderUnitISO;
	@Column(name = "ORDER_PRICE_UNIT_ISO")
	private String orderPriceUnitISO;
	@Column(name = "ITEM_CATEGORY")
	private String itemCategory;
	@Column(name = "ACCOUNT_ASSIGNMENT_CAT")
	private String accountAssignmentCat;
	@Column(name = "PRODUCT_TYPE")
	private String productType;
	@Column(name = "PO_MAT_NUM")
	private String poMatNum;
	@Column(name = "PO_ITEM_TEXT")
	private String poItemText;
	@Column(name = "PO_QTY_OU")
	private Double poQtyOU;
	@Column(name = "PO_QTY_OPU")
	private Double poQtyOPU;
	@Column(name = "PO_TAX_CODE")
	private String poTaxCode;
	@Column(name = "GR_FLAG")
	private Boolean grFlag;
	@Column(name = "GR_BSD_IV")
	private Boolean grBsdIv;
	@Column(name = "SRV_BSD_IV")
	private Boolean srvBsdIv;
	@Column(name = "IV_FLAG")
	private Boolean ivFlag;
	@Column(name = "CONV_NUM_1")
	private Integer convNum1;
	@Column(name = "CONV_DEN_1")
	private Integer convDen1;
	@Column(name = "GR_NON_VAL_IND")
	private Boolean grNonValInd;
	@Column(name = "DISTRIBUTION_IND" , length  = 1)
	private String distributionInd;
	@Column(name = "PARTIAL_INV_IND" , length = 1)
	private String partialInvInd;
}
