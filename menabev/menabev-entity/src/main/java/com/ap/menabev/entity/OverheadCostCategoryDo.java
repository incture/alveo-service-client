package com.ap.menabev.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Table(name = "OVERHEAD_COSTCATEGORY")
@Getter
@Setter
public class OverheadCostCategoryDo 
{
	@Id
	@Column(name = "ID")	
	private String id;
	
	@Column(name="OVERHEAD_COSTCATEGORY_CODE")
	private String overheadCostCategoryCode;
	
	@Column(name="OVERHEADCOSTCATEGORY_TEXT")
	private String overheadCostCategoryText;

}
