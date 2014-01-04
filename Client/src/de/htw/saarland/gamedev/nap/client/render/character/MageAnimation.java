package de.htw.saarland.gamedev.nap.client.render.character;

import java.io.File;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.htw.saarland.gamedev.nap.client.render.EntityAnimation;
import de.htw.saarland.gamedev.nap.client.render.EntityAnimation.CharacterStates;

public class MageAnimation extends EntityAnimation {

private static final String ANIMATIONSHEET_FILEPATH = "data/gfx/mageSheet.png";
	
	private static final int SPRITE_WIDTH = 115;
	private static final int SPRITE_HEIGHT = 115;
	private static final float FRAME_DURATION = 0.025f;
	private static final int WALK_FRAMES = 6;
	private static final int SKILL1_FRAMES = 6;
	private static final int SKILL3_FRAMES = 6;
	
	private static final int WALKING = 0;
	private static final int SKILL1 = 1;
	private static final int SKILL3 = 2;
	
	private Texture animationSheet;
	private Animation[] animations;
	private TextureRegion idle;
	
	public MageAnimation() {
		animationSheet = new Texture(new FileHandle(new File(ANIMATIONSHEET_FILEPATH)));
		
		idle = new TextureRegion(animationSheet, 0, 0, SPRITE_WIDTH, SPRITE_HEIGHT);
		animations = new Animation[3]; // 3 animations, idle is no animation!
		
		animations[WALKING] = createAnimation(animationSheet, 1, WALK_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
		animations[SKILL1] = createAnimation(animationSheet, 2, SKILL1_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
		animations[SKILL3] = createAnimation(animationSheet, 3, SKILL3_FRAMES, SPRITE_WIDTH, SPRITE_HEIGHT, FRAME_DURATION);
	}
	
	@Override
	public TextureRegion getAnimationFrame(CharacterStates state, float stateTime) {
		switch (state) {
			case JUMPING:
			case IDLE:
				return idle;
			case DEAD:
				return idle; // TODO death animation
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
}
