package com.ap.menabev.dto;

import java.util.List;

import lombok.Data;

@Data
public class OdataOutPutPayload {
	
	private  List<OdataResultObject> results;
	
}
