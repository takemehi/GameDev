package de.htw.saarland.gamedev.nap.data;

import com.badlogic.gdx.math.Vector2;

public interface IStatusUpdateListener {
	public void hpUpdated(int newHp);
	public void stunUpdated(boolean stunned);
	public void snareUpdated(boolean snared);
	public void respawn();
	public void positionChanged(Vector2 pos);
}
