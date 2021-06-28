package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PoDeliveryCostHistoryDo;

public interface PoDeliveryCostHistoryRepository extends JpaRepository<PoDeliveryCostHistoryDo, String> {

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PoDeliveryCostHistoryDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

	@Query("select ph from PoDeliveryCostHistoryDo ph where ph.documentNumber= ?1")
	public List<PoDeliveryCostHistoryDo> getDeliveryCostHistory(String documentNumber);

}
