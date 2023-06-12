package ru.otus.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.cachehw.MyCache;
import ru.otus.model.Client;
import ru.otus.orm.core.repository.DataTemplate;
import ru.otus.orm.core.repository.executor.DbExecutor;
import ru.otus.orm.core.repository.executor.DbExecutorImpl;
import ru.otus.orm.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.orm.mapper.DataTemplateJdbc;
import ru.otus.orm.mapper.EntityClassMetaData;
import ru.otus.orm.mapper.EntitySQLMetaData;
import ru.otus.orm.mapper.impl.EntityClassMetaDataImpl;
import ru.otus.orm.mapper.impl.EntitySQLMetaDataImpl;
import ru.otus.service.cache.DBServiceClientCachedImpl;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
@DisplayName("Сравниваем скорость работы с использованием кэша и без него ")
public class DbServiceClientCacheTest {

    private static final Logger logger = LoggerFactory.getLogger(DbServiceClientCacheTest.class);

    private HikariDataSource dataSourcePool;
    private DbServiceClientImpl dbServiceClient;
    private DBServiceClientCachedImpl dbServiceClientCached;

    @BeforeEach
    public void beforeAll() {
        makeConnectionPool();
        TransactionRunnerJdbc transactionRunner = new TransactionRunnerJdbc(dataSourcePool);
        DbExecutor dbExecutor = new DbExecutorImpl();
        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl<>(entityClassMetaDataClient);
        DataTemplate<Client> dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient, entityClassMetaDataClient);
        dbServiceClient = Mockito.spy(new DbServiceClientImpl(transactionRunner, dataTemplateClient));;
        dbServiceClientCached = new DBServiceClientCachedImpl(new MyCache<>(), dbServiceClient);
    }

    @Container
    private final PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:12-alpine")
            .withDatabaseName("testDataBase")
            .withUsername("owner")
            .withPassword("secret")
            .withClasspathResourceMapping("00_createTables.sql", "/docker-entrypoint-initdb.d/00_createTables.sql", BindMode.READ_ONLY)
            .withClasspathResourceMapping("01_insertData.sql", "/docker-entrypoint-initdb.d/01_insertData.sql", BindMode.READ_ONLY);

    @Test
    @DisplayName(" Время работы метода получения клиента без применения кэша больше чем с использованием")
    public void getClientWithCache() {

        long before = System.currentTimeMillis();

        dbServiceClient.getClient(1);
        Client client = dbServiceClient.getClient(1).get();

        verify(dbServiceClient, times(2)).getClient(1);

        long timeWithoutCache = System.currentTimeMillis() - before;
        logger.info("getting client ({}) without cache, time:{}", client, timeWithoutCache);

        before = System.currentTimeMillis();

        dbServiceClientCached.getClient(1);
//        System.gc(); // При вызове сборщика мусора кэш очищается, тест не проходит
        client = dbServiceClientCached.getClient(1).get();

        verify(dbServiceClient, times(3)).getClient(1);

        long timeWithCache = System.currentTimeMillis() - before;
        logger.info("getting client ({}) with using cache, time:{}", client, timeWithCache);

        assertTrue(timeWithCache < timeWithoutCache);
    }


    @Test
    @DisplayName(" Время работы метода получения всех клиентов без применения кэша больше чем с использованием")
    public void getAllClientsWithCache() {

        long before = System.currentTimeMillis();

        dbServiceClient.findAll();
        List<Client> clients = dbServiceClient.findAll();

        verify(dbServiceClient, times(2)).findAll();

        long timeWithoutCache = System.currentTimeMillis() - before;
        logger.info("getting client ({}) without cache, time:{}", clients, timeWithoutCache);

        before = System.currentTimeMillis();

        dbServiceClientCached.findAll();
//        System.gc(); // При вызове сборщика мусора кэш очищается, тест не проходит
        clients = dbServiceClientCached.findAll();

        verify(dbServiceClient, times(3)).findAll();

        long timeWithCache = System.currentTimeMillis() - before;
        logger.info("getting client ({}) with using cache, time:{}", clients, timeWithCache);

        assertTrue(timeWithCache < timeWithoutCache);
    }

    private Properties getConnectionProperties() {
        Properties props = new Properties();
        props.setProperty("user", postgresqlContainer.getUsername());
        props.setProperty("password", postgresqlContainer.getPassword());
        props.setProperty("ssl", "false");
        return props;
    }

    void makeConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresqlContainer.getJdbcUrl());
        config.setConnectionTimeout(3000); //ms
        config.setIdleTimeout(60000); //ms
        config.setMaxLifetime(600000);//ms
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.setDataSourceProperties(getConnectionProperties());

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSourcePool = new HikariDataSource(config);
    }
}