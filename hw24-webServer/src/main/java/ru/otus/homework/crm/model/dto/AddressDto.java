package ru.otus.homework.crm.model.dto;

import jakarta.persistence.*;
import lombok.*;
import ru.otus.homework.crm.model.Address;

@Data
public class AddressDto {
    private String street;

    public AddressDto(Address address) {
        street = address.getStreet();
    }
}
