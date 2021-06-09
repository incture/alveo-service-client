package com.ap.menabev.invoice;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.InvoiceItemAcctAssignmentDo;



@Repository
public interface InvoiceItemAcctAssignmentRepository extends JpaRepository<InvoiceItemAcctAssignmentDo, String> {
	@Query(value = "Select max(SERIAL_NO) from APAUTOMATION.INVOICE_ITEM_ACCOUNT_ASSIGNMENT where REQUEST_ID=:requestId"
			+ " and ITEM_ID=:itemId", nativeQuery = true)
	String getSerialNo(@Param("requestId") String requestId, @Param("itemId") String itemId);

	@Query(value = "select iDo from InvoiceItemAcctAssignmentDo iDo where  iDo.requestId=:requestId "
			+ "and iDo.itemId=:itemId and iDo.isDeleted=false")
	List<InvoiceItemAcctAssignmentDo> get(@Param("requestId") String requestId,
			@Param("itemId") String itemId);
	
	@Query(value = "select iDo from InvoiceItemAcctAssignmentDo iDo where  iDo.requestId=:requestId "
			+" and iDo.isDeleted=false")
	List<InvoiceItemAcctAssignmentDo> getByRequestId(@Param("requestId") String requestId);
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="Delete from InvoiceItemAcctAssignmentDo iDo where iDo.requestId=:requestId and iDo.itemId=:itemId")
	public int deleteByRequestIdItemId(@Param("requestId") String requestId,@Param("itemId") String itemId);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="Delete from InvoiceItemAcctAssignmentDo iDo where iDo.requestId=:requestId")
	public int deleteByRequestIdItemId(@Param("requestId") String requestId);
	
	

}
