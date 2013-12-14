package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;

public class NPC extends MoveableEntity{
	
	private Body body;

	public NPC(Shape shape, float weight, float density, float friction,
			float restitution, Vector2 position) {
		super(shape, weight, density, friction, restitution, position);
		// TODO Auto-generated constructor stub
	}
	
	
	//getter / setter
	
	public void setBody(Body body){
		this.body=body;
	}
	
	public Body getBody(){
		return body;
	}

}
