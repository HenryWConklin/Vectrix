package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Manages high scores
 * @author Henry
 *
 */
public class State_Score implements GameState {

	//Static list of high scores
	public static Score[] highScores = new Score[19];
	
	//Data representing an input score
	private boolean enteringName;
	private String newName;
	private int score;
	private boolean hardMode;
	
	/**
	 * Transitions to the high score table game state. If the player has a score higher than the lowest
	 * score, display a prompt for them to enter their name.
	 * Called from State_Game
	 * @param score The score the player earned 
	 * @param hard Whether the player was playing in hard mode or not
	 */
	public State_Score(int score, boolean hard){
		if (highScores[18] == null || score > highScores[18].getScore()) enteringName = true;
		else enteringName = false;
		newName = "";
		this.score = score;
		hardMode = hard;
		while(Keyboard.next());
	}
	
	/**
	 * Just brings up the high score table. Called from State_Main
	 */
	public State_Score(){
		enteringName = false;
	}
	
	/**
	 * If the player got a high enough score, ask them to enter their name
	 */
	@Override
	public void update() {
		if (enteringName){
			//Go through key press events
			while(Keyboard.next()){
				//If the key for this event was pressed
				if (Keyboard.getEventKeyState()){
					
					if (newName.length() > 0){
						/*
						 * If the player presses enter after having entered something for their name stop
						 *  the prompt and insert their score into the table.
						 */
						if (Keyboard.getEventKey() == Keyboard.KEY_RETURN){ 
							enteringName = false;
							insertScore(new Score(newName, score, hardMode));
						}
						//If the player presses backspace, delete the last character
						else if (Keyboard.getEventKey() == Keyboard.KEY_BACK) newName = newName.substring(0, newName.length()-1);
					}
					//Otherwise add letters a-z and numbers 0-9 to the name being entered
					char c = Keyboard.getEventCharacter();
					if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) newName += Keyboard.getEventCharacter();
				}
				if (newName.length() > 10) newName = newName.substring(0, 10);
			}
		}
	}

	/**
	 * Inserts the given score into the high score list at the appropriate position
	 * @param s Score to be inserted
	 */
	private void insertScore(Score s) {
		int i = 0;
		//Find the position of the first score less than this score or null value which indicates an empty spot
		while(i < highScores.length && highScores[i] != null && highScores[i].compareTo(s) > 0){
			i++;
		}
		//If there is an empty spot, just insert the score
		if (highScores[i] == null) highScores[i] = s;
		//Otherwise shift all other scores over and then insert the score
		else{
			int j = highScores.length -1;
			while (j > i){
				highScores[j] = highScores[j-1];
				j--;
			}
			highScores[i] = s;
		}
	}

	/**
	 * 
	 * Render the enter name prompt or render each high score's rank, name and value
	 */
	@Override
	public void render() {
		if (enteringName){
			new VectorString("enter name", 10).renderCentered(400, 350);
			new VectorString(newName, 10).renderCentered(400, 300);
			new VectorString("press enter to confirm", 10).renderCentered(400, 250);
		}
		else{
			if (highScores[0] == null) new VectorString("none recorded", 10).renderCentered(400, 300);
			for(int i = 0; i < highScores.length && highScores[i] != null; i++){
				new VectorString("" + (i+1), 10).render(10,  570 - i * 30);
				new VectorString(highScores[i].getName(), 10).render(80, 570 - i * 30);
				String scoreString = "" + highScores[i].getScore();
				while(scoreString.length() < 10) scoreString = "0" + scoreString;
				new VectorString(scoreString, 10).render(410,  570 - i * 30);
				new VectorString("" + (highScores[i].hardMode() ? "h" : ""), 10).render(760,  570 - i * 30);
			}
		}
	}

	/**
	 * Transition to the main menu if the mouse button is pressed
	 */
	@Override
	public GameState transitionTo() {
		if (Mouse.isButtonDown(0)) return new State_FadeTransition(this, new State_Main(), 2000);
		else return null;
	}
	
	//Destroy necessary
	@Override
	public void destroy() {
		
	}

	/**
	 * Save the list of high scores to file
	 */
	public static void save() {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(new File("scores.dat")));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		
		for (int i = 0; i < highScores.length && highScores[i] != null; i++){
			try {
				out.write("" + highScores[i].getName() + " " + highScores[i].getScore() + " " + highScores[i].hardMode());
				out.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Load the high score table from file
	 */
	public static void load(){
		Scanner in;
		try {
			in = new Scanner(new File("scores.dat"));
		} catch (FileNotFoundException e) {
			return;
		}
		int i = 0;
		while(in.hasNext() && i < 19){
			String name = in.next();
			if (name.length() > 10) name = name.substring(0, 10);
			highScores[i] = new Score(name, in.nextInt(), in.nextBoolean());
			i++;
		}
		in.close();
	}
	
	/**
	 * Internal class, represents a single score with name, value and whether it was in hard mode or not
	 * @author Henry
	 *
	 */
	private static class Score implements Comparable<Score>{
		
		String name;
		int score;
		boolean hardMode;
		
		public Score(String name, int score, boolean hardMode){
			this.name = name;
			this.score = score;
			this.hardMode = hardMode;
		}

		//Accesor methods
		public boolean hardMode() {
			return hardMode;
		}
		public String getName() {
			return name;
		}
		public int getScore(){
			return score;
		}
		
		/**
		 * Compare to another score, return the difference between scores, or the comparison between the scores' names
		 */
		@Override
		public int compareTo(Score arg0) {
			int diff =  this.getScore() - ((Score)arg0).getScore();
			if (diff == 0) return this.getName().compareTo(((Score)arg0).getName());
			else return diff;
		}
		
		
	}

}
