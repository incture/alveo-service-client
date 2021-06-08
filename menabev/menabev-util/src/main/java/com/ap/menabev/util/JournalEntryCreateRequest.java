package com.ap.menabev.util;

import lombok.Data;

@Data
public class JournalEntryCreateRequest {
	private MessageHeaderDto MessageHeader;
	private JournalEntryDto JournalEntry;
}
