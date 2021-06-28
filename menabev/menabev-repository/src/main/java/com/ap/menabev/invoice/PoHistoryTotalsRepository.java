package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PoHistoryDo;
import com.ap.menabev.entity.PoHistoryTotalsDo;

public interface PoHistoryTotalsRepository extends JpaRepository<PoHistoryTotalsDo, String> {

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PoHistoryTotalsDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

	@Query("select ph from PoHistoryTotalsDo ph where ph.documentNumber= ?1")
	public List<PoHistoryTotalsDo> getHistoryTotals(String documentNumber);

}
