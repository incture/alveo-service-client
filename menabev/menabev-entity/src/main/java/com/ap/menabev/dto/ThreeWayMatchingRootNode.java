package com.ap.menabev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThreeWayMatchingRootNode {
	@JsonProperty("Vendor")
	private String vendor;
	@JsonProperty("ToMatchInput")
	private ToMatchInput toMatchInput;
	@JsonProperty("ToMatchOutput")
	private ToMatchOutput toMatchOutput;
}
