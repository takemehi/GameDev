package de.htw.saarland.gamedev.nap.data.shapes;

import com.badlogic.gdx.math.Vector2;

public class MageShape extends EntityShape {
	public MageShape(){
		super(new Vector2(0, -0.45f));
		this.setAsBox(.18f, .4f);
	}
}
