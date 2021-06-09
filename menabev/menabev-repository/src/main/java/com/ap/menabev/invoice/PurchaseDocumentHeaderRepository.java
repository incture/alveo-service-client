package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.PurchaseDocumentHeaderDo;

@Repository
public interface PurchaseDocumentHeaderRepository extends JpaRepository<PurchaseDocumentHeaderDo, Integer> {
	@Query("select ph,pi from PurchaseDocumentHeaderDo ph inner join PurchaseDocumentItemDo pi on ph.documentNumber = pi.documentNumber where ph.documentNumber=:documentNumber")
	public List<Object[]> getAllPurchaseDocHeaderByDocNumber(@Param("documentNumber") String documentNumber);
	
	@Query(value="select distinct p.companyCode,p.currency from PurchaseDocumentHeaderDo p where p.documentNumber=:documentNumber")
	public List<Object[]> getPOHeader(@Param("documentNumber") String documentNumber);
	
//	@Query("select vendorId from PurchaseDocumentHeaderDo where documentNumber=:documentNumber")
//	public String getVendorId(@RequestParam("documentNumber") String documentNumber);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from PurchaseDocumentHeaderDo ph where ph.documentNumber=:documentNumber")
	public int deleteByDocNum(@Param("documentNumber") String poNum);
	
	@Query(value = "Select pHeaderDo from PurchaseDocumentHeaderDo pHeaderDo where pHeaderDo.documentNumber=:documentNumber")
	public PurchaseDocumentHeaderDo getByPoNumber(@Param("documentNumber")String documentNumber);
	
//	@Query(value = "select count(document_number) from APAUTOMATION.Purchase_Document_Header where DOCUMENT_NUMBER=:documentNumber", nativeQuery = true)
//	public int getCountForDocNum(@Param("documentNumber") String poNum);
	
	@Transactional
	@Modifying
	@Query("update PurchaseDocumentHeaderDo d set d.comment = :comment where d.documentNumber = :documentNumber")
	void setComment(@Param("documentNumber") String documentNumber , @Param("comment") String comment);

	@Query("select th.documentNumber from PurchaseDocumentHeaderDo th where th.documentNumber in ?1")
	public List<String> matchedPurchaseOrder(List<String> purchaseOrder);

	@Query("select th.documentNumber from PurchaseDocumentHeaderDo th where th.documentNumber in ?1 and vendor <>?2")
	public List<String> checkVendor(List<String> matchedPurchaseOrder, String vendorId);
}
