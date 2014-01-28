package de.htw.saarland.gamedev.nap.data;

public final class Timer {

	public static float TIME = 0;
	
	public static void addTime(float deltaTime){
		TIME += deltaTime;
	}
}
