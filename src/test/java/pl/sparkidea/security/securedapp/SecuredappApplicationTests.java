package pl.sparkidea.security.securedapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@WebFluxTest
public class SecuredappApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void contextLoads() {
        assertNotNull(webTestClient);
    }

}
