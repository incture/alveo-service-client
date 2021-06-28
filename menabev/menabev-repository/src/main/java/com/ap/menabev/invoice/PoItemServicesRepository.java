package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PoItemServicesDo;

public interface PoItemServicesRepository extends JpaRepository<PoItemServicesDo, String> {

	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PoItemServicesDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

	@Query("select ser from PoItemServicesDo ser where ser.documentNumber = ?1")
	public List<PoItemServicesDo> getServices(String documentNumber);

}
