package ru.otus.homework.crm.model.dto;


import lombok.Data;
import ru.otus.homework.crm.model.Client;
import ru.otus.homework.crm.model.Phone;

import java.util.List;

@Data
public class ClientDto {
    private Long id;
    private String name;
    private AddressDto address;
    private List<String> phones;

    public ClientDto(Client client) {
        id = client.getId();
        name = client.getName();
        if (client.getAddress() != null) {
            address = new AddressDto(client.getAddress());
        }
        phones = client.getPhones().stream()
                .map(Phone::getNumber)
                .toList();
    }
}
