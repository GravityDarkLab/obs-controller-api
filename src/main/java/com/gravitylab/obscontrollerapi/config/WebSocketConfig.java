package com.gravitylab.obscontrollerapi.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gravitylab.obscontrollerapi.websocket.OBSWebSocketClient;

@Configuration
public class WebSocketConfig {
	private final static String WS = "ws://";
	private final static String DEFAULT_IP_ADDRESS = "localhost";
	private final static int DEFAULT_PORT = 4444;

	@Bean
	public OBSWebSocketClient obsWebSocketClient() throws URISyntaxException {
		return new OBSWebSocketClient(new URI(WS + DEFAULT_IP_ADDRESS + ":" + DEFAULT_PORT));
	}
}
