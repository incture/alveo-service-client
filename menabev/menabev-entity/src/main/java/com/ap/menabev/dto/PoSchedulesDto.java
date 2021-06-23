package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PoSchedulesDto {

	private String poScheduleUuid;
	private String documentNumber;
	private String itemNo;
	private String schedLine;
	private String delDatcatExt;
	private String deliveryDate;
	private Double quantity;
	private Long delivTime;
	private Long statDate;
	private String preqNo;
	private String preqItem;
	private Long poDate;
	private String deleteInd;
	private String reqClosed;
}
