package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.ReasonForRejectionDo;

@Repository
public interface ReasonForRejectionRepository extends JpaRepository<ReasonForRejectionDo, String> {

	@Query(value = "Select reasonforRej from ReasonForRejectionDo where rejectionText=:reason")
	String getReasonForId(@Param("reason") String reason);
	
	@Query(value = "Select rejectionText from ReasonForRejectionDo where reasonforRej=:reasonforRej and languageId=:languageId")
	String getRejectionTextbyRejectionId(@Param("reasonforRej") String reasonforRej,@Param("languageId") String languageId);
	
	@Query(value = "Select rDo from ReasonForRejectionDo rDo where rDo.languageId=:languageId")
	List<ReasonForRejectionDo>getRejectionByLangKey(@Param("languageId") String languageId);
}
