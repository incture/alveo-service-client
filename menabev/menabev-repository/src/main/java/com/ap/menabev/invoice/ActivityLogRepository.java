package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.ActivityLogDo;

@Repository
public interface ActivityLogRepository  extends JpaRepository<ActivityLogDo, String>{
	
	@Query(value = "select i from ActivityLogDo i where i.requestId=:requestId")
	List<ActivityLogDo> getAllActivityForRequestId(@Param("requestId") String requestId);

}
