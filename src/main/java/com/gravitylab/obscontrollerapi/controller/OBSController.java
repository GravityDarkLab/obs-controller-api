package com.gravitylab.obscontrollerapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gravitylab.obscontrollerapi.service.OBSWebSocketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
	@Operation(summary = "Connects to an OBS instance", description = "Connects to an OBS instance, must be called before any other operation")
	@PostMapping("/connect")
	public ResponseEntity<?> connect(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, @RequestParam String password,
			HttpServletRequest request) {
		try {
			if (ipAddress.equals("self")) {
				ipAddress = request.getRemoteAddr();
			}
			obsWebSocketService.connectClient(ipAddress, port, password);
			return ResponseEntity.ok("Connected to OBS at " + ipAddress + ":" + port);
		} catch (Exception e) {
			log.error("Error connecting to OBS: ", e);
			return ResponseEntity.badRequest().body("Failed to connect: " + e.getMessage());
		}
	}

	@Tag(name = "Connection Management")
	@Operation(summary = "Connects to an OBS instance", description = "Connects to an OBS instance, must be called before any other operation. Requires no authentication")
	@PostMapping("/connectNoAuth")
	public ResponseEntity<?> connect(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		try {
			if (ipAddress.equals("self")) {
				ipAddress = request.getRemoteAddr();
			}
			obsWebSocketService.connectClient(ipAddress, port);
			return ResponseEntity.ok("Connected to OBS at " + ipAddress + ":" + port);
		} catch (Exception e) {
			log.error("Error connecting to OBS: ", e);
			return ResponseEntity.badRequest().body("Failed to connect: " + e.getMessage());
		}
	}

	@Tag(name = "Connection Management")
	@Operation(summary = "Authenticates an OBS instance", description = "Authenticates an OBS instance, must be called after connecting, but not needed when no password is set")
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		if (ipAddress.equals("self")) {
			ipAddress = request.getRemoteAddr();
		}
		obsWebSocketService.authenticate(ipAddress, port);
		return ResponseEntity.ok("Authenticated OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Connection Management")
	@Operation(summary = "Reconnects to an OBS instance", description = "Reconnects to an OBS instance, must be called if connection is lost")
	@PostMapping("/reconnect")
	public ResponseEntity<?> reconnect(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		if (ipAddress.equals("self")) {
			ipAddress = request.getRemoteAddr();
		}
		obsWebSocketService.reconnect(ipAddress, port);
		return ResponseEntity.ok("Reconnected to OBS at " + ipAddress + ":" + port);
	}
	@Tag(name = "Connection Management")
	@Operation(summary = "Reconnects to all OBS instances", description = "Reconnects to all OBS instances, must be called if connection is lost")
	@PostMapping("/reconnectAll")
	public ResponseEntity<?> reconnectAll() {
		obsWebSocketService.reconnectAll();
		return ResponseEntity.ok("Reconnected to all OBS instances");
	}

	@Tag(name = "Connection Management")
	@Operation(summary = "Disconnects from an OBS instance", description = "Disconnects from an OBS instance, called only at the end")
	@PostMapping("/disconnect")
	public ResponseEntity<?> disconnect(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		if (ipAddress.equals("self")) {
			ipAddress = request.getRemoteAddr();
		}
		obsWebSocketService.disconnect(ipAddress, port);
		return ResponseEntity.ok("Disconnected from OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Connection Management")
	@Operation(summary = "Disconnects from all OBS instances", description = "Disconnects from all OBS instances, called only at the end")
	@PostMapping("/disconnectAll")
	public ResponseEntity<?> disconnectAll() {
		obsWebSocketService.disconnectAll();
		return ResponseEntity.ok("Disconnected from all OBS instances");
	}

	@Tag(name = "Recording Control")
	@Operation(summary = "Starts recording on an OBS instance", description = "Starts recording on an OBS instance")
	@PostMapping("/startRecording")
	public ResponseEntity<?> startRecording(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		if (ipAddress.equals("self")) {
			ipAddress = request.getRemoteAddr();
		}
		obsWebSocketService.startRecording(ipAddress, port);
		return ResponseEntity.ok("Started recording on OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Recording Control")
	@Operation(summary = "Starts recording on all OBS instances", description = "Starts recording on all OBS instances")
	@PostMapping("/startRecordingAll")
	public ResponseEntity<?> startRecordingAll() {
		obsWebSocketService.startRecordingAll();
		return ResponseEntity.ok("Started recording on all OBS instances");
	}

	@Tag(name = "Recording Control")
	@Operation(summary = "Stops recording on an OBS instance", description = "Stops recording on an OBS instance")
	@PostMapping("/stopRecording")
	public ResponseEntity<?> stopRecording(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		if (ipAddress.equals("self")) {
			ipAddress = request.getRemoteAddr();
		}
		obsWebSocketService.stopRecording(ipAddress, port);
		return ResponseEntity.ok("Stopped recording on OBS at " + ipAddress + ":" + port);
	}

	@Tag(name = "Recording Control")
	@Operation(summary = "Stops recording on all OBS instances", description = "Stops recording on all OBS instances")
	@PostMapping("/stopRecordingAll")
	public ResponseEntity<?> stopRecordingAll() {
		obsWebSocketService.stopRecordingAll();
		return ResponseEntity.ok("Stopped recording on all OBS instances");
	}

	@Tag(name = "Health Check")
	@Operation(summary = "Checks for the connected instances", description = "Checks for the connected instances")
	@GetMapping("/ipAddresses")
	public ResponseEntity<?> getIpAddresses() {
		List<String> ipAddresses = obsWebSocketService.getAllIpAddresses();
		ipAddresses.sort(String::compareTo);
		return ResponseEntity.ok(ipAddresses);
	}

	@Tag(name = "Health Check")
	@Operation(summary = "Checks for the number of connected instances", description = "Checks for the number of connected instances")
	@GetMapping("/count")
	public ResponseEntity<?> getCount() {
		int count = obsWebSocketService.getNumberOfClients();
		return ResponseEntity.ok(count);
	}

	@Tag(name = "Health Check")
	@Operation(summary = "Test connection to an OBS instance", description = "Test connection to an OBS instance")
	@PostMapping("/testConnection")
	public ResponseEntity<?> testConnection(@RequestParam(required = false, defaultValue = "self") String ipAddress,
			@RequestParam(required = false, defaultValue = "4455") int port, HttpServletRequest request) {
		try {
			if (ipAddress.equals("self")) {
				ipAddress = request.getRemoteAddr();
			}
			String str = obsWebSocketService.testConnection(ipAddress, port);
			return ResponseEntity.ok(str);
		} catch (Exception e) {
			log.error("Error connecting to OBS: ", e);
			return ResponseEntity.badRequest().body("Failed to connect: " + e.getMessage());
		}
	}
}