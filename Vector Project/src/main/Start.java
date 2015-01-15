package main;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * Initializes, deconstructs and manages loop for the game
 * @author Henry
 *
 */
public class Start {
	
	//Used to calculate time between frames
	private static double deltaTime;
	private static double lastTime;
	
	//Current game state
	private static GameState state;
	
	public static void main(String[] args) {
		
		//Initialize Display and OpenGL
		lastTime = (Sys.getTime()* 1000 / Sys.getTimerResolution() );
		init();
		while(!Display.isCloseRequested()){
			
			//Updates game state
			updateDeltaTime();
			update();
			
			//Clear screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			GL11.glLoadIdentity();
			
			//Render
			render();
			
			
			//Update display
			Display.update();
			Display.sync(1000);
		}
		//Destroy when done
		decon();

	}
	
	/**
	 * Update the current state, transition to a new state if indicated by state.transitionTo()
	 */
	private static void update(){
		state.update();
		if (state.transitionTo() != null) state = state.transitionTo();
	}
	
	/**
	 * Render the current game state
	 */
	private static void render(){
		state.render();
	}
	
	/**
	 * 
	 * @return Time between frames
	 */
	public static double deltaTime(){
		return deltaTime;
	}
	
	/**
	 * Calculates time between frames
	 */
	private static void updateDeltaTime(){
		deltaTime = (Sys.getTime()* 1000.0 / Sys.getTimerResolution() ) - lastTime;
		lastTime = (Sys.getTime()* 1000.0 / Sys.getTimerResolution() );
	}
	
	/**
	 * Initialize display, controls, sound, and the gameState
	 */
	private static void init(){
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
			Display.setTitle("Vectrix");
			Keyboard.create();
			Mouse.create();
			Sounds.setUpSound();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,800,0,600,-1,1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable (GL11.GL_BLEND);
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0, 0, 0, 1);
		
		
		
		State_Score.load();
	
		state = new State_Main();
	}
	
	/**
	 * Destroy resources, save score table
	 */
	private static void decon(){
		State_Score.save();
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
		Sounds.decon();
		AL.destroy();
		
	}

}
