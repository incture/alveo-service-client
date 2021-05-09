package com.ap.menabev.entity;



import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import com.ap.menabev.dto.PurchaseDocumentItemPkDto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Purchase_Document_Item")
@IdClass(PurchaseDocumentItemPkDto.class)
@Getter
@Setter
@ToString
public class PurchaseDocumentItemDo implements Serializable{

	private static final long serialVersionUID = 3875994822103092792L;
	
	@Id
	@Column(name = "DOCUMENT_NUMBER")
	private String documentNumber;
	
	@Id
	@Column(name = "DOCUMENT_ITEM")
	private String documentItem;//Item_Id
	
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PurchaseDocItem")
//	@GenericGenerator(name = "PurchaseDocItem", strategy = "com.incture.ap.sequences.PurchaseDocumentItemSequenceGenerator", parameters = {
//			@Parameter(name = InvoiceHeaderSequenceGenerator.INCREMENT_PARAM, value = "1"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.VALUE_PREFIX_PARAMETER, value = "PDI"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d"),
//			@Parameter(name = InvoiceHeaderSequenceGenerator.SEQUENCE_PARAM, value = "PURCHASE_DOCUMENT_ITEM_SEQ") })
	@Column(name = "PURCHASE_DOCUMENT_ITEM_ID")
	private String purchaseDocumentItemId;

	@Column(name = "DELETION_IND")
	private Boolean deletionInd;
	
	@Column(name = "SHORT_TEXT")
	private String shortText;//Description
	
	@Column(name = "MATERIAL_NUM")
	private String materialNum;
	@Column(name = "VEND_MAT")
	private String vendMat;
	@Column(name = "PLANT")
	private String plant;
	@Column(name = "STG_LOC")
	private String stgLoc;
	@Column(name = "PO_QTY")
	private BigDecimal poQty;
	@Column(name = "ORDER_UNIT")
	private String orderUnit;
	@Column(name = "ORDER_PRICE_UNIT")
	private String orderPriceUnit;//UOM
	@Column(name = "CONV_NUM1")
	private BigDecimal convNum1;
	@Column(name = "CONV_DEN1")
	private BigDecimal convDen1;
	@Column(name = "NET_PRICE")
	private BigDecimal netPrice;
	@Column(name = "PRICE_UNIT")
	private String priceUnit;
	@Column(name = "TAX_CODE")
	private String taxCode;
	@Column(name = "NO_MORE_GR")
	private Boolean noMoreGr;
	@Column(name = "ITEM_CAT")
	private String itemCat;
	@Column(name = "ACCTASSCAT")
	private String acctasscat;
	@Column(name = "GR_IND")
	private Boolean grInd;
	@Column(name = "IR_IND")
	private Boolean irInd;
	@Column(name = "GR_BASEDIV")
	private Boolean grBasediv;
	@Column(name = "NET_WEIGHT")
	private BigDecimal netWeight;
	@Column(name = "NET_VOLUME")
	private BigDecimal netVolume;
	@Column(name = "BASE_UNIT")
	private Integer baseUnit;
	
	@Column(name="UPC_CODE")
	private String upcCode;
	
	@Column(name="DELIVERED_QTY")
	private String deliveredQty;
	
	@Column(name="INVOICE_QTY")
    private String invoiceQty;
	
	@Column(name="TAX_PERCENTAGE")
    private String taxPercentage;
	
	@Column(name="TOTAL_PRICE")
    private String totalPrice;//net Price
	
	@Column(name="TAX_AMOUNT")
    private String taxAmount;
	
	@Column(name="DELIVERY_DATE")
    private Date deliveryDate;
	
	
}
