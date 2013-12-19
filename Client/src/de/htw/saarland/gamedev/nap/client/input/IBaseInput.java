package de.htw.saarland.gamedev.nap.client.input;

public interface IBaseInput {

	/**
	 * This method has to be called once per game loop! So the input will get updated
	 */
	public void process();
	
	/**
	 * Gets the current X position of the crosshair
	 */
	public int getCrossHairX();
	/**
	 * Gets the current Y position of the crosshair
	 */
	public int getCrossHairY();
	
	/**
	 * Whether skill1 key is pressed
	 */
	public boolean isSkill1Down();
	/**
	 * Whether skill2 key is pressed
	 */
	public boolean isSkill2Down();
	/**
	 * Whether skill3 key is pressed
	 */
	public boolean isSkill3Down();
	
	/**
	 * Whether jump key is pressed
	 */
	public boolean isJumpDown();
	/**
	 * Whether left key is pressed
	 */
	public boolean isLeftDown();
	/**
	 * Whether left key was down on the last iteration
	 */
	public boolean wasLeftDown();
	/**
	 * Whether right key is pressed
	 */
	public boolean isRightDown();
	/**
	 * Whether right key was down on the last iteration
	 */
	public boolean wasRightDown();
	/**
	 * Whether the down key is pressed
	 */
	public boolean isDownDown(); // :D
	/**
	 * Whether the capture key is pressed
	 */
	public boolean isCaptureDown();
	/**
	 * Whether the capture key was down on the last iteration
	 */
	public boolean wasCaptureDown();
	
}
