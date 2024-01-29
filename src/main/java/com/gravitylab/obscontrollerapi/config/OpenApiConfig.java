package com.gravitylab.obscontrollerapi.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("OBS WebSocket API").version("v1.0")
				.description("API for controlling OBS through WebSocket").license(new License().name("MIT License")
						.identifier("GravityLab").url("https://github.com/GravityDarkLab")));
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("OBS").pathsToMatch("/obs/**").build();
	}

}
