package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PoItemAccountAssignDo;
import com.ap.menabev.entity.PoItemServicesDo;

public interface PoItemAccountAssignRepository extends JpaRepository<PoItemAccountAssignDo, String> {

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PoItemAccountAssignDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

	@Query("select ph from PoItemAccountAssignDo ph where ph.documentNumber= ?1")
	public List<PoItemAccountAssignDo> getAccountAssignmentServices(String documentNumber);


}
