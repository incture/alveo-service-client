package com.ap.menabev.invoice;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.CostAllocationDo;



@Repository
public interface CostAllocationRepository extends JpaRepository<CostAllocationDo, Integer> {
	@Query(value = "select count(cd.itemId) from CostAllocationDo cd where cd.requestId=:requestId and cd.itemId=:itemId")
	public Integer getItemCount(@Param("requestId") String requestId,
			@Param("itemId") Integer itemId);
	@Query(value="select count(cd.itemId) from CostAllocationDo cd where cd.requestId=:requestId")
	public Integer getItemCountTotal(@Param("requestId") String requestId);
	
	@Query(value = "select cd from CostAllocationDo cd where cd.requestId=:requestId and cd.itemId=:itemId")
    public CostAllocationDo fetchCostAllocationDo(@Param("requestId") String requestId,
			@Param("itemId") Integer itemId);
	
	@Query(value = "select cd from CostAllocationDo cd where cd.requestId=:requestId")
    public List<CostAllocationDo> getAllOnRequestId(@Param("requestId") String requestId);
	
	@Modifying
	@Query("Delete from CostAllocationDo cd where cd.requestId=:requestId")
	public Integer deleteCostAllocationDo(@Param("requestId") String requestId);
	
}