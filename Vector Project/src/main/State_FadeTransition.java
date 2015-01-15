package main;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * Fades between two game states
 * @author Henry
 *
 */
public class State_FadeTransition implements GameState{
	//States to transition from and to
	GameState s1;
	GameState s2;
	
	//Indicates which state to render and update
	boolean s1Active;
	
	//Current fade factor, from 0 to 1
	double fade;
	//Time to fade between states
	double fadeTime;
	
	/**
	 * Fades from one state to another
	 * @param from State to transition from
	 * @param to State to transition to
	 * @param fadeTime Time to take to fade between states
	 */
	public State_FadeTransition(GameState from, GameState to, double fadeTime){
		s1 = from;
		s2 = to;
		s1Active = true;
		fade = 0;
		this.fadeTime = fadeTime;
	}

	/**
	 * Update fade factor and currently active game state
	 */
	@Override
	public void update() {
		if (s1Active) s1.update();
		else s2.update();
		fade += Start.deltaTime() / fadeTime * (s1Active ? 1 : -1);
		if (fade >= 1){ 
			fade = 1;
			s1Active = false;
		}
	}

	/**
	 * Render the currently active game state and a black rectangle over the entire screen with
	 * opacity indicated by fade
	 */
	@Override
	public void render() {
		if (s1Active) s1.render(); 
		else s2.render();
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(0,0,0, fade);
			GL11.glVertex2d(0,0);
			GL11.glVertex2d(Display.getWidth(), 0);
			GL11.glVertex2d(Display.getWidth(), Display.getHeight());
			GL11.glVertex2d(0, Display.getHeight());
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	/**
	 * Returns the state to transition to when it is done fading
	 */
	@Override
	public GameState transitionTo() {
		if (!s1Active && fade <= 0){ 
			s1.destroy();
			return s2;
		
		}
		else return null;
	}

	/** 
	 * Free resources used by this game state
	 */
	@Override
	public void destroy() {
		
	}

}
