package com.gravitylab.obscontrollerapi.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.gravitylab.obscontrollerapi.websocket.OBSWebSocketClient;

@Service
public class OBSWebSocketService {

	private final Map<String, OBSWebSocketClient> clients = new ConcurrentHashMap<>();

	/**
	 * Connects to an OBS instance and stores the client in a map
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 * @param password
	 *            Password of the OBS instance
	 * @throws URISyntaxException
	 *             thrown if the URI is invalid
	 */
	public void connectClient(String ipAddress, int port, String password) throws URISyntaxException {
		String key = createClientKey(ipAddress, port);
		OBSWebSocketClient client = new OBSWebSocketClient(ipAddress, port, password);
		clients.put(key, client);
		client.connect();
	}

	/**
	 * Connects to an OBS instance and stores the client in a map
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 * @throws URISyntaxException
	 *             thrown if the URI is invalid
	 */
	public void connectClient(String ipAddress, int port) throws URISyntaxException {
		String key = createClientKey(ipAddress, port);
		OBSWebSocketClient client = new OBSWebSocketClient(ipAddress, port);
		clients.put(key, client);
		client.connect();
	}

	/**
	 * Authenticates an OBS instance
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
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
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 */
	public void disconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.close();
			clients.remove(createClientKey(ipAddress, port));
		}
	}

	/**
	 * Disconnects from all OBS instances
	 */
	public void disconnectAll() {
		for (OBSWebSocketClient client : clients.values()) {
			client.close();
		}
		clients.clear();
	}

	/**
	 * Starts recording on an OBS instance
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 */
	public void startRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.startRecording();
		}
	}

	/**
	 * Starts recording on all OBS instances
	 */
	public void startRecordingAll() {
		for (OBSWebSocketClient client : clients.values()) {
			client.startRecording();
		}
	}

	/**
	 * Stops recording on an OBS instance
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 */
	public void stopRecording(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.stopRecording();
		}
	}

	/**
	 * Stops recording on all OBS instances
	 */
	public void stopRecordingAll() {
		for (OBSWebSocketClient client : clients.values()) {
			client.stopRecording();
		}
	}
	/**
	 * Reconnects to an OBS instance
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 */
	public void reconnect(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			client.reconnect();
		}
	}

	/**
	 * Reconnects to all OBS instances
	 */
	public void reconnectAll() {
		for (OBSWebSocketClient client : clients.values()) {
			client.reconnect();
		}
	}

	/**
	 * Gets an OBS client from the map
	 *
	 * @param ipAddress
	 *            IP address of the OBS instance
	 * @param port
	 *            Port of the OBS instance
	 * @return OBS client
	 */
	public OBSWebSocketClient getClient(String ipAddress, int port) {
		return clients.get(createClientKey(ipAddress, port));
	}

	/**
	 * Gets all IP addresses of the connected OBS instances
	 *
	 * @return List of IP addresses
	 */
	public List<String> getAllIpAddresses() {
		return clients.keySet().stream().map(key -> key.split(":")[0]).collect(Collectors.toList());
	}

	/**
	 * Gets the number of connected OBS instances
	 *
	 * @return Number of connected OBS instances
	 */
	public int getNumberOfClients() {
		return clients.size();
	}

	/**
	 * Creates a key for the OBS client map
	 *
	 * @param ipAddress
	 *            IP address
	 * @param port
	 *            Port
	 * @return key
	 */
	private String createClientKey(String ipAddress, int port) {
		return ipAddress + ":" + port;
	}

	public String testConnection(String ipAddress, int port) {
		OBSWebSocketClient client = getClient(ipAddress, port);
		if (client != null) {
			return client.testConnection();
		}
		return "Failed to connect to OBS at " + ipAddress + ":" + port;
	}
}
