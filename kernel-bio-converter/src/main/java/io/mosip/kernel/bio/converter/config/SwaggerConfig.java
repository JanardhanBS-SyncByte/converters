package io.mosip.kernel.bio.converter.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration class for Swagger/OpenAPI documentation generation.
 * <p>
 * Builds the Springdoc {@link OpenAPI} bean from {@link OpenApiProperties} and
 * registers an Authorization apiKey so Swagger UI shows the Authorize button
 * (same pattern as kernel-auth-service).
 * </p>
 */
@Configuration
public class SwaggerConfig {

	/**
	 * Scheme name shown by Swagger UI as Authorize (IDA-style Authorization apiKey).
	 */
	public static final String AUTHORIZATION_SCHEME = "Authorization";

	private OpenApiProperties openApiProperties;

	/**
	 * Constructs a {@code SwaggerConfig} instance with the provided
	 * {@link OpenApiProperties}.
	 *
	 * @param openApiProperties The properties containing OpenAPI configuration
	 *                          details
	 */
	@Autowired
	public SwaggerConfig(OpenApiProperties openApiProperties) {
		this.openApiProperties = openApiProperties;
	}

	/**
	 * Creates an {@link OpenAPI} bean configured with title, version, description,
	 * license, servers, and Authorize apiKey.
	 *
	 * @return Configured {@link OpenAPI} instance representing the OpenAPI
	 *         specification
	 */
	@Bean
	public OpenAPI openApi() {
		OpenAPI api = new OpenAPI()
				.components(new Components().addSecuritySchemes(AUTHORIZATION_SCHEME, authorizationApiKey()))
				.addSecurityItem(new SecurityRequirement().addList(AUTHORIZATION_SCHEME))
				.info(new Info().title(openApiProperties.getInfo().getTitle())
						.version(openApiProperties.getInfo().getVersion())
						.description(openApiProperties.getInfo().getDescription())
						.license(new License().name(openApiProperties.getInfo().getLicense().getName())
								.url(openApiProperties.getInfo().getLicense().getUrl())));

		openApiProperties.getService().getServers().forEach(
				server -> api.addServersItem(new Server().description(server.getDescription()).url(server.getUrl())));
		return api;
	}

	/**
	 * Header apiKey named {@code Authorization}, so Swagger UI shows Authorize with
	 * Name/In/Value (paste {@code Bearer <token>} or the raw token as used by the
	 * gateway/adapter).
	 *
	 * @return the security scheme
	 */
	private static SecurityScheme authorizationApiKey() {
		return new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)
				.name(AUTHORIZATION_SCHEME);
	}

	/**
	 * Creates a {@link GroupedOpenApi} bean to group API paths based on configured
	 * properties.
	 *
	 * @return {@link GroupedOpenApi} instance representing grouped API paths
	 */
	@Bean
	public GroupedOpenApi groupedOpenApi() {
		return GroupedOpenApi.builder().group(openApiProperties.getGroup().getName())
				.pathsToMatch(openApiProperties.getGroup().getPaths().stream().toArray(String[]::new)).build();
	}
}
