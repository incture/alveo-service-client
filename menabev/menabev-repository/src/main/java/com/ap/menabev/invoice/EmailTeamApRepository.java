package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.EmailTeamAPDo;
@Repository
public interface EmailTeamApRepository extends JpaRepository<EmailTeamAPDo, String> {
	@Query("select eDo from EmailTeamAPDo eDo where eDo.configurationId=:configurationId and Upper(eDo.actionType)=Upper(:actionType)")
	public List<EmailTeamAPDo> getEmailTeamAP(@Param("configurationId") String configurationId , @Param("actionType") String actionType);
	
	
	
	@Query("select eDo from EmailTeamAPDo eDo where eDo.configurationId=:configurationId and Upper(eDo.actionType) in(:actionType1,:actionType2)")
	public List<EmailTeamAPDo> getEmailTeamAP(@Param("configurationId") String configurationId , @Param("actionType1") String actionType1, @Param("actionType2") String actionType2);

}
