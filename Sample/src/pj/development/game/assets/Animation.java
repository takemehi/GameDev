package pj.development.game.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation
{
	private Texture texture;
	private int frames;
	private float frameduration;
	private int rowBeginHeight;
	private int width;
	private int height;
	private boolean flip;
	
	public Animation(Texture texture, int frames, float frameduration, int rowBeginHeight, int width, int height) throws AnimationException
	{
		if (texture == null || frames <= 0 || frameduration <= 0 || rowBeginHeight < 0 || width <= 0 || height <= 0)
			throw new AnimationException("Wrong param");
		
		this.texture = texture;
		this.frames = frames;
		this.frameduration = frameduration;
		this.rowBeginHeight = rowBeginHeight;
		this.width = width;
		this.height = height;
		flip = false;
	}
	
	public TextureRegion getCurrentFrame(float stateTime)
	{
		int currentFrame = (int)(stateTime / frameduration) - 1;
		
		if (currentFrame < 0 || currentFrame > frames - 1)
			currentFrame = 0;
		
		TextureRegion result = new TextureRegion(texture, width * currentFrame, rowBeginHeight, width, height);
		
		result.flip(flip, false);
		
		return result;
	}
	
	public void setFlip(boolean flip)
	{
		this.flip = flip;
	}
	
	@SuppressWarnings("serial")
	public class AnimationException extends Exception
	{
		public AnimationException()
		{
		}
		
		public AnimationException(String message)
		{
			super(message);
		}
	}
}
