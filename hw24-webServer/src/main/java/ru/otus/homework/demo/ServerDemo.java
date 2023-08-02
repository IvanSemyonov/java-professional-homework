package ru.otus.homework.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.core.repository.DataTemplateHibernate;
import ru.otus.homework.core.repository.HibernateUtils;
import ru.otus.homework.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.homework.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.homework.crm.model.Address;
import ru.otus.homework.crm.model.Client;
import ru.otus.homework.crm.model.Phone;
import ru.otus.homework.crm.service.TemplateProcessor;
import ru.otus.homework.crm.service.impl.ClientsServiceImpl;
import ru.otus.homework.crm.service.impl.DbServiceClientImpl;
import ru.otus.homework.crm.service.impl.TemplateProcessorImpl;
import ru.otus.homework.helpers.FileSystemHelper;
import ru.otus.homework.server.WebServer;
import ru.otus.homework.server.WebServerWithBasicSecurity;

public class ServerDemo {

    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HASH_LOGIN_SERVICE_CONFIG_NAME = "realm.properties";
    private static final String REALM_NAME = "AnyRealm";

    private static final Logger log = LoggerFactory.getLogger(ServerDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
///
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        var clientService = new ClientsServiceImpl(dbServiceClient);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        String hashLoginServiceConfigPath = FileSystemHelper.localFileNameOrResourceNameToFullPath(HASH_LOGIN_SERVICE_CONFIG_NAME);
        LoginService loginService = new HashLoginService(REALM_NAME, hashLoginServiceConfigPath);



        WebServer clientsWebServer = new WebServerWithBasicSecurity(WEB_SERVER_PORT,
                loginService, gson, templateProcessor, clientService);

        clientsWebServer.start();
        clientsWebServer.join();

    }
}
