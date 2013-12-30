package de.htw.saarland.gamedev.nap.data.shapes;

import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MageShape extends PolygonShape {
	public MageShape(){
		this.setAsBox(.18f, .4f);
	}
}
