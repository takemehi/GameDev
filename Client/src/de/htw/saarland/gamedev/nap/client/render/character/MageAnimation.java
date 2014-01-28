package de.htw.saarland.gamedev.nap.client.render.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.htw.saarland.gamedev.nap.assets.AssetStorage;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;

public class MageAnimation extends EntityAnimation {
	
	private static final float X_OFFSET = 12;
	private static final float Y_OFFSET = 10;
	private static final float HEALTH_X_OFFSET = 40;
	private static final float HEALTH_Y_OFFSET = 60;
	
	private static final int SPRITE_WIDTH = 96;
	private static final int SPRITE_HEIGHT = 96;
	private static final float FRAME_DURATION = 0.1f;
	private static final float FRAME_DURATION_SKILLS = 0.07f;
	private static final int WALK_FRAMES = 6;
	private static final int SKILL1_FRAMES = 6;
	private static final int SKILL3_FRAMES = 6;
	private static final int DIE_FRAMES = 6;
	
	private static final int WALKING = 0;
	private static final int SKILL1 = 1;
	private static final int SKILL3 = 2;
	private static final int DIE = 3;
	
	private Texture animationSheet;
	private Animation[] animations;
	private TextureRegion idle;
	
	public MageAnimation() {
		animationSheet = AssetStorage.getInstance().mageAnimation;
		
		idle = new TextureRegion(animationSheet, 0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
		animations = new Animation[4]; // 4 animations, idle is no animation!
		
		animations[WALKING] = createAnimation(animationSheet, 1, WALK_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
		animations[SKILL1] = createAnimation(animationSheet, 2, SKILL1_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION_SKILLS);
		animations[SKILL3] = createAnimation(animationSheet, 3, SKILL3_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
		animations[DIE] = createAnimation(animationSheet, 4, DIE_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
	}
	
	@Override
	public TextureRegion getAnimationFrame(CharacterStates state, float stateTime) {
		switch (state) {
			case JUMPING:
			case IDLE:
				return idle;
			case DEAD:
				return animations[DIE].getKeyFrame(stateTime, false);
			case SKILL2:
			case SKILL1:
				return animations[SKILL1].getKeyFrame(stateTime, false);
			case SKILL3:
				return animations[SKILL3].getKeyFrame(stateTime, false);
			case WALKING:
				return animations[WALKING].getKeyFrame(stateTime, true);
			case CAPTURING:
				return idle; // TODO capturing animation
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
			case SKILL2:
			case SKILL1:
				return SKILL1_FRAMES * FRAME_DURATION_SKILLS;
			case SKILL3:
				return SKILL3_FRAMES * FRAME_DURATION;
			default:
				return Float.MAX_VALUE;
		}
	}
}
