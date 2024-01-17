package com.gravitylab.obscontrollerapi.utils;

import lombok.Getter;

@Getter
public enum Operation {
	HELLO(0), IDENTIFY(1), IDENTIFIED(2), REIDENTIFY(3), EVENT(5), REQUEST(6), REQUEST_RESPONSE(7), REQUEST_BATCH(
			8), REQUEST_BATCH_RESPONSE(9);

	private final int opCode;

	Operation(int opCode) {
		this.opCode = opCode;
	}

}
