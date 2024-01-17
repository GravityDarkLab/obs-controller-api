package com.gravitylab.obscontrollerapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gravitylab.obscontrollerapi.service.OBSWebSocketService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/obs")
@Slf4j
public class OBSController {

	private final OBSWebSocketService obsWebSocketService;

	@Autowired
	public OBSController(OBSWebSocketService obsWebSocketService) {
		this.obsWebSocketService = obsWebSocketService;
	}

	@Tag(name = "Connection Management")
	@PostMapping("/connect")
	public ResponseEntity<?> connect(@RequestParam String ipAddress, @RequestParam int port,
			@RequestParam String password) {
		try {
			obsWebSocketService.connectClient(ipAddress, port, password);
			return ResponseEntity.ok("Connected to OBS at " + ipAddress + ":" + port);
		} catch (Exception e) {
			log.error("Error connecting to OBS: ", e);
			return ResponseEntity.badRequest().body("Failed to connect: " + e.getMessage());
		}
	}

	@Tag(name = "Connection Management")
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@RequestParam String ipAddress, @RequestParam int port) {
		obsWebSocketService.authenticate(ipAddress, port);
		return ResponseEntity.ok("Authenticated OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Connection Management")
	@PostMapping("/reconnect")
	public ResponseEntity<?> reconnect(@RequestParam String ipAddress, @RequestParam int port) {
		obsWebSocketService.reconnect(ipAddress, port);
		return ResponseEntity.ok("Reconnected to OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Connection Management")
	@PostMapping("/disconnect")
	public ResponseEntity<?> disconnect(@RequestParam String ipAddress, @RequestParam int port) {
		obsWebSocketService.disconnect(ipAddress, port);
		return ResponseEntity.ok("Disconnected from OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Recording Control")
	@PostMapping("/startRecording")
	public ResponseEntity<?> startRecording(@RequestParam String ipAddress, @RequestParam int port) {
		obsWebSocketService.startRecording(ipAddress, port);
		return ResponseEntity.ok("Started recording on OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Recording Control")
	@PostMapping("/stopRecording")
	public ResponseEntity<?> stopRecording(@RequestParam String ipAddress, @RequestParam int port) {
		obsWebSocketService.stopRecording(ipAddress, port);
		return ResponseEntity.ok("Stopped recording on OBS at " + ipAddress + ":" + port);
	}
}