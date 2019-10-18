package com.EMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EMS.model.ClientModel;

public interface ClientRepository extends JpaRepository<ClientModel, Long>{

	@Query(value="SELECT distinct client_country FROM client",nativeQuery=true)
	List<String> getLocation();

	@Query(value="SELECT count(*) FROM client  WHERE client_name=?1",nativeQuery=true)
	int findClient(String clientName);

}
