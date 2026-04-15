package es.marugi.spring.api;

import es.marugi.spring.api.adapter.in.rest.dto.CreateGameRequestDTO;
import es.marugi.spring.api.adapter.in.rest.dto.GameResponseDTO;
import es.marugi.spring.api.adapter.in.rest.dto.UpdateGameRequestDTO;
import es.marugi.spring.api.generated.model.CreateGameDTO;
import es.marugi.spring.api.generated.model.GameDTO;
import es.marugi.spring.api.generated.model.UpdateGameDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GameControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;


    @BeforeEach
    void setUp() {
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void createAndRetrieveGame() {
        CreateGameRequestDTO newGame = new CreateGameRequestDTO(
            "Integration Test Game",
            "Game created by integration test",
            2026,
            8.0
        );

        GameResponseDTO createdGame = webTestClient.post()
            .uri("/api/games")
            .bodyValue(newGame)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().valueMatches("Location", ".*/api/games/\\d+$")
            .expectBody(GameResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(createdGame).isNotNull();
        assertThat(createdGame.id()).isNotNull();
        assertThat(createdGame.recordedAt()).isNotNull();
        assertThat(createdGame.title()).isEqualTo("Integration Test Game");

        GameResponseDTO retrievedGame = webTestClient.get()
            .uri("/api/games/" + createdGame.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(retrievedGame).isNotNull();
        assertThat(retrievedGame.id()).isEqualTo(createdGame.id());
        assertThat(retrievedGame.title()).isEqualTo("Integration Test Game");

        GameResponseDTO[] games = webTestClient.get()
            .uri("/api/games")
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameResponseDTO[].class)
            .returnResult().getResponseBody();

        assertThat(games).isNotNull();
        boolean found = false;
        for (GameResponseDTO g : games) {
            if (g.id().equals(createdGame.id())) {
                found = true;
                assertThat(g.title()).isEqualTo("Integration Test Game");
                assertThat(g.recordedAt()).isNotNull();
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void updateGame() {
        CreateGameRequestDTO newGame = new CreateGameRequestDTO(
            "Update Test Game",
            "Game to be updated",
            2020,
            5.0
        );

        GameResponseDTO createdGame = webTestClient.post()
            .uri("/api/games")
            .bodyValue(newGame)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(GameResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(createdGame).isNotNull();
        Long id = createdGame.id();

        UpdateGameRequestDTO updateRequest = new UpdateGameRequestDTO(
            "Updated Game Title",
            "Updated description",
            2022,
            9.0
        );

        GameResponseDTO updatedGame = webTestClient.put()
            .uri("/api/games/" + id)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameResponseDTO.class)
            .returnResult().getResponseBody();

        assertThat(updatedGame).isNotNull();
        assertThat(updatedGame.title()).isEqualTo("Updated Game Title");
        assertThat(updatedGame.description()).isEqualTo("Updated description");
        assertThat(updatedGame.developmentYear()).isEqualTo(2022);
        assertThat(updatedGame.score()).isEqualTo(9);
    }

    @Test
    void createGameReturnsBadRequestWhenPayloadIsInvalid() {
        var invalidRequest = new java.util.HashMap<String, Object>();
        invalidRequest.put("title", "");
        invalidRequest.put("description", "Valid description");
        invalidRequest.put("developmentYear", 2026);
        invalidRequest.put("score", 11);

        webTestClient.post()
            .uri("/api/games")
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.error").isEqualTo("Bad Request")
            .jsonPath("$.message").exists()
            .jsonPath("$.path").isEqualTo("/api/games");
    }

    @Test
    void updateGameReturnsNotFoundWhenGameDoesNotExist() {
        var updateRequest = new java.util.HashMap<String, Object>();
        updateRequest.put("title", "Missing Game");
        updateRequest.put("description", "This game does not exist");
        updateRequest.put("developmentYear", 2022);
        updateRequest.put("score", 9.5);

        webTestClient.put()
            .uri("/api/games/999999")
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not Found")
            .jsonPath("$.message").isEqualTo("Game with id 999999 not found")
            .jsonPath("$.path").isEqualTo("/api/games/999999");
    }

    @Test
    void deleteGameReturnsNotFoundWhenGameDoesNotExist() {
        webTestClient.delete()
            .uri("/api/games/999999")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not Found")
            .jsonPath("$.message").isEqualTo("Game with id 999999 not found")
            .jsonPath("$.path").isEqualTo("/api/games/999999");
    }
}
