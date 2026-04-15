package es.marugi.spring.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "server.servlet.context-path=/api",
    "spring.datasource.url=jdbc:h2:mem:securitytest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never",
    "spring.jpa.defer-datasource-initialization=false",
    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwks",
    "app.cors.allowed-origins=http://localhost:3000"
})
@ActiveProfiles("security")
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    void getGamesIsPublic() throws Exception {
        mockMvc.perform(get("/api/games").contextPath("/api"))
            .andExpect(status().isOk());
    }

    @Test
    void healthProbeIsPublic() throws Exception {
        mockMvc.perform(get("/api/actuator/health").contextPath("/api"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void optionsRequestsArePublic() throws Exception {
        mockMvc.perform(options("/api/games").contextPath("/api"))
            .andExpect(status().isOk());
    }

    @Test
    void createGameRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/games").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Unauthorized Create",
                      "description": "Should be rejected without token",
                      "developmentYear": 2026,
                      "score": 7.5
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void updateGameRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/games/1").contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Unauthorized Update",
                      "description": "Should be rejected without token",
                      "developmentYear": 2026,
                      "score": 8.0
                    }
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteGameRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/api/games/1").contextPath("/api"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void createGameAllowsAuthenticatedRequests() throws Exception {
        String uniqueTitle = "Secured Game " + System.currentTimeMillis();

        mockMvc.perform(post("/api/games").contextPath("/api")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "%s",
                      "description": "Created with JWT authentication",
                      "developmentYear": 2026,
                      "score": 8.5
                    }
                    """.formatted(uniqueTitle)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/api/games/\\d+$")))
            .andExpect(jsonPath("$.title").value(uniqueTitle));
    }
}


