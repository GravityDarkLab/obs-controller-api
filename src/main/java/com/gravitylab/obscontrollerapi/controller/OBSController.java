package com.gravitylab.obscontrollerapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gravitylab.obscontrollerapi.service.OBSWebSocketService;

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

	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate() {
		this.obsWebSocketService.authenticate();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/reconnect")
	public ResponseEntity<?> reconnect() {
		this.obsWebSocketService.reconnect();
		return ResponseEntity.ok().build();
	}

	@PostMapping("disconnect")
	public ResponseEntity<?> disconnect() {
		obsWebSocketService.disconnect();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/startRecording")
	public ResponseEntity<?> startRecording() {
		// Call service method to start recording
		this.obsWebSocketService.startRecording();
		return ResponseEntity.ok().build();
	}

	@PostMapping("/stopRecording")
	public ResponseEntity<?> stopRecording() {
		// Call service method to stop recording
		obsWebSocketService.stopRecording();
		return ResponseEntity.ok().build();
	}

}
