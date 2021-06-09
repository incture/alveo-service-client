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



@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItemDo, String> {

	@Query(value = "select * from APAUTOMATION.INVOICE_ITEM order by ITEM_ID limit :limit offset :offset ", nativeQuery = true)
	List<InvoiceItemDo> findAllByLimit(@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = "Select max(ITEM_ID) from APAUTOMATION.INVOICE_ITEM", nativeQuery = true)
	String getItemId();

	@Query(value = "select id from InvoiceItemDo id where id.requestId=:requestId and id.itemId=:itemId ")
	InvoiceItemDo fetchInvoiceItemDo(@Param("requestId") String requestId, @Param("itemId") String itemId);

	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId and i.isDeleted=false")
	List<InvoiceItemDo> getInvoiceItemDos(@Param("requestId") String requestId);
	
	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId")
	List<InvoiceItemDo> getTotalItems(@Param("requestId") String requestId);
	
	@Query(value = "select i from InvoiceItemDo i where i.requestId=:requestId and i.isDeleted=true")
	List<InvoiceItemDo> getDeletedItems(@Param("requestId") String requestId);

	@Query(value = "select i from InvoiceItemDo i where i.invQty=:inQty  and  i.refDocNum=:refDocNum and i.requestId=:reqId")
	InvoiceItemDo getInvoiceItem(@Param("inQty") String inQty, @Param("refDocNum") Long refDocNum,
			@Param("reqId") String reqId);

	
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="Delete from InvoiceItemDo iDo where iDo.requestId=:requestId")
	 int  deleteTotalItems(@Param("requestId") String requestId);
	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceItemDo i SET i.isThreewayMatched=false WHERE i.matchDocItem=:matchDocItem  AND  i.matchDocNum=:matchDocNum AND i.requestId=:reqId")
	int updateInvItemFalse(@Param("matchDocItem") String matchDocItem, @Param("matchDocNum") Long matchDocNum,
			@Param("reqId") String reqId);

	/*
	 * invoiceItemDto.get(i).setIsTwowayMatched(false);
	 * invoiceItemDto.get(i).matchDocNum(null);
	 * invoiceItemDto.get(i).isSelected(false);
	 * invoiceItemDto.get(i).matchDocItem(null);
	 * invoiceItemDto.get(i).matchParam(null);
	 * invoiceItemDto.get(i).matchedBy(ApplicationConstants.NOT_MATCHED);
	 * invoiceItemDto.get(i).setItemLifeCycleStatus(ApplicationConstants.
	 * TWO_WAY_MISMATCH); invoiceItemDto.get(i).setUnitPriceOPU(null);
	 * invoiceItemDto.get(i).setPoNetPrice(null);
	 * invoiceItemDto.get(i).setPoTaxCode(null);
	 * invoiceItemDto.get(i).setPoAvlQtyOU(null);
	 * invoiceItemDto.get(i).setShortText(null);
	 * invoiceItemDto.get(i).setPoMaterialNum(null);
	 * invoiceItemDto.get(i).setPoVendMat(null);
	 * invoiceItemDto.get(i).setPoUPC(null);
	 * invoiceItemDto.get(i).setAmountDifference("0.000");
	 */
	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "Update InvoiceItemDo i set i.isTwowayMatched=:isTwowayMatched , i.matchDocItem=:matchDocItem , i.isSelected=:isSelected , i.matchDocNum=:matchDocNum, "
			+ " i.matchParam=:matchParam , i.matchedBy=:matchedBy , i.itemLifeCycleStatus=:itemLifeCycleStatus , i.unitPriceOPU=:unitPriceOPU , i.poAvlQtyOU=:poAvlQtyOU ,"
			+ " i.poNetPrice=:poNetPrice , i.poTaxCode=:poTaxCode "
			+ ", i.poMaterialNum=:poMaterialNum , i.poVendMat=:poVendMat  , i.poUPC=:poUPC , i.amountDifference=:amountDifference,i.qtyUom=:qtyUom where "
			+ "  i.requestId=:requestId and i.itemId=:itemId")
	int updateInvoiceItem(@Param("isTwowayMatched") Boolean isTwowayMatched, @Param("matchDocItem") String matchDocItem,
			@Param("isSelected") Boolean isSelected, @Param("matchDocNum") Long matchDocNum,
			@Param("matchParam") String matchParam, @Param("matchedBy") String matchedBy,
			@Param("itemLifeCycleStatus") String itemLifeCycleStatus, @Param("unitPriceOPU") BigDecimal unitPriceOPU,
			@Param("poAvlQtyOU") BigDecimal poAvlQtyOU, @Param("poNetPrice") BigDecimal poNetPrice,
			@Param("poTaxCode") String poTaxCode, @Param("poMaterialNum") String poMaterialNum,
			@Param("poVendMat") String poVendMat, @Param("poUPC") String poUPC,
			@Param("amountDifference") String amountDifference,
			@Param("qtyUom")String qtyUom,
			@Param("requestId") String requestId,
			@Param("itemId") String itemId

	);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceItemDo i SET i.isThreewayMatched=true WHERE i.matchDocItem=:matchDocItem  AND  i.matchDocNum=:matchDocNum AND i.requestId=:reqId")
	int updateInvItemTrue(@Param("matchDocItem") String matchDocItem, @Param("matchDocNum") Long matchDocNum,
			@Param("reqId") String reqId);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceItemDo i SET i.itemLifeCycleStatus=:lcs WHERE i.matchDocItem=:matchDocItem  AND  i.matchDocNum=:matchDocNum AND i.requestId=:reqId")
	int updateInvItemLifeCycleStatus(@Param("lcs") String statusCode, @Param("matchDocItem") String matchDocItem,
			@Param("matchDocNum") Long matchDocNum, @Param("reqId") String reqId);

	@Query(value = "select count(i.isSelected) from InvoiceItemDo i where i.requestId=:requestId and i.isSelected=true and i.isDeleted=false")
	Integer getSelectedCount(@Param("requestId") String requestId);

	@Query(value = "select count(i) from InvoiceItemDo i where i.requestId=:requestId and isDeleted=false")
	Integer getInvoiceItemCount(@Param("requestId") String requestId);

	@Transactional(TxType.REQUIRES_NEW)
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE InvoiceItemDo i SET i.itemLifeCycleStatus=:lcs WHERE i.requestId=:reqId")
	int updateInvoiceItemLifeCycleStatus(@Param("lcs") String statusCode, @Param("reqId") String requestId);

	@Query(value = "select count(id.itemLifeCycleStatus) from InvoiceItemDo id where id.requestId=:requestId  and id.isDeleted=false and id.itemLifeCycleStatus='16'")
	int getGrnComplete(@Param("requestId") String requestId);

	@Query(value = "select count(id.itemLifeCycleStatus) from InvoiceItemDo id where id.requestId=:requestId  and id.isDeleted=false and id.itemLifeCycleStatus='12'")
	int getPartialComplete(@Param("requestId") String requestId);

	@Query("select distinct th.refDocNum from InvoiceItemDo th where th.requestId = ?1")
	List<String> PurchaseOrderByRequestId(String requestId);

//	@Transactional(TxType.REQUIRES_NEW)
//	@Modifying(clearAutomatically = true)
//	@Query(value = "UPDATE InvoiceItemDo i SET i.isThreewayMatched=false, i.isSelected=false, i.isTwowayMatched=false, i.matchDocItem=null,"
//			+ " i.matchParam=null, i.matchDocNum=null  WHERE i.matchDocItem=:matchDocItem  AND  i.matchDocNum=:matchDocNum AND i.requestId=:reqId")
//	int updateInvoiceItemForDeletePO(@Param("requestId") String requestId, @Param("refDocNum") Long refDocNum);
}
