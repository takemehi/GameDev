package de.htw.saarland.gamedev.nap.server.launcher.exception;

public class PlayerNotFoundException extends RuntimeException {
	
	public PlayerNotFoundException() {
	}
	
	public PlayerNotFoundException(String msg) {
		super(msg);
	}
}
