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

	@Query(value = "SELECT max(NO_OF_EMAIL_PICKED),max(NO_OF_EMAILS_READ_SUCCESSFULLY), max(NO_OF_ATTACHMENTS),max(NO_OF_PDFS),max(NO_OF_JSON_FILES),SCHEDULER_RUN_ID FROM SCHEDULER_CYCLE where  SCHEDULER_RUN_ID=?1 group by SCHEDULER_RUN_ID", nativeQuery = true)
	List<Object[]> getMaxResultsBySchedulerRunId(String schedulerRunID);

}
