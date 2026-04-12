package es.marugi.spring.api.infrastructure.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

	@NotEmpty
	private List<String> allowedOrigins = new ArrayList<>();

	public List<String> getAllowedOrigins() {
		return List.copyOf(allowedOrigins);
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = new ArrayList<>(allowedOrigins);
	}
}

