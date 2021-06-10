package com.ap.menabev.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ap.menabev.entity.MatchingHistoryDo;

public interface MatchingHistoryRepository extends JpaRepository<MatchingHistoryDo, String> {

	@Query("select th.matchScore from  MatchingHistoryDo th where th.uuid = ?1")
	Integer getMatchingScore(String uuid);

}
