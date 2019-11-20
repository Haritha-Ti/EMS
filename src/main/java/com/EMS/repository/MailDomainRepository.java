package com.EMS.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EMS.model.MailDomainModel;

public interface MailDomainRepository extends JpaRepository<MailDomainModel, Long>{

}
