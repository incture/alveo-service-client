package com.ap.menabev.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PoHistoryDo;

public interface PoHistoryRepository extends JpaRepository<PoHistoryDo, String> {

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PoHistoryDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

}
