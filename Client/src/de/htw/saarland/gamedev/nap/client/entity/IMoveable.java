package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.math.Vector2;

public interface IMoveable {
	public void setPosition(Vector2 pos);
	public void moveLeft();
	public void moveRight();
	public void moveDown();
	public void startJump();
	public void stopJump();
	public void stopMoveLeft();
	public void stopMoveRight();
	public void stopDown();
}
