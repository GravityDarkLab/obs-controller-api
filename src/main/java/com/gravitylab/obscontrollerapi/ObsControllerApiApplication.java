package com.gravitylab.obscontrollerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class of the application.
 */
@SpringBootApplication
public class ObsControllerApiApplication {

	/**
	 * The main method of the application.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(final String[] args) {
		SpringApplication.run(ObsControllerApiApplication.class, args);
	}

	/**
	 * This is a utility class. No instances of this class should be created.
	 */
	private ObsControllerApiApplication() {
		throw new IllegalStateException("Utility class");
	}

}
