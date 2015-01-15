package main;
/**
 * Interface to allow polymorphism between game states
 * @author Henry
 *
 */
public interface GameState {

	public void update();
	public void render();
	public GameState transitionTo();	
	public void destroy();
}
