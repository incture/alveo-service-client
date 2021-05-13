package com.ap.menabev.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.menabev.entity.MailTemplateDo;


@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplateDo, String> {

	@Query("select mDo from MailTemplateDo mDo where mDo.configurationId=:configurationId ")
	public List<MailTemplateDo> getMailTemplate(@Param("configurationId") String configurationId);
	
	
}
