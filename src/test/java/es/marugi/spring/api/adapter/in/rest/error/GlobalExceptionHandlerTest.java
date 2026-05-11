package es.marugi.spring.api.adapter.in.rest.error;

import es.marugi.spring.api.application.exception.GameNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.startsWith;
import static java.util.Objects.requireNonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void handleValidationExceptionReturnsBadRequestResponse() throws Exception {
        mockMvc.perform(post("/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value(startsWith("title: ")))
            .andExpect(jsonPath("$.path").value("/validation"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGameNotFoundExceptionReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Game with id 42 not found"))
            .andExpect(jsonPath("$.path").value("/not-found"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGenericExceptionReturnsInternalServerErrorResponse() throws Exception {
        mockMvc.perform(get("/unexpected"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
            .andExpect(jsonPath("$.path").value("/unexpected"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class TestController {

        @PostMapping("/validation")
        void validate(@Valid @RequestBody ValidationRequest request) {
            requireNonNull(request.title());
        }

        @GetMapping("/not-found")
        void notFound() {
            throw new GameNotFoundException(42L);
        }

        @GetMapping("/unexpected")
        void unexpected() {
            throw new IllegalStateException("Unexpected failure");
        }
    }

    record ValidationRequest(@NotBlank String title) {
    }
}

