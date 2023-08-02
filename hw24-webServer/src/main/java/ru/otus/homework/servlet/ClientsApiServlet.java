package ru.otus.homework.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.otus.homework.crm.model.Address;
import ru.otus.homework.crm.model.Client;
import ru.otus.homework.crm.model.Phone;
import ru.otus.homework.crm.model.dto.ClientDto;
import ru.otus.homework.crm.model.dto.SaveClientRequest;
import ru.otus.homework.crm.service.ClientsService;
import ru.otus.homework.crm.service.DBServiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class ClientsApiServlet extends HttpServlet {

    private final ClientsService clientsService;
    private final Gson gson;

    public ClientsApiServlet(ClientsService clientService, Gson gson) {
        this.clientsService = clientService;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(clientsService.getAllClients()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        SaveClientRequest saveClientRequest = gson.fromJson(sb.toString(), SaveClientRequest.class);
        clientsService.saveClient(saveClientRequest);
    }
}
