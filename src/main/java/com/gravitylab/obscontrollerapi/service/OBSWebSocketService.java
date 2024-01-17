package com.gravitylab.obscontrollerapi.service;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.gravitylab.obscontrollerapi.websocket.OBSWebSocketClient;

@Service
public class OBSWebSocketService {

	private final Map<String, OBSWebSocketClient> clients = new ConcurrentHashMap<>();

	/**
	 * Connects to an OBS instance and stores the client in a map
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 * @param password  Password of the OBS instance
	 * @throws URISyntaxException thrown if the URI is invalid
	 */
	public void connectClient(String ipAddress, int port, String password) throws URISyntaxException {
		String key = createClientKey(ipAddress, port);
		OBSWebSocketClient client = new OBSWebSocketClient(ipAddress, port, password);
		clients.put(key, client);
		client.connect();
	}

	/**
	 * Authenticates an OBS instance
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 */
	public void authenticate(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.authenticate();
		}
	}

	/**
	 * Disconnects from an OBS instance
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 */
	public void disconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.close();
			clients.remove(createClientKey(ipAddress, port));
		}
	}

	/**
	 * Starts recording on an OBS instance
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 */
	public void startRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.startRecording();
		}
	}

	/**
	 * Stops recording on an OBS instance
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 */
	public void stopRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.stopRecording();
		}
	}

	/**
	 * Reconnects to an OBS instance
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 */
	public void reconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.reconnect();
		}
	}

	/**
	 * Gets an OBS client from the map
	 * 
	 * @param ipAddress IP address of the OBS instance
	 * @param port      Port of the OBS instance
	 * @return OBS client
	 */
	private OBSWebSocketClient getClient(String ipAddress, int port) {
		return clients.get(createClientKey(ipAddress, port));
	}

	/**
	 * Creates a key for the OBS client map
	 * 
	 * @param ipAddress IP address
	 * @param port      Port
	 * @return key
	 */
	private String createClientKey(String ipAddress, int port) {
		return ipAddress + ":" + port;
	}
}
