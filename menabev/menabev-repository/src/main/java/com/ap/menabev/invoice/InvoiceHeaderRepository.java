package com.ap.menabev.invoice;



import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.InvoiceHeaderDo;
import com.ap.menabev.entity.InvoiceHeaderPk;



@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeaderDo,InvoiceHeaderPk> {

	List<InvoiceHeaderDo> findByInvoiceDateBetween(String from, String to);


	@Query(value = " select ih,ii from InvoiceHeaderDo ih inner join InvoiceItemDo ii on ih.requestId=ii.requestId where ih.requestId=:requestId")
	List<Object[]> getAllInvoiceDetailsOnRequestId(@Param("requestId") String requestId);


	@Query(value = "select ih from InvoiceHeaderDo ih where ih.requestId=:requestId")
	InvoiceHeaderDo fetchInvoiceHeader(@Param("requestId") String requestId);
	
	 @Query("select e FROM InvoiceHeaderDo e WHERE e.requestId IN (:requestIds)")  
	 List<InvoiceHeaderDo> findByRequestIdIn(@Param("requestIds")List<String> requestIds);
	 
	@Query(value = "select i from InvoiceHeaderDo i where i.requestId=:requestId")
	InvoiceHeaderDo getInvoiceHeader(@Param("requestId") String requestId);
	
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value = "Delete from InvoiceHeaderDo i where i.requestId=:requestId")
	int deleteInvoiceHeader(@Param("requestId") String requestId);
	
	@Query(value = "select i from InvoiceHeaderDo i where i.taskOwner IN (:taskOwner) and i.invoiceStatus='9'")
	List<InvoiceHeaderDo> getInvoiceHeaderDocStatusByUserId(@Param("taskOwner") List<String> taskOwnerId);
	
	@Query(value = "select i from InvoiceHeaderDo i where i.taskOwner IN (:taskOwnerId) and i.requestId=:requestId and i.invoiceStatus='9' order by request_created_at desc")
	List<InvoiceHeaderDo> getInvoiceHeaderDocStatusByUserIdAndRequestId(@Param("taskOwner") List<String> taskOwnerId,@Param("requestId") String requestId);


	@Query("select i.refpurchaseDoc from InvoiceHeaderDo i where i.requestId=:requestId")
	Long getRefDocNumByReqId(@Param("requestId") String requestId);

	@Query(value = "select rDo.id from InvoiceHeaderDo rDo where rDo.sapInvoiceNumber=:sapInvoiceNumber")
    String getUuidUsingSAPInvoiceNo(@Param("sapInvoiceNumber") long sapInvoiceNumber);

	@Query(value = "select rDo from InvoiceHeaderDo rDo where rDo.id =:id")
	InvoiceHeaderDo getAllById(@Param("id")String id);
	

	
}
