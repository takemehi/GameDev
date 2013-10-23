package pj.development.game.menu;

public interface MenuListener
{
	public enum MenuState
	{
		Main, Pause, Continue
	}
	
	public void startGame(String levelFilename);
	public void changeState(MenuState state);
}
