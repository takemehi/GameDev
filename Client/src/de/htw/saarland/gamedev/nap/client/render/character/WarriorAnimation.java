package de.htw.saarland.gamedev.nap.client.render.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;

public class WarriorAnimation extends EntityAnimation {
	
	private static final float X_OFFSET = 20;
	private static final float Y_OFFSET = 16;
	private static final float HEALTH_X_OFFSET = 40;
	private static final float HEALTH_Y_OFFSET = 50;
	
	private static final int SPRITE_WIDTH = 115;
	private static final int SPRITE_HEIGHT = 96;
	private static final float WALK_FRAME_DURATION = 0.025f;
	private static final float SKILL1_FRAME_DURATION = 0.05f;
	private static final float DIE_FRAME_DURATION = 0.1f;
	private static final int WALK_FRAMES = 6;
	private static final int SKILL1_FRAMES = 5;
	private static final int DIE_FRAMES = 6;
	
	private static final int WALKING = 0;
	private static final int SKILL1 = 1;
	private static final int DIE = 2;
	
	private Texture animationSheet;
	private Animation[] animations;
	private TextureRegion idle;
	
	public WarriorAnimation() {
		animationSheet = AssetStorage.getInstance().warriorAnimation;
		
		idle = new TextureRegion(animationSheet, 0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
		animations = new Animation[3]; // 3 animations, idle is no animation!
		
		animations[WALKING] = createAnimation(animationSheet, 1, WALK_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, WALK_FRAME_DURATION);
		animations[SKILL1] = createAnimation(animationSheet, 2, SKILL1_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, SKILL1_FRAME_DURATION);
		animations[DIE] = createAnimation(animationSheet, 3, DIE_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, DIE_FRAME_DURATION);
	}
	
	@Override
	public TextureRegion getAnimationFrame(CharacterStates state, float stateTime) {
		switch (state) {
			case JUMPING:
			case SKILL2:
			case SKILL3:
			case IDLE:
				return idle;
			case DEAD:
				return animations[DIE].getKeyFrame(stateTime, false);
			case SKILL1:
				return animations[SKILL1].getKeyFrame(stateTime, true);
			case WALKING:
				return animations[WALKING].getKeyFrame(stateTime, true);
//			case CAPTURING: //no animation for capturing planned
//				return idle;
			default:
				System.out.println("Character state not handled!");
				return idle;
		}
	}

	@Override
	public void dispose() {
		animationSheet.dispose();
	}

	@Override
	public float getXOffset(float unitScale) {
		return X_OFFSET * unitScale;
	}

	@Override
	public float getYOffset(float unitScale) {
		return Y_OFFSET * unitScale;
	}
	
	@Override
	public float getHealthBarXOffset(float unitScale) {
		return HEALTH_X_OFFSET * unitScale;
	}

	@Override
	public float getHealthBarYOffset(float unitScale) {
		return HEALTH_Y_OFFSET * unitScale;
	}

	@Override
	public float getAnimationTime(CharacterStates state) {
		switch (state) {
			case SKILL1:
				return SKILL1_FRAME_DURATION * SKILL1_FRAMES;
			default:
				return Float.MAX_VALUE;
		}
	}
}
