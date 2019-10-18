package com.EMS.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EMS.model.ClientModel;
import com.EMS.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService{

	@Autowired
	ClientRepository clientRepository;
	
	@Override
	public int duplicationchecking(String clientName) {
		int value = clientRepository.findClient(clientName);
		return value;
	}

	@Override
	public ClientModel save_client_record(ClientModel client) {
		ClientModel clientt=clientRepository.save(client);
		return clientt;
	}

	@Override
	public ArrayList<ClientModel> getClientList() {
		ArrayList<ClientModel> list=(ArrayList<ClientModel>) clientRepository.findAll();
		return list;
	}

	@Override
	public ClientModel getClientData(Long clientId) {
		ClientModel clientData=clientRepository.getOne(clientId);
		return clientData;
	}

}
