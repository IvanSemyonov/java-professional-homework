package ru.otus.service.cache;

import ru.otus.cachehw.HwCache;
import ru.otus.model.Client;
import ru.otus.service.DBServiceClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DBServiceClientCachedImpl implements DBServiceClient {
    
    private static final String ALL_CLIENTS_KEY = "allClients";

    private final HwCache<String, Object> clientHwCache;
    private final DBServiceClient dbServiceClient;

    public DBServiceClientCachedImpl(HwCache<String, Object> clientHwCache, DBServiceClient dbServiceClientImpl) {
        this.clientHwCache = clientHwCache;
        this.dbServiceClient = dbServiceClientImpl;
    }

    @Override
    public Client saveClient(Client client) {
        return dbServiceClient.saveClient(client);
    }

    @Override
    public Optional<Client> getClient(long id) {
        return Optional.ofNullable((Client) clientHwCache.get(String.valueOf(id)))
                .or(() -> dbServiceClient.getClient(id)
                        .map(this::saveToCache));
    }

    private Client saveToCache(Client client) {
        clientHwCache.put(String.valueOf(client.getId()), client);
        return client;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = (List<Client>) clientHwCache.get(ALL_CLIENTS_KEY);
        if (Objects.isNull(clients) || clients.isEmpty()) {
            clients = dbServiceClient.findAll();
            clientHwCache.put(new String(ALL_CLIENTS_KEY), clients);
        }
        return clients;
    }
}
