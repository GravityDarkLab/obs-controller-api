package com.gravitylab.obscontrollerapi.websocket;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OBSWebSocketClient extends WebSocketClient {

	@Value("${obs.websocket.password}")
	private String obsPassword;

	private String salt = "";
	private String challenge = "";
	private String authToken = "";

	private URI serverUri;

	static int requestID = 0;
	static int rpcVersion = 1;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@Autowired
	public OBSWebSocketClient(@Value("${obs.websocket.uri}") URI serverUri) {
		super(serverUri);
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		log.info("Connected to OBS Websocket");
	}

	@Override
	public void onMessage(String s) {
		JSONObject receivedJson = new JSONObject(s);
		if (!receivedJson.has("op") || !receivedJson.has("d")) {
			log.info("Received message from OBS Websocket {}", receivedJson.toString(4));
			return;
		}
		log.info("Message from OBS Websocket {}", receivedJson.toString(4));

		int operation = receivedJson.getInt("op");
		rpcVersion = setRpcVersion(receivedJson);

		if (operation == 0) {
			handleAuthentication(receivedJson);
		}
	}

	@Override
	public void onClose(int i, String s, boolean b) {
		String message = new String(s.getBytes(), StandardCharsets.UTF_8);
		log.info("Disconnected from OBS Websocket {} {}", i, message);
	}

	@Override
	public void onError(Exception e) {
		log.error("Error from OBS Websocket", e);
	}

	public void authenticate() {
		sendIdentifyMessage(this.authToken);
	}

	public void startRecording() {
		sendStartRecordRequest();
	}

	public void stopRecording() {
		sendStopRecordRequest();
	}

	private void handleAuthentication(JSONObject receivedJson) {
		JSONObject authenticationData = receivedJson.optJSONObject("d").getJSONObject("authentication");
		this.salt = authenticationData.getString("salt");
		this.challenge = authenticationData.getString("challenge");
		this.authToken = generateAuthToken(salt, challenge);
		log.info("Token generated :)");
	}

	private void sendStartRecordRequest() {
		JSONObject request = new JSONObject();
		request.put("op", 6);
		JSONObject data = new JSONObject();
		data.put("requestType", "StartRecord");
		data.put("requestId", requestID++);
		JSONObject requestData = new JSONObject();
		requestData.put("sceneName", "Scene 1");
		data.put("requestData", requestData);
		request.put("d", data);
		this.send(request.toString());
		log.info("Sent request to OBS Websocket {}", request.toString(4));
	}

	private void handleRecordStateChange(JSONObject receivedJson) {
		JSONObject eventData = receivedJson.optJSONObject("d").getJSONObject("eventData");
		String eventType = receivedJson.getJSONObject("d").getString("eventType");
		boolean outputActive = eventData.getBoolean("outputActive");
		String outputState = eventData.getString("outputState");
		var outputPath = eventData.get("outputPath");

		if (outputActive && outputState.equals("OBS_WEBSOCKET_OUTPUT_STARTED")) {
			log.info("Event type: {} ,Output active: {}, Output state: {}, Output path: {}", eventType, outputActive,
					outputState, outputPath);
			scheduler.schedule(this::sendStopRecordRequest, 30, TimeUnit.SECONDS);
		}
	}

	private void sendStopRecordRequest() {
		JSONObject stopRecordRequest = new JSONObject();
		stopRecordRequest.put("op", 6); // Assuming '6' is the operation code for StopRecord
		JSONObject data = new JSONObject();
		data.put("requestType", "StopRecord");
		data.put("requestId", requestID++);
		stopRecordRequest.put("d", data);
		this.send(stopRecordRequest.toString());
		log.info("Sent StopRecord request to OBS Websocket {}", stopRecordRequest.toString(4));
	}

	private void sendIdentifyMessage(String authToken) {
		JSONObject identifyMessage = new JSONObject();
		identifyMessage.put("op", 1);
		JSONObject data = new JSONObject();
		data.put("rpcVersion", rpcVersion);
		data.put("authentication", authToken);
		identifyMessage.put("d", data);
		this.send(identifyMessage.toString());
		log.info("Sent identify message to OBS Websocket {}", identifyMessage.toString(4));
	}

	private String generateAuthToken(String salt, String challenge) {
		try {
			return generateSecret(salt, challenge);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateSecret(String salt, String challenge) throws NoSuchAlgorithmException {
		// Step 1: Concatenate password and salt
		String passAndSalt = this.obsPassword + salt;
		// Step 2: SHA256 hash and base64 encode
		String base64Secret = base64Encode(sha256Hash(passAndSalt));
		// Step 3: Concatenate base64 secret with challenge
		String secretAndChallenge = base64Secret + challenge;
		// Step 4: SHA256 hash of the result and base64 encode
		return base64Encode(sha256Hash(secretAndChallenge));
	}

	private static byte[] sha256Hash(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(input.getBytes());
	}

	private static String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private int setRpcVersion(JSONObject receivedJson) {
		int rpcVersion = 1;
		JSONObject data = receivedJson.getJSONObject("d");
		if (data.has("rpcVersion")) {
			rpcVersion = data.getInt("rpcVersion");
		}
		if (data.has("negotiatedRpcVersion")) {
			rpcVersion = data.getInt("negotiatedRpcVersion");
		}
		return rpcVersion;
	}
}
