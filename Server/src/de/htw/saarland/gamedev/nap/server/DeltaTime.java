package de.htw.saarland.gamedev.nap.server;

public class DeltaTime {
	
	private long lastTime;
	private float deltaTime;
	
	public DeltaTime() {
		lastTime = System.nanoTime();
		deltaTime = 0;
	}
	
	/**
	 * This is called per iteration DO NOT CALL DIRECTLY OUTSIDE MAIN GAMELOOP METHOD
	 */
	public void update() {
		long time = System.nanoTime();
		deltaTime = (time - lastTime) / 1000000000.0f;
		lastTime = time;
	}
	
	public float getDeltaTime() {
		return deltaTime;
	}
}
