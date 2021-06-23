package com.ap.menabev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PO_SCHEDULES")
@Getter
@Setter
public class PoSchedulesDo {
    @Id
    @Column(name="PO_SCHEDULED_UUID")
	private String poScheduleUuid = UUID.randomUUID().toString();
	@Column(name="DOCUMENT_NUMBER",length=10)
	private String documentNumber;
	@Column(name="ITEM_NO",length=5)
	private String itemNo;
	@Column(name="SCHED_LINE",length=4)
	private String schedLine;
	@Column(name="DEL_DAT_CAT_EXT",length=1)
	private String delDatcatExt;
	@Column(name="DELIVERY_DATE",length=10)
	private String deliveryDate;
	@Column(name="QUANTITY",length=13)
	private Double quantity;
	@Column(name="DELIVERY_TIME",length=6)
	private Long delivTime;
	@Column(name="STAT_DATE",length=8)
	private Long statDate;
	@Column(name="PREQ_NO",length=10)
	private String preqNo;
	@Column(name="PREQ_ITEM",length=5)
	private String preqItem;
	@Column(name="PO_DATE",length=8)
	private Long poDate;
	@Column(name="DELETE_IND",length=1)
	private String deleteInd;
	@Column(name="REQ_CLOSED",length=1)
	private String reqClosed;
}
