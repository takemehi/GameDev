package de.htw.saarland.gamedev.nap.client.input;

import com.badlogic.gdx.Gdx;

/**
 * Input processor that processes input from the keyboard for all keys except skill 1 and the crosshair position
 * the crosshair position is calculated by the mouse and skill 1 is one of the mouse buttons.
 * 
 * Before any call to a get function, process must be called to process the current input!
 * 
 * @author Pascal
 *
 */
public class KeyboardMouseInputProcessor implements IBaseInput {

	private int skill1Key;
	private int skill2Key;
	private int skill3Key;
	private int jumpKey;
	private int leftKey;
	private int rightKey;
	private int downKey;
	private int captureKey;
	
	private boolean leftDown;
	private boolean rightDown;
	private boolean wasLeftDown;
	private boolean wasRightDown;
	private boolean jumpDown;
	private boolean downDown;
	private boolean skill1Down;
	private boolean skill2Down;
	private boolean skill3Down;
	private boolean captureDown;
	private boolean wasCaptureDown;
	
	private int mouseX;
	private int mouseY;
	
	/**
	 * Creates a new Keyboard and mouse Input processor
	 * 
	 * @param skill1Key the key for skill 1, has to be a mouse button
	 * @param skill2Key the key for skill 2, has to be a keyboard key
	 * @param skill3Key the key for skill 3, has to be a keyboard key
	 * @param jumpKey the key to perform a jump, has to be a keyboard key
	 * @param leftKey the key to move left, has to be a keyboard key
	 * @param rightKey the key to move right, has to be a keyboard key
	 * @param downKey the key to move down (drop through platforms), has to be a keyboard key
	 * @param captureKey the key to capture a capture point, has to be a keyboard key
	 */
	public KeyboardMouseInputProcessor(int skill1Key,
			int skill2Key,
			int skill3Key,
			int jumpKey,
			int leftKey,
			int rightKey,
			int downKey,
			int captureKey) {
	
		this.skill1Key = skill1Key;
		this.skill2Key = skill2Key;
		this.skill3Key = skill3Key;
		this.jumpKey = jumpKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.downKey = downKey;
		this.captureKey = captureKey;
		
		leftDown = false;
		rightDown = false;
		wasLeftDown = false;
		wasRightDown = false;
		jumpDown = false;
		downDown = false;
		skill1Down = false;
		skill2Down = false;
		skill3Down = false;
		captureDown = false;
		wasCaptureDown = false;
	}
	
	@Override
	public void process() {
		skill1Down = Gdx.input.isButtonPressed(skill1Key);
		
		skill2Down = Gdx.input.isKeyPressed(skill2Key);
		
		skill3Down = Gdx.input.isKeyPressed(skill3Key);
		
		wasLeftDown = leftDown;
		leftDown = Gdx.input.isKeyPressed(leftKey);
		
		wasRightDown = rightDown;
		rightDown = Gdx.input.isKeyPressed(rightKey);
		
		jumpDown = Gdx.input.isKeyPressed(jumpKey);
		
		downDown = Gdx.input.isKeyPressed(downKey);
		
		wasCaptureDown = captureDown;
		captureDown = Gdx.input.isKeyPressed(captureKey);
		
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
	}
	
	@Override
	public int getCrossHairX() {
		return mouseX;
	}

	@Override
	public int getCrossHairY() {
		return mouseY;
	}

	@Override
	public boolean isSkill1Down() {
		return skill1Down;
	}

	@Override
	public boolean isSkill2Down() {
		return skill2Down;
	}

	@Override
	public boolean isSkill3Down() {
		return skill3Down;
	}

	@Override
	public boolean isJumpDown() {
		return jumpDown;
	}

	@Override
	public boolean isLeftDown() {
		return leftDown;
	}

	@Override
	public boolean wasLeftDown() {
		return wasLeftDown;
	}

	@Override
	public boolean isRightDown() {
		return rightDown;
	}

	@Override
	public boolean wasRightDown() {
		return wasRightDown;
	}

	@Override
	public boolean isDownDown() {
		return downDown;
	}

	@Override
	public boolean isCaptureDown() {
		return captureDown;
	}

	@Override
	public boolean wasCaptureDown() {
		return wasCaptureDown;
	}
}
