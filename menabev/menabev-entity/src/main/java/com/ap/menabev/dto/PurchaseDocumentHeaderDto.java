package com.ap.menabev.dto;



import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
public class PurchaseDocumentHeaderDto {

	private String uuid;
	private String documentNumber;
	private String compCode;
	private String purOrg;
	private String purchGroup;
	private String currency;
	private String currencyISO;
	private Long documentDate;
	private String supplVend;
	private String supplPlant;
	private String diffInv;
	private String poRelIndicator;
	private String poRelStatus;
	private String vatCountry;
	private Long lasChangedAt;
	private String vendorId;
	private String doctType;
	private String docCat;
	private String detetetionInd;
	private String status;
	private Long createDate;
	private String createdBy;
	private String itmInvl;
	private String paymentTerms;
	private Double dscnt1To;
	private Double dscnt2To;
	private Double dscnt3To;
	private Double dsctPct1;
	private Double dsctPct2;
	private List<PurchaseDocumentItemDto> poItem;
	private List<PoHistoryDto> poHistory;
	private List<PoHistoryTotalsDto> poHistoryTotals;
	private List<PoDeliveryCostHistoryDto> poDeliveryCostHistory;
}
