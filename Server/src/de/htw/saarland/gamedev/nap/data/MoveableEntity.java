package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;

public class MoveableEntity extends Entity{

	//base values
	private Vector2 baseAcceleration;
	private Vector2 baseVelocity;
	private int weight;
	
	//momentary values
	private Vector2 acceleration;
	private Vector2 velocity;
	private Vector2 position;
	
	public MoveableEntity(){
		
	}
	
	public MoveableEntity(Vector2 position){
		this.weight = weight;
	}
	
	public MoveableEntity(Vector2 position, int weight, Vector2 baseAcceleration, Vector2 baseVelocity){
		this(position);
		this.weight=weight;
		this.baseAcceleration=baseAcceleration;
		this.baseVelocity=baseVelocity;
	}
}
