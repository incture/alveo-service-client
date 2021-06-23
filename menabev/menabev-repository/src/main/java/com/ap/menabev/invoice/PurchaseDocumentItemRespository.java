package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PurchaseDocumentItemDo;

@Repository
public interface PurchaseDocumentItemRespository extends JpaRepository<PurchaseDocumentItemDo, String> {

	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PurchaseDocumentItemDo ph where ph.documentNumber= ?1")
	public Integer deleteByDocumentNumber(String documentNumber);

//	@Query("select pd from PurchaseDocumentItemDo pd where pd.documentNumber=:refDocNum")
//	public List<PurchaseDocumentItemDo> getPurchaseDocumentItem(@Param("refDocNum") String refDocNum);
//
//	@Query(value = "select distinct p.poQty,p.convNum1,p.convDen1,p.netPrice,p.itemCat from  PurchaseDocumentItemDo p where p.documentItem=:documentItem and p.documentNumber=:documentNumber")
//	List<Object[]> getPOItem(@Param("documentItem") String string, @Param("documentNumber") String string2);
//
//	@Query("select pd from PurchaseDocumentItemDo pd where pd.documentNumber=:DocNum and pd.documentItem=:DocItem")
//	public List<PurchaseDocumentItemDo> getDetailsByDocNumDocCode(@Param("DocNum") String DocNum,
//			@Param("DocItem") String DocItem);
//
//	@Transactional
//	@Modifying(clearAutomatically=true)
//	@Query("Delete from PurchaseDocumentItemDo p where p.documentNumber=:documentNumber")
//	public int deleteByDocNum(@Param("documentNumber") String poNum);
//
//	@Transactional
//	@Modifying
//	@Query(value = "Select itemDo from PurchaseDocumentItemDo itemDo where itemDo.documentNumber=:documentNumber")
//	public List<PurchaseDocumentItemDo> getByPoNumber(@Param("documentNumber") String documentNumber);
//	
////	@Query(value = "select count(document_number) from APAUTOMATION.Purchase_Document_Item where DOCUMENT_NUMBER=:documentNumber", nativeQuery = true)
////	public int getCountForDocNum(@Param("documentNumber") String poNum);
	
	

}
