package com.ap.menabev.invoice;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.AttachmentDo;




@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentDo, String> {

	@Query(value = "select i from AttachmentDo i where i.requestId=:requestId")
	List<AttachmentDo> getAllAttachmentsForRequestId(@Param("requestId") String requestId);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query("Delete from AttachmentDo i where i.attachmentId=:attachmentId")
	public int deleteByAttachmentId(@Param("attachmentId") String attachmentId);
	
	
	@Modifying(clearAutomatically=true)
	@Query("Delete from AttachmentDo i where i.requestId=:requestId")
	public int deleteByRequestId(@Param("requestId") String requestId);
	
	@Query(value = "select i from AttachmentDo i where i.requestId=:requestId and i.master=true")
	AttachmentDo getAttachmentsForRequestId(@Param("requestId") String requestId);
	
}
