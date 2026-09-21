package es.marugi.spring.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void createListAndUpdateUserWithoutAuthentication() throws Exception {
        WebTestClient client = client();
        String payload = "{\"name\":\"Test User\",\"login\":\"test-user\","
            + "\"password\":\"plainpass\",\"email\":\"test-user@example.com\"}";

        String response = client.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(String.class)
            .returnResult().getResponseBody();

        assertThat(response).isNotNull().doesNotContain("password").contains("test-user@example.com");
        JsonNode created = JsonMapper.builder().build().readTree(response);

        client.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[?(@.login == 'test-user')]").exists();

        client.put()
            .uri("/api/users/" + created.get("id").asLong())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload.replace("Test User", "Updated User"))
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void rejectsShortPasswordAndMissingUser() {
        WebTestClient client = client();

        client.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"name\":\"Test User\",\"login\":\"short-password\","
                + "\"password\":\"123456\",\"email\":\"short@example.com\"}")
            .exchange()
            .expectStatus().isBadRequest();

        client.put()
            .uri("/api/users/999999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"name\":\"Missing User\",\"login\":\"missing-user\","
                + "\"password\":\"plainpass\",\"email\":\"missing@example.com\"}")
            .exchange()
            .expectStatus().isNotFound();
    }

    private WebTestClient client() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }
}