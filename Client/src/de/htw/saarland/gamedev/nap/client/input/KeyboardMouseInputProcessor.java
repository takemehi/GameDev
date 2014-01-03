package de.htw.saarland.gamedev.nap.client.input;

import com.badlogic.gdx.Gdx;

/**
 * Input processor that processes input from the keyboard and mouse
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
	private boolean wasJumpDown;
	private boolean downDown;
	private boolean wasDownDown;
	private boolean skill1Down;
	private boolean skill2Down;
	private boolean skill3Down;
	private boolean captureDown;
	private boolean wasCaptureDown;
	
	private int mouseX;
	private int mouseY;
	
	/**
	 * Creates a new Keyboard and mouse Input processor
	 * if any key value is less than or equal to 1 it is assumed to be a mouse button
	 * 
	 * @param skill1Key the key for skill 1
	 * @param skill2Key the key for skill 2
	 * @param skill3Key the key for skill 3
	 * @param jumpKey the key to perform a jump
	 * @param leftKey the key to move left
	 * @param rightKey the key to move right
	 * @param downKey the key to move down (drop through platforms)
	 * @param captureKey the key to capture a capture point
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
		wasDownDown = false;
		wasJumpDown = false;
	}
	
	private boolean isPressed(int key) {
		if (key <= 1) {
			return Gdx.input.isButtonPressed(key);
		}
		else {
			return Gdx.input.isKeyPressed(key);
		}
	}
	
	@Override
	public void process() {
		skill1Down = isPressed(skill1Key);
		
		skill2Down = isPressed(skill2Key);
		
		skill3Down = isPressed(skill3Key);
		
		wasLeftDown = leftDown;
		leftDown = isPressed(leftKey);
		
		wasRightDown = rightDown;
		rightDown = isPressed(rightKey);
		
		wasJumpDown = jumpDown;
		jumpDown = isPressed(jumpKey);
		
		wasDownDown = downDown;
		downDown = isPressed(downKey);
		
		wasCaptureDown = captureDown;
		captureDown = isPressed(captureKey);
		
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
	public boolean wasDownDown() {
		return wasDownDown;
	}

	@Override
	public boolean isCaptureDown() {
		return captureDown;
	}

	@Override
	public boolean wasCaptureDown() {
		return wasCaptureDown;
	}

	@Override
	public boolean wasJumpDown() {
		return wasJumpDown;
	}
}
