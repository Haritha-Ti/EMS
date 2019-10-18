package com.EMS.service;

import java.util.ArrayList;

import com.EMS.model.ClientModel;

public interface ClientService {

	int duplicationchecking(String clientName);

	ClientModel save_client_record(ClientModel client);

	ArrayList<ClientModel> getClientList();

	ClientModel getClientData(Long clientId);

}
