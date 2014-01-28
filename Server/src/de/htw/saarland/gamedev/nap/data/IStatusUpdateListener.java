package de.htw.saarland.gamedev.nap.data;

public interface IStatusUpdateListener {
	public void hpUpdated(int newHp);
	public void stunUpdated(boolean stunned);
	public void snareUpdated(boolean snared);
}
