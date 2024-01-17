package com.gravitylab.obscontrollerapi.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.gravitylab.obscontrollerapi.utils.AuthTokenGenerationException;
import com.gravitylab.obscontrollerapi.utils.Operation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OBSWebSocketClient extends WebSocketClient {
	private final static int JSON_INDENT_FACTOR = 4;
	private final static String SECRET_ALGORITHM = "SHA-256";

	private final String obsPassword;

	private String authToken = "";

	static int requestID = 0;
	static int rpcVersion = 1;

	public OBSWebSocketClient(String ipAddress, int port, String password) throws URISyntaxException {
		super(new URI("ws://" + ipAddress + ":" + port));
		this.obsPassword = password;
		log.info("OBSWebSocketClient initialised!");
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		log.info("Connected to OBS Websocket");
	}

	@Override
	public void onMessage(String s) {
		JSONObject receivedJson = new JSONObject(s);
		if (!receivedJson.has("op") || !receivedJson.has("d")) {
			log.info("Received message from OBS Websocket {}", receivedJson.toString(JSON_INDENT_FACTOR));
			return;
		}
		log.info("Message from OBS Websocket {}", receivedJson.toString(JSON_INDENT_FACTOR));

		int operation = receivedJson.getInt("op");
		rpcVersion = setRpcVersion(receivedJson);

		if (operation == Operation.HELLO.getOpCode()) {
			handleAuthentication(receivedJson);
			log.info("Authentication successful!");
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
		String salt = authenticationData.getString("salt");
		String challenge = authenticationData.getString("challenge");
		this.authToken = generateAuthToken(salt, challenge);
	}

	private void sendStartRecordRequest() {
		JSONObject request = new JSONObject();
		request.put("op", Operation.REQUEST.getOpCode());
		JSONObject data = new JSONObject();
		data.put("requestType", "StartRecord");
		data.put("requestId", requestID++);
		JSONObject requestData = new JSONObject();
		requestData.put("sceneName", "Scene 1");
		data.put("requestData", requestData);
		request.put("d", data);
		this.send(request.toString());
		log.info("Sent request to OBS Websocket {}", request.toString(JSON_INDENT_FACTOR));
	}

	private void sendStopRecordRequest() {
		JSONObject stopRecordRequest = new JSONObject();
		stopRecordRequest.put("op", Operation.REQUEST.getOpCode()); // Assuming '6' is the operation code for StopRecord
		JSONObject data = new JSONObject();
		data.put("requestType", "StopRecord");
		data.put("requestId", requestID++);
		stopRecordRequest.put("d", data);
		this.send(stopRecordRequest.toString());
		log.info("Sent StopRecord request to OBS Websocket {}", stopRecordRequest.toString(JSON_INDENT_FACTOR));
	}

	private void sendIdentifyMessage(String authToken) {
		JSONObject identifyMessage = new JSONObject();
		identifyMessage.put("op", Operation.IDENTIFY.getOpCode());
		JSONObject data = new JSONObject();
		data.put("rpcVersion", rpcVersion);
		data.put("authentication", authToken);
		identifyMessage.put("d", data);
		this.send(identifyMessage.toString());
		log.info("Sent identify message to OBS Websocket {}", identifyMessage.toString(JSON_INDENT_FACTOR));
	}

	private String generateAuthToken(String salt, String challenge) {
		try {
			return generateSecret(salt, challenge);
		} catch (NoSuchAlgorithmException e) {
			throw new AuthTokenGenerationException("Failed to generate auth token", e);
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
		MessageDigest digest = MessageDigest.getInstance(SECRET_ALGORITHM);
		return digest.digest(input.getBytes());
	}

	private static String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	private int setRpcVersion(JSONObject receivedJson) {
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
