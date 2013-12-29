package de.htw.saarland.gamedev.nap.data.shapes;

import com.badlogic.gdx.physics.box2d.PolygonShape;

public class WarriorShape extends PolygonShape{
	public WarriorShape(){
		this.setAsBox(.2f, .35f);
	}
}
