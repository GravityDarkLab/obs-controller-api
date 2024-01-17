package com.gravitylab.obscontrollerapi.service;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.gravitylab.obscontrollerapi.websocket.OBSWebSocketClient;

@Service
public class OBSWebSocketService {

	private final Map<String, OBSWebSocketClient> clients = new ConcurrentHashMap<>();

	public void connectClient(String ipAddress, int port, String password) throws URISyntaxException {
		String key = createClientKey(ipAddress, port);
		OBSWebSocketClient client = new OBSWebSocketClient(ipAddress, port, password);
		clients.put(key, client);
		client.connect();
	}

	public void authenticate(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.authenticate();
		}
	}

	public void disconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.close();
			clients.remove(createClientKey(ipAddress, port));
		}
	}

	public void startRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.startRecording();
		}
	}

	public void stopRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.stopRecording();
		}
	}

	public void reconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.reconnect();
		}
	}

	private OBSWebSocketClient getClient(String ipAddress, int port) {
		return clients.get(createClientKey(ipAddress, port));
	}

	private String createClientKey(String ipAddress, int port) {
		return ipAddress + ":" + port;
	}
}
