package com.ap.menabev.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InvoiceChangeIndicator {
	private boolean headerChange;
	private boolean itemChange;
	private boolean costAllocationChange;
	private boolean attachementsChange;
	private boolean commentChange;
	private boolean activityLog;
}
