package com.ap.menabev.invoice;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.NonPoTemplateDo;

import java.util.List;


@Repository
public interface NonPoTemplateRepository extends JpaRepository<NonPoTemplateDo, Integer> {
	@Query("select td from NonPoTemplateDo td where  td.templateId=:templateId")
	public NonPoTemplateDo fetchNonPoTemplateDo(@Param("templateId") String templateId);
	
	//@Query(value = "select * from APAUTOMATION.NON_PO_TEMPLATE order by created_At desc limit :limit offset :offset", nativeQuery = true)
//	public List<NonPoTemplateDo> fetchAll(@Param("limit")int limit,@Param("offset") int offset);
	@Query(value = "select * from APAUTOMATION.NON_PO_TEMPLATE order by created_At desc", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAll();
	
	@Query(value = "select * from APAUTOMATION.NON_PO_TEMPLATE order by template_id desc", nativeQuery = true)
	public List<NonPoTemplateDo> fetchTemplateId();

	@Modifying
	@Query("Delete from NonPoTemplateDo npd where  npd.templateId=:templateId")
	public Integer deleteNonPoTemplateDo(@Param("templateId") String templateId);
}
