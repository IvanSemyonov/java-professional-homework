package ru.otus.homework.crm.model.dto;

import lombok.Data;

@Data
public class SaveClientRequest {
    private String name;
    private String address;
    private String phones;
}
