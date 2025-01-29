package com.chrisp1985.UserDataAPI;

import com.chrisp1985.UserDataAPI.aspects.CacheAspect;
import com.chrisp1985.UserDataAPI.data.UserR2dbcRepository;
import com.chrisp1985.UserDataAPI.service.UserDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.time.Duration;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SpringExtension.class)
public class ContainerTests {

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>(
            DockerImageName.parse("mysql:8.0"))
            .withUsername("root")
            .withPassword("root")
            .withDatabaseName("mydb")
            .withStartupTimeout(Duration.ofSeconds(60));;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", ContainerTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", mysql::getUsername);
        registry.add("spring.r2dbc.password", mysql::getPassword);
        registry.add("spring.flyway.url", mysql::getJdbcUrl);

    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:mysql://%s:%s/%s", mysql.getHost(),
                mysql.getMappedPort(MySQLContainer.MYSQL_PORT), mysql.getDatabaseName());
    }

    @Autowired
    private UserDataService userDataService;

    @MockBean
    private UserR2dbcRepository userR2DbcRepository;

    @SpyBean
    private CacheAspect cacheAspect;

//    @SpyBean
//    private AbstractMetrics abstractMetrics;
//
//    @SpyBean
//    private UserServiceMetrics userServiceMetrics;

    @Test
    public void shouldUseCacheForRepositoryCalls() {

        userDataService.getAllUsers();
        verify(userR2DbcRepository, times(1)).findAll();
        Mockito.clearInvocations(userR2DbcRepository);

        // Check cache works by verifying we only call the findAll on the repository once.
        userDataService.getAllUsers();
        verify(userR2DbcRepository, times(0)).findAll();

    }
}
