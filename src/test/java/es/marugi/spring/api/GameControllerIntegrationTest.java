package es.marugi.spring.api;

import es.marugi.spring.api.generated.model.CreateGameDTO;
import es.marugi.spring.api.generated.model.GameDTO;
import es.marugi.spring.api.generated.model.UpdateGameDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
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
        CreateGameDTO newGame = new CreateGameDTO();
        newGame.setTitle("Integration Test Game");
        newGame.setGenre("Arcade");
        newGame.setReleaseYear(2026);

        GameDTO createdGame = webTestClient.post()
            .uri("/api/v1/games")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newGame)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(GameDTO.class)
            .returnResult().getResponseBody();

        assertThat(createdGame).isNotNull();
        assertThat(createdGame.getId()).isNotNull();
        assertThat(createdGame.getTitle()).isEqualTo("Integration Test Game");

        GameDTO retrievedGame = webTestClient.get()
            .uri("/api/v1/games/" + createdGame.getId())
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameDTO.class)
            .returnResult().getResponseBody();

        assertThat(retrievedGame).isNotNull();
        assertThat(retrievedGame.getId()).isEqualTo(createdGame.getId());
        assertThat(retrievedGame.getTitle()).isEqualTo("Integration Test Game");

        GameDTO[] games = webTestClient.get()
            .uri("/api/v1/games")
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameDTO[].class)
            .returnResult().getResponseBody();

        assertThat(games).isNotNull();
        boolean found = false;
        for (GameDTO g : games) {
            if (g.getId().equals(createdGame.getId())) {
                found = true;
                assertThat(g.getTitle()).isEqualTo("Integration Test Game");
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void updateGame() {
        CreateGameDTO newGame = new CreateGameDTO();
        newGame.setTitle("Update Test Game");
        newGame.setGenre("RPG");
        newGame.setReleaseYear(2020);

        GameDTO createdGame = webTestClient.post()
            .uri("/api/v1/games")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newGame)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(GameDTO.class)
            .returnResult().getResponseBody();

        assertThat(createdGame).isNotNull();
        Long id = createdGame.getId();

        UpdateGameDTO updateRequest = new UpdateGameDTO();
        updateRequest.setTitle("Updated Game Title");
        updateRequest.setGenre("Strategy");
        updateRequest.setReleaseYear(2022);

        GameDTO updatedGame = webTestClient.put()
            .uri("/api/v1/games/" + id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(GameDTO.class)
            .returnResult().getResponseBody();

        assertThat(updatedGame).isNotNull();
        assertThat(updatedGame.getTitle()).isEqualTo("Updated Game Title");
        assertThat(updatedGame.getGenre()).isEqualTo("Strategy");
        assertThat(updatedGame.getReleaseYear()).isEqualTo(2022);
    }

    @Test
    void createGameReturnsBadRequestWhenPayloadIsInvalid() {
        var invalidRequest = new java.util.HashMap<String, Object>();
        invalidRequest.put("title", "");
        invalidRequest.put("genre", "Arcade");
        invalidRequest.put("releaseYear", 2026);

        webTestClient.post()
            .uri("/api/v1/games")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void updateGameReturnsNotFoundWhenGameDoesNotExist() {
        var updateRequest = new java.util.HashMap<String, Object>();
        updateRequest.put("title", "Missing Game");
        updateRequest.put("genre", "Adventure");
        updateRequest.put("releaseYear", 2022);

        webTestClient.put()
            .uri("/api/v1/games/999999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void deleteGameReturnsNotFoundWhenGameDoesNotExist() {
        webTestClient.delete()
            .uri("/api/v1/games/999999")
            .exchange()
            .expectStatus().isNotFound();
    }
}
