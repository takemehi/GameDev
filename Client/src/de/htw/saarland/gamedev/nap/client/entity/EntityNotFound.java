package de.htw.saarland.gamedev.nap.client.entity;

public class EntityNotFound extends RuntimeException {
	public EntityNotFound() {
	}
	
	public EntityNotFound(String msg) {
		super(msg);
	}
}
