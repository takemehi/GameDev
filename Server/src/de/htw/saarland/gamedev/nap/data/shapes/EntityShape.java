package de.htw.saarland.gamedev.nap.data.shapes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class EntityShape extends PolygonShape {
	public Vector2 footSensorPos;
	
	public EntityShape(Vector2 footSensorPos) {
		this.footSensorPos = footSensorPos;
	}
}
