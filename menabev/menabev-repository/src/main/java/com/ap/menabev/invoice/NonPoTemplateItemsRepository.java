package com.ap.menabev.invoice;



import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.NonPoTemplateItemsDo;



@Transactional
@Repository
public interface NonPoTemplateItemsRepository extends JpaRepository<NonPoTemplateItemsDo, Integer> {
	@Query("select id from NonPoTemplateItemsDo id where  id.templateId=:templateId and id.itemId=:itemId")
	public NonPoTemplateItemsDo fetchNonPoTemplateItemsDo(@Param("templateId") String templateId,
			@Param("itemId") String itemId);
	
	@Query("select id from NonPoTemplateItemsDo id where  id.templateId=:templateId")
	public List<NonPoTemplateItemsDo> fetchNonPoTemplateItemsDo(@Param("templateId") String templateId);
  
	
	
	@Query("select count(id) from NonPoTemplateItemsDo id where id.templateId=:templateId")
	public Integer countOfNonPoTemplateItems(@Param("templateId") String templateId);
	
	@Modifying
	@Query("Delete from NonPoTemplateItemsDo id where  id.templateId=:templateId and id.itemId=:itemId")
	public Integer deleteNonPoTemplateItemsDo(@Param("templateId") String templateId,
			@Param("itemId") String itemId);
	
//	@Modifying
//	@Query("Delete from NonPoTemplateItemsDo id where  id.templateId=:templateId")
//	public Integer deleteNonPoTemplateItemsDo(@Param("templateId") String templateId);
	
	@Modifying
	@Query("Delete from NonPoTemplateItemsDo npd where  npd.templateId in ?1")
	Integer deleteNonPoTemplateItemsDo(List<String> templateId);

	
	String selectNonPoTemplateQuery = "select distinct  t.TEMPLATE_ID, "
            + "case when (select count(distinct(t2.ACCOUNT_NO))  from NON_PO_TEMPLATE_ITEMS t2 where "
            + "t2.TEMPLATE_ID =t.TEMPLATE_ID) =1 then  (t.ACCOUNT_NO) "
            + "when (select count(distinct(t2.ACCOUNT_NO))  from NON_PO_TEMPLATE_ITEMS "
            + "t2 where t2.TEMPLATE_ID =t.TEMPLATE_ID) >1 then '*' "
            + " end as accountNo, "
            + "(select TEMPLATE_NAME from NON_PO_TEMPLATE where TEMPLATE_ID =t.TEMPLATE_ID) "
            + " as TemplateName "
            + "from NON_PO_TEMPLATE_ITEMS t "
            + "order by t.TEMPLATE_ID desc";
	
//	@Query(value="select DISTINCT TEMPLATE_ID,ACCOUNT_NO from "
//			+ "MENABEVD.NON_PO_TEMPLATE_ITEMS ORDER BY TEMPLATE_ID DESC",nativeQuery=true)
	@Query(value=selectNonPoTemplateQuery,nativeQuery=true)
	public List<Object[]> selectNonPoTemplate();

	
//	String getAccountQuery = "select "
//            + "case when (select count(distinct(t2.ACCOUNT_NO))  from NON_PO_TEMPLATE_ITEMS t2 where "
//            + "t2.TEMPLATE_ID =?1) =1 then  (t.ACCOUNT_NO) "
//            + "when (select count(distinct(t2.ACCOUNT_NO))  from NON_PO_TEMPLATE_ITEMS "
//            + "t2 where t2.TEMPLATE_ID =?1) >1 then '*' "
//            + " end as accountNo, "
//            + " "
//            + ""
//            + "from NON_PO_TEMPLATE_ITEMS t "
//            + " where t.TEMPLATE_ID = ?1 ";
//	
//	@Query(value=getAccountQuery,nativeQuery=true)
//	public String getAccountNo(String templateId);

	
	
	
	//	public default NonPoTemplateItemsDo importDo(NonPoTemplateItemsDto dto) {
//		NonPoTemplateItemsDo nonPoTemplateItemsDo = new NonPoTemplateItemsDo();
//		nonPoTemplateItemsDo.setAsset(dto.getAsset());
//		nonPoTemplateItemsDo.setCostCenter(dto.getCostCenter());
//		nonPoTemplateItemsDo.setGlAccount(dto.getGlAccount());
//		nonPoTemplateItemsDo.setInternalOrder(dto.getInternalOrder());
//		nonPoTemplateItemsDo.getNonPoTemplateItemsPKId().setItemId(dto.getItemId());
//		nonPoTemplateItemsDo.getNonPoTemplateItemsPKId().setTemplateId(dto.getTemplateId());
//		nonPoTemplateItemsDo.setSubAsset(dto.getSubAsset());
//		nonPoTemplateItemsDo.setWbsElement(dto.getWbsElement());
//		return nonPoTemplateItemsDo;
//	}
//
//	public default NonPoTemplateItemsDto exportDo(NonPoTemplateItemsDo ndo) {
//		NonPoTemplateItemsDto nonPoTemplateItemsDto = new NonPoTemplateItemsDto();
//		nonPoTemplateItemsDto.setAsset(ndo.getAsset());
//		nonPoTemplateItemsDto.setCostCenter(ndo.getCostCenter());
//		nonPoTemplateItemsDto.setGlAccount(ndo.getGlAccount());
//		nonPoTemplateItemsDto.setInternalOrder(ndo.getInternalOrder());
//		nonPoTemplateItemsDto.setItemId(ndo.getNonPoTemplateItemsPKId().getItemId());
//
//		nonPoTemplateItemsDto.setTemplateId(ndo.getNonPoTemplateItemsPKId().getTemplateId());
//		nonPoTemplateItemsDto.setSubAsset(ndo.getSubAsset());
//		nonPoTemplateItemsDto.setWbsElement(ndo.getWbsElement());
//		return nonPoTemplateItemsDto;
//	}
//
}
