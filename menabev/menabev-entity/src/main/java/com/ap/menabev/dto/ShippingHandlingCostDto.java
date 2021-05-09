package com.ap.menabev.dto;



import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ShippingHandlingCostDto 
{
	private String id=UUID.randomUUID().toString();	
    private String requestId;
	private String companyShipToAddress;
	private String description;
	private String company;
	private String partnerCode;
}
