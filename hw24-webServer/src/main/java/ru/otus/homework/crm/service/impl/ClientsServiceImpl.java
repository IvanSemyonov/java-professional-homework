package ru.otus.homework.crm.service.impl;

import ru.otus.homework.crm.model.Address;
import ru.otus.homework.crm.model.Client;
import ru.otus.homework.crm.model.Phone;
import ru.otus.homework.crm.model.dto.ClientDto;
import ru.otus.homework.crm.model.dto.SaveClientRequest;
import ru.otus.homework.crm.service.ClientsService;
import ru.otus.homework.crm.service.DBServiceClient;

import java.util.Arrays;
import java.util.List;

public class ClientsServiceImpl implements ClientsService {

    private final DBServiceClient dbServiceClient;

    public ClientsServiceImpl(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    public void saveClient(SaveClientRequest request) {
        Address address = new Address();
        address.setStreet(request.getAddress());

        List<Phone> phones = Arrays.stream(request.getPhones().split(","))
                .map(Phone::new)
                .toList();

        Client client = new Client();
        client.setName(request.getName());
        client.setAddress(address);
        client.setPhones(phones);

        dbServiceClient.saveClient(client);
    }

    @Override
    public List<ClientDto> getAllClients() {
        return dbServiceClient.findAll().stream()
                .map(ClientDto::new)
                .toList();
    }
}
