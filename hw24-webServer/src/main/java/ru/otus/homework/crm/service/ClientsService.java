package ru.otus.homework.crm.service;

import ru.otus.homework.crm.model.dto.ClientDto;
import ru.otus.homework.crm.model.dto.SaveClientRequest;

import java.util.List;

public interface ClientsService {
    void saveClient(SaveClientRequest request);
    List<ClientDto> getAllClients();
}
