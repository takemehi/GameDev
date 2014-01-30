package de.htw.saarland.gamedev.nap.data.shapes;

import com.badlogic.gdx.math.Vector2;

public class WarriorShape extends EntityShape {
	public WarriorShape(){
		super(new Vector2(0, -0.4f));
		this.setAsBox(.2f, .35f);
	}
}
