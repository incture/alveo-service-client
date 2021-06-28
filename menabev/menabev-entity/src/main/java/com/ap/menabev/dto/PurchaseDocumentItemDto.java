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
	private Boolean noMoreGr;//
	private Boolean finalInvInd;//
	private String itemCategory;
	private String accountAssCat;
	private String distribution;
	private String partInv;
	private String grInd;
	private Boolean gr_non_val;//
	private Boolean irInd;//
	private Boolean grBsdIVInd;//
	private Boolean srvBsdIVInd;//
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
	private Boolean delivComplete;//
	private String partDelv;
	private Long itemChangedAt;
	private String productType;//TODO
	private String InterArticleNum;//TODO 18
	private List<PoItemServicesDto> poItemServices;
	private List<PoItemAccountAssignDto> poAccountAssigment;
	private List<PoSchedulesDto> Schedules;
}
