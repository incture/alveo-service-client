package com.ap.menabev.invoice;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ap.menabev.entity.CommentDo;



@Repository
public interface CommentRepository extends JpaRepository<CommentDo, String> {

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("Delete from CommentDo i where i.commentId=:commentId")
	int deleteByCommentId(@Param("commentId") String commentId);

	@Query(value = "select i from CommentDo i where i.requestId=:requestId order by i.createdAt desc")
	List<CommentDo> getCommentsByRequestIdAndUser(@Param("requestId") String requestId);
	
	@Query(value = "select i from CommentDo i where i.requestId=:requestId order by i.createdAt desc")
	List<CommentDo> getAllCommentsForRequestId(@Param("requestId") String requestId);
}
