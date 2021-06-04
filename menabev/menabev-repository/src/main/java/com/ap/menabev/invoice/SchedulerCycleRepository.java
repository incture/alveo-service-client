package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.SchedulerCycleDo;

@Repository
public interface SchedulerCycleRepository extends JpaRepository<SchedulerCycleDo, String> {

	@Query("select th from SchedulerCycleDo th  where  th.schedulerRunID = ?1")
	List<SchedulerCycleDo> getAllBySchedulerRunId(String schedulerRunID);

}
