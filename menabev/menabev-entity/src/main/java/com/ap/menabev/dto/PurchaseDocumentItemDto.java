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
public class PurchaseDocumentItemDto {

	private String itemUuid;
	private String documentNumber;
	private String documentItem;
	private String deleteInd;
	private String shortText;
	private String material;
	private String materialExt;
	private String eMaterial;
	private String plant;
	private String storageLoc;
	private String trackingNo;
	private String materialGrp;
	private String infoRecord;
	private String vendMat;
	private Double quantity;
	private String poUnit;
	private String poUnitISO;
	private String orderPriceUnit;
	private String orderPriceUnitISO;
	private Double convNum1;
	private Double convDen1;
	private Double netPrice;
	private Double priceUnit;
	private Double grPrTime;
	private String taxCode;
	private String noMoreGr;
	private String finalInvInd;
	private String itemCategory;
	private String accountAssCat;
	private String distribution;
	private String partInv;
	private String grInd;
	private String gr_non_val;
	private String irInd;
	private String grBsdIVInd;
	private String srvBsdIVInd;
	private String agreement;
	private String agreeemntItm;
	private String taxJurCode;
	private String supplVendor;
	private String preqNum;
	private String preqItem;
	private String contractNum;
	private String contractItm;
	private String refDocNum;
	private String refDocItem;
	private String delivComplete;
	private String partDelv;
	private Long itemChangedAt;
	private String productType;//TODO
	private String InterArticleNum;//TODO 18
	private List<PoItemServicesDto> poItemServices;
	private List<PoItemAccountAssignDto> poAccountAssigment;
	private List<PoSchedulesDto> Schedules;
}
