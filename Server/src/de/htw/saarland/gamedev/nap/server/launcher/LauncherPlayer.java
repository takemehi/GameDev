package de.htw.saarland.gamedev.nap.server.launcher;

import com.smartfoxserver.v2.entities.User;

import de.htw.saarland.gamedev.nap.data.PlayableCharacter;

public class LauncherPlayer {

	private int characterId;
	private User sfsUser;
	private boolean isReady;
	
	public LauncherPlayer(int characterId, User sfsUser) {
		if (sfsUser == null) {
			throw new NullPointerException("User object is null!");
		}
		
		setCharacterId(characterId);
		setReady(false);
		this.sfsUser = sfsUser;
	}
	
	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public boolean isReady() {
		return isReady;
	}

	public int getCharacterId() {
		return characterId;
	}
	
	public void setCharacterId(int characterId) {
		if (characterId != PlayableCharacter.ID_MAGE && characterId != PlayableCharacter.ID_WARRIOR) {
			throw new IllegalArgumentException("CharId wrong!");
		}
		
		this.characterId = characterId;
	}

	public User getSfsUser() {
		return sfsUser;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LauncherPlayer other = (LauncherPlayer) obj;
		if (sfsUser == null) {
			if (other.sfsUser != null)
				return false;
		} else if (!sfsUser.equals(other.sfsUser))
			return false;
		return true;
	}
}
