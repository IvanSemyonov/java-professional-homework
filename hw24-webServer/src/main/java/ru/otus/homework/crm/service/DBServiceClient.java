package ru.otus.homework.crm.service;


import ru.otus.homework.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DBServiceClient {

    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    Optional<Client> findByLogin(String login);

    List<Client> findAll();
}
