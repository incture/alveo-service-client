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
	@Query(value = "select * from MENABEVD.NON_PO_TEMPLATE order by created_At desc", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAll();
	
//	@Query(value = "select * from APAUTOMATION.NON_PO_TEMPLATE order by template_id desc", nativeQuery = true)
//	public List<NonPoTemplateDo> fetchTemplateId();

	@Query(value = "select \"NON_PO_TEMPLATE_ITEMS_ID\".NEXTVAL from dummy", nativeQuery = true)
    public String fetchTemplateId();
	
//	@Modifying
//	@Query("Delete from NonPoTemplateDo npd where  npd.templateId=:templateId")
//	public Integer deleteNonPoTemplateDo(@Param("templateId") String templateId);
	@Modifying
	@Query("Delete from NonPoTemplateDo npd where  npd.templateId in ?1")
	Integer deleteNonPoTemplateDo(List<String> templateId);

	@Query("select templateName from NonPoTemplateDo where  templateId=:templateId")
	public String getById(@Param("templateId") String templateId);

	@Query("select templateName from NonPoTemplateDo where  templateName=:templateName")
	public String getByName(@Param("templateName") String templateName);

	@Query("select templateName from NonPoTemplateDo where  templateName=?1 and templateId <> ?2 ")
	public String checkTemplateNameIsAvaible(String templateName, String templateId);

	@Query(value = "select * from NON_PO_TEMPLATE where Template_Id in ?1 order by created_At desc", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAllByTemplateId(List<String> getTemplateId);

	@Query(value = "select * from NON_PO_TEMPLATE order by created_At desc limit ?1 offset ?2", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAllByLimitOffset(int limit, int offset);

	@Query(value = "select * from NON_PO_TEMPLATE where Template_Id in ?1 order by created_At desc limit ?2 offset ?3", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAllByTemplateIdWithLimitandOffset(List<String> getTemplateId, int limit,int offset);

	@Query(value = "select count(*) from NON_PO_TEMPLATE  " ,nativeQuery = true )
	public Integer getCount();

	@Query(value = "select * from NON_PO_TEMPLATE where Template_Name in ?1 order by created_At desc limit ?2 offset ?3", nativeQuery = true)
	public List<NonPoTemplateDo> fetchAllByTemplateIdWithLimitandOffsetandtemplateName(List<String> getTemplateId,
			Integer limit, Integer offset);
}
