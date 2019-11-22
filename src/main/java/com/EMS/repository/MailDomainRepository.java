package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.EMS.model.MailDomainModel;

public interface MailDomainRepository extends JpaRepository<MailDomainModel, Long>{

	@Query(value="SELECT * FROM email_notification where mail_to=?1",nativeQuery=true)
	List<MailDomainModel> getAllEmails(String email);

	@Transactional
	@Modifying
	@Query(value="update email_notification set status=1 where mail_domain_id=?1",nativeQuery=true)
	int updateEmailStatus(Long mailDomainId);

	
	@Query(value="SELECT count(*) FROM pms_staging.email_notification where mail_domain_id=?1",nativeQuery=true)
	int getEmailCount(Long mailDomainId);

}
