package com.gravitylab.obscontrollerapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gravitylab.obscontrollerapi.websocket.OBSWebSocketClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OBSWebSocketService {
	private final OBSWebSocketClient obsWebSocketClient;

	@Autowired
	public OBSWebSocketService(OBSWebSocketClient obsWebSocketClient) {
		this.obsWebSocketClient = obsWebSocketClient;
		obsWebSocketClient.connect();
	}

	public void authenticate() {
		obsWebSocketClient.authenticate();
	}

	public void disconnect() {
		obsWebSocketClient.close();
	}

	public void startRecording() {
		this.obsWebSocketClient.startRecording();
	}

	public void stopRecording() {
		this.obsWebSocketClient.stopRecording();
	}

	public void reconnect() {
		obsWebSocketClient.reconnect();
	}
}
