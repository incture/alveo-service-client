package com.ap.menabev.invoice;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.NonPoTemplateItemsDo;



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

	@Query("select distinct templateId,accountNo from NonPoTemplateItemsDo")
	public List<Object[]> selectNonPoTemplate();

	
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
