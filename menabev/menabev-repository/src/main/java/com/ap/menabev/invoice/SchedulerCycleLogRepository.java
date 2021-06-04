package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerCycleLogDo;

@Repository
public interface SchedulerCycleLogRepository extends JpaRepository<SchedulerCycleLogDo, String> {

	@Query("select th from SchedulerCycleLogDo th  where  th.cycleID= ?1")
	List<SchedulerCycleLogDo> findAllById(String schedulerCycleID);

	
	

}
