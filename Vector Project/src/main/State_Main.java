package main;

import java.awt.geom.Rectangle2D;

import org.lwjgl.input.Mouse;

/**
 * Game State that represents the main menu
 * @author Henry
 *
 */
public class State_Main implements GameState {
	
	private static boolean hardMode = false;
	
	//Menu options
	private VectorGeometry title;
	private VectorGeometry play;
	private VectorGeometry scores;
	private VectorGeometry oTitle;
	private VectorGeometry oPlay;
	private VectorGeometry oScores;
	private VectorString copyright;
	private VectorString hardModeStr;
	
	//Used to deform menu options
	private double time;
	
	//Whether the mouse was pressed in the last frame
	private boolean mousePressed;
	
	//Handles to sound sources that play when menu options are selected
	private int playSource;
	private int scoresSource;

	//Whether the select sound has been played for the Play menu option
	private boolean playPlayed;
	//Whether the select sound has been played for the Scores menu option
	private boolean scoresPlayed;
	 
	/**
	 * Create a new Main menu game state
	 */
	public State_Main(){
		
		//Load the original menu option geometries
		oTitle = new VectorGeometry("/geomDefs/vectrix.geom").scale(-.25);
		oPlay = new VectorGeometry("/geomDefs/play.geom").scale(-.5);
		oScores = new VectorGeometry("/geomDefs/scores.geom").scale(-.75);
		
		//Set new menu options to copies of the original menu options
		play = oPlay.scale(0);
		title = oTitle.scale(0);
		scores = oScores.scale(0);
				
		//Load GUI strings displayed at the bottom of the screen
		copyright = new VectorString("henry conklin 2013", 5);
		hardModeStr = new VectorString("hard mode", 5);
		
		//Set the position of the sound listener
		Sounds.updateListener(400, 300, 0, 0);
		
		//Load sound effects
		playSource = Sounds.genSource(Sounds.SHOT, 400, 350, 0, 0);
		playPlayed = false;
		scoresSource = Sounds.genSource(Sounds.SHOT, 400, 250, 0, 0);
		scoresPlayed = false;
	}
	
	@Override
	public void update() {
		
		//Update time
		time+= Start.deltaTime();
		time %= 5000;
		
		//Deform menu options based on time and whether or not they are currently selected
		title = oTitle.rotate(Math.PI/24 * Math.sin(time * Math.PI * 2 / 5000));
		if (play.translate(400, 350).getBoundingRect().contains(Mouse.getX(), Mouse.getY())){
			if (!playPlayed)Sounds.playSound(playSource);
			play = oPlay.scale(.5);
			playPlayed = true;
		}
		else {
			playPlayed = false;
			play = oPlay.scale(.25 * Math.sin(time * Math.PI * 2 / 5000) + .25);
		
		}
		if (scores.translate(400, 250).getBoundingRect().contains(Mouse.getX(), Mouse.getY())){
			if (!scoresPlayed)Sounds.playSound(scoresSource);
			scores = oScores.scale(.5);
			scoresPlayed = true;
		}
		else {
			scores = oScores.scale(.25 * Math.sin(time * Math.PI * 2 / 5000) + .25);
			scoresPlayed = false;
		}
		
		//Check if the player has clicked on the hard mode option
		Rectangle2D.Double hardRect = new Rectangle2D.Double(450, 10, 200, 10);
		if (mousePressed && !Mouse.isButtonDown(0)){
			if(hardRect.contains(Mouse.getX(), Mouse.getY())){
				hardMode = !hardMode;
				Sounds.playSound(scoresSource);
			}
			mousePressed = false;
		}
		if (hardRect.contains(Mouse.getX(), Mouse.getY()) && Mouse.isButtonDown(0)) mousePressed = true;;
	}

	/**
	 * Render the menu options
	 */
	@Override
	public void render() {
		title.render(400, 530);
		play.render(400, 350);
		scores.render(400, 250);
		copyright.render(20, 10);
		hardModeStr.render(450, 10);
		new VectorString((hardMode ? "y" : "n"), 5).render(630, 10);
	}

	/**
	 * Return a transition if the player has clicked on one of the menu options
	 */
	@Override
	public GameState transitionTo() {
		if (play.translate(400,350).getBoundingRect().contains(Mouse.getX(), Mouse.getY()) && Mouse.isButtonDown(0)){
			Sounds.playSound(playSource);
			return new State_FadeTransition(this, new State_Game(hardMode), 2000);
		}
		if (scores.translate(400,250).getBoundingRect().contains(Mouse.getX(), Mouse.getY()) && Mouse.isButtonDown(0)){ 
			Sounds.playSound(scoresSource);
			return new State_FadeTransition(this, new State_Score(), 2000);
			
		}
		else return null;
	}

	/**
	 * Free resources used by this game state
	 */
	@Override
	public void destroy() {
		Sounds.deleteSource(playSource);
		Sounds.deleteSource(scoresSource);
		
	}

}
