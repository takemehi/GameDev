package de.htw.saarland.gamedev.nap.client.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.IRender;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;
import de.htw.saarland.gamedev.nap.data.NPC;

public class ClientNPC implements IRender, IMoveable, Disposable {

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
		if (npc.isJumping()) {
			return CharacterStates.JUMPING;
		}
		else if (npc.getLeft() || npc.getRight()) {
			return CharacterStates.WALKING;
		}
		else if (npc.getHealth() <= 0) {
			return CharacterStates.DEAD;
		}
		
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
		npc.setLeft(true);
	}

	@Override
	public void moveRight() {
		npc.setRight(true);
	}

	@Override
	public void startJump() {
		npc.setUp(true);
	}
	
	@Override
	public void stopJump() {
		npc.setUp(false);
	}

	@Override
	public void stopMoveLeft() {
		npc.setLeft(false);
	}
	
	@Override
	public void stopMoveRight() {
		npc.setRight(false);
	}

	@Override
	public void moveDown() {
		npc.setDown(true);
	}
	
	@Override
	public void stopDown() {
		npc.setDown(false);
	}

	@Override
	public void dispose() {
		animation.dispose();
	}
}
