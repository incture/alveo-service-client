package com.ap.menabev.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConsumtionLogicOutputDto {
	List<ItemThreeWayMatchPaylod> itemThreeWayMatchPaylod;
	List<ItemThreeWayAccAssgnPaylod> itemThreeWayAccAssgnPaylod;
}