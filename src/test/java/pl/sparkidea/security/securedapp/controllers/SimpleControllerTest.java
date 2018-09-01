package pl.sparkidea.security.securedapp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static com.spotify.hamcrest.jackson.IsJsonArray.jsonArray;
import static com.spotify.hamcrest.jackson.IsJsonBoolean.jsonBoolean;
import static com.spotify.hamcrest.jackson.IsJsonNull.jsonNull;
import static com.spotify.hamcrest.jackson.IsJsonObject.jsonObject;
import static com.spotify.hamcrest.jackson.IsJsonText.jsonText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * @author Maciej Lesniak
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private final Consumer<EntityExchangeResult<byte[]>> ASSERT_PUBLIC_RESPONSE = entityExchangeResult ->
            assertThat(getJsonNode(entityExchangeResult), is(jsonObject()
                    .where("response", is(jsonText("public resource")))
            ));

    private final Consumer<EntityExchangeResult<byte[]>> ASSERT_SECURED_RESPONSE = entityExchangeResult ->
            assertThat(getJsonNode(entityExchangeResult), is(jsonObject()
                    .where("response", is(jsonText("secured resource requested")))
                    .where("principal", jsonObject()
                            .where("authenticated", is(jsonBoolean(true)))
                            .where("name", is(jsonText("username")))
                            .where("credentials", is(not(jsonNull())))
                            .where("authorities", is(jsonArray(contains(
                                    jsonObject().where("authority", is(jsonText("ROLE_USER")))
                            ))))
                    ))
            );

    @Test
    public void getPublicResource_willRespondWithPayload_whenNoAuthorization() {
        //@formatter:off
        webTestClient.get().uri("/public")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                    .consumeWith(entityExchangeResult ->
                        assertThat(getJsonNode(entityExchangeResult), is(notNullValue()))
                    );
        //@formatter:on
    }

    @Test
    public void getPublicResource_willRespondWithPayload_whenAuthorizedRequest() {
        //@formatter:off
        webTestClient.get().uri("/public")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationHeaderValue("username", "password"))
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                    .consumeWith(ASSERT_PUBLIC_RESPONSE);
        //@formatter:on
    }

    @Test
    public void getPublicResource_willRespondWith400_whenWrongAuthorization() {
        //@formatter:off
        webTestClient.get().uri("/public")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationHeaderValue("username", "wrong password"))
                .exchange()
                .expectStatus()
                    .isUnauthorized();
        //@formatter:on
    }

    @Test
    public void getSecuredResource_willRespondWithPayloadAndPrincipal_whenAuthorizedReuest() {
        //@formatter:off
        webTestClient.get().uri("/api/secured")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationHeaderValue("username", "password"))
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                    .consumeWith(ASSERT_SECURED_RESPONSE);
        //@formatter:on
    }

    @Test
    public void getSecuredResource_willRespondWith400_whenNotAuthorizedReuest() throws UnsupportedEncodingException {
        //@formatter:off
        webTestClient.get().uri("/api/secured")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", authorizationHeaderValue("unknownUser", "misspelled password"))
                .exchange()
                .expectStatus()
                    .isUnauthorized();
        //@formatter:on
    }

    private static String authorizationHeaderValue(String username, String password) {
        String payload = "";
        try {
            payload = "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return payload;
    }

    private JsonNode getJsonNode(EntityExchangeResult<byte[]> entityExchangeResult) {
        JsonNode simpleResponse = null;
        try {
            simpleResponse = objectMapper.readTree(entityExchangeResult.getResponseBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return simpleResponse;
    }

}