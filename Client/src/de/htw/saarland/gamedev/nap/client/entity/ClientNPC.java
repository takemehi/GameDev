package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;
import de.htw.saarland.gamedev.nap.data.NPC;

public class ClientNPC implements IRender, IMoveable {

	private NPC npc;
	
	private EntityAnimation animation;
	private float stateTime;
	
	public ClientNPC(NPC npc) {
		if (npc == null) {
			throw new NullPointerException();
		}
		
		this.npc = npc;
		this.stateTime = 0;
		
		// TODO initialize animation
	}
	
	@Override
	public void render(SpriteBatch batch) {
		stateTime += Gdx.graphics.getDeltaTime();
		
		Vector2 pos = npc.getBody().getPosition();
		batch.draw(animation.getAnimationFrame(getCharacterState(), stateTime), pos.x, pos.y);
		
		// TODO render healthbar
	}
	
	protected CharacterStates getCharacterState() {
		// TODO return correct state
		
		return CharacterStates.IDLE;
	}
	
	public int getEntityId() {
		return npc.getId();
	}

	@Override
	public void setPosition(Vector2 pos) {
		npc.getBody().setTransform(pos, npc.getBody().getAngle());
	}

	@Override
	public void moveLeft() {
		// TODO call move left method at npc class once it is implemented

	}

	@Override
	public void moveRight() {
		// TODO call move right method at npc class once it is implemented

	}

	@Override
	public void startJump() {
		// TODO call jump method at npc class once it is implemented

	}

	@Override
	public void stopMove() {
		// TODO call stop move method at npc class once it is implemented

	}
}
