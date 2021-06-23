package com.ap.menabev.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_HEADER")
@Getter
@Setter
public class PurchaseDocumentHeaderDo {
	@Id
	@Column(name = "UUID")
	private String uuid = UUID.randomUUID().toString();
	@Column(name = "DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name = "COMPANY_CODE",length=4)
	private String compCode;
	@Column(name = "PURCHASE_ORGANISATION",length=4)
	private String purOrg;
	@Column(name = "PURCHASE_GROUP",length=3)
	private String purchGroup;
	@Column(name = "CURRENCY",length=5)
	private String currency;
	@Column(name = "CURRENCY_ISO",length=3)
	private String currencyISO;
	@Column(name = "DOCUMENT_DATE",length=8)
	private Long documentDate;
	@Column(name = "SUPPL_VEND",length=10)
	private String supplVend;
	@Column(name = "SUPPL_PLANT",length=4)
	private String supplPlant;
	@Column(name = "DIFF_INV",length=10)
	private String diffInv;
	@Column(name = "PO_RELEASE_INDICATOR",length=1)
	private String poRelIndicator;
	@Column(name = "PO_RELEASE_STATUS",length=8)
	private String poRelStatus;
	@Column(name = "VAT_COUNTRY",length=3)
	private String vatCountry;
	@Column(name = "LAST_CHANGED_AT",length=21)
	private Long lasChangedAt;
	@Column(name = "VENDOR_ID",length=10)
	private String vendorId;
	@Column(name = "DOC_TYPE",length=4)
	private String doctType;
	@Column(name = "DOC_CAT",length=1)
	private String docCat;
	@Column(name = "DELETION_INDICATOR",length=1)
	private String detetetionInd;
	@Column(name = "STATUS",length=1)
	private String status;
	@Column(name = "CREATE_DATE",length=8)
	private Long createDate;
	@Column(name = "CREATE_BY",length=12)
	private String createdBy;
	@Column(name = "ITM_INVL",length=5)
	private String itmInvl;
	@Column(name = "PAYMENT_TERMS",length=4)
	private String paymentTerms;
	@Column(name = "DISCOUNT1_TO",length=3)
	private Double dscnt1To;
	@Column(name = "DISCOUNT2_TO",length=3)
	private Double dscnt2To;
	@Column(name = "DISCOUNT3_TO",length=3)
	private Double dscnt3To;
	@Column(name = "DISCTPCT1",length=5)
	private Double dsctPct1;
	@Column(name = "DSCTPCT2",length=5)
	private Double dsctPct2;

}
