package com.ap.menabev.invoice;



import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceItemDo;
import com.ap.menabev.entity.InvoiceItemPkDo;



@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemDo, InvoiceItemPkDo> {

	

	@Query(value = "Select max(ITEM_ID) from INVOICE_ITEM", nativeQuery = true)
	String getItemId();
	@Query(value = "Select max(ITEM_ID) from INVOICE_ITEM where REQUEST_ID=:requestId", nativeQuery = true)
	String getItemId(String requestId);
	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId and i.isDeleted=false")
	List<InvoiceItemDo> getInvoiceItemDos(@Param("requestId") String requestId);
	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId")
	List<InvoiceItemDo> getTotalItems(@Param("requestId") String requestId);
	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId and i.isDeleted=true")
	List<InvoiceItemDo> getDeletedItems(@Param("requestId") String requestId);
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="Delete from InvoiceItemDo iDo where iDo.requestId=:requestId")
	 int  deleteTotalItems(@Param("requestId") String requestId);
	@Query(value = "select count(i.isSelected) from InvoiceItemDo i where i.requestId=:requestId and i.isSelected=true and i.isDeleted=false")
	Integer getSelectedCount(@Param("requestId") String requestId);
	@Query(value = "select count(i) from InvoiceItemDo i where i.requestId=:requestId and isDeleted=false")
	Integer getInvoiceItemCount(@Param("requestId") String requestId);
	@Query("select distinct th.refDocNum from InvoiceItemDo th where th.requestId = ?1")
	List<String> PurchaseOrderByRequestId(String requestId);
}
