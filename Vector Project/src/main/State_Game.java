package main;

import gameObjects.*;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * Manages the game, spawns enemies, updates GameObjects, renders GameObjects
 * @author Henry
 *
 */
public class State_Game implements GameState {
	
	private Player player;
	
	//List of all active GameObjects in the game
	private ArrayList<GameObject> objects;
	
	//List of enemies to be added in next wave
	private ArrayList<Wave> waveQueue;
	
	//Difficulty factor, increases over time causing more enemies to spawn
	private int diffFactor;
	
	//GUI text, says "SCORE" and "HEALTH"
	private VectorString score;
	private VectorString health;
	
	//Whether the game is in hard mode or not, causes more enemies to spawn initially, increases max difficulty cap
	private boolean hardMode;
	
	//Time between waves
	public static double waveDelay = 5000;
	//Radius of a circle that is outside the screen
	public static double spawnRadius = 500;
	
	/**
	 * Starts a new game
	 * @param hard Is the game in hard mode or not
	 */
	public State_Game(boolean hard){
		hardMode = hard;
		player = new Player(0, 0);
		objects = new ArrayList<GameObject>();
		
		objects.add(player);
		GameObject.initGameObjectList(objects);
		diffFactor = 1;
		if (hardMode) diffFactor = 25;
		waveQueue = new ArrayList<Wave>();
		updateWave();
		score = new VectorString("score", 5);
		health = new VectorString("health", 5);
	}

	/**
	 * Update all GameObjects and update the wave queue
	 */
	@Override
	public void update() {
		//Set the listener position to the player
		Sounds.updateListener(player.getX(), player.getY(), player.getVX(), player.getVY());
		
		GameObject.updateQueue();
		for(GameObject ob : objects) ob.update();
		GameObject.checkCollisions();
		GameObject.updateQueue();
		
		//If all objects in wave queue have been added, add a new wave to the queue
		if (waveQueue.size() == 0){
			updateWave();
		}
		else {
			if (waveQueue.get(0).added) waveQueue.remove(0);
			else {
				waveQueue.get(0).update();
			}
		}
		
	}

	/**
	 * Add enemies to the wave queue
	 */
	private void updateWave() {
		//Number of enemies varies based on difficulty factor
		int numEnemies = (int)(Math.random() * diffFactor/2) + diffFactor/2 +1;
		int numTypes = 4;
		int i = 0;
		while (i < numEnemies){
			ArrayList<GameObject> enemies = new ArrayList<GameObject>();
			
			//Randomly choose a type of enemy and a number of that enemy to add
			int type = (int)(Math.random() * numTypes) + 1;
			int num = (int)(Math.random() * (numEnemies-i)) + 1;
			
			//Make sure no more than 6 Spiral enemies spawn in the same wave
			if (type == 4 && num > 6){
				num = 6;
				numTypes = 3;
			}
			//Random angle to add variation to the angle at which enemies spawn
			double angle = Math.random() * 2 * Math.PI;
			for (int j = 0; j < num; j++){
				//Add enemies evenly spaced in a circle around the player
				switch (type){
				case 1:
					enemies.add(new Orbiter(player, 2 * Math.PI * j / num + .618 * Math.PI * 2 * i + angle));
					break;
				case 2:
					enemies.add(new Follow(player, 2 * Math.PI * j / num + .618 * Math.PI * 2 * i + angle));
					break;
				case 3:
					enemies.add(new Gravimetric(player, 2 * Math.PI * j / num + .618 * Math.PI * 2 * i + angle));
					break;
				case 4:
					enemies.add(new Spiral(player, 2 * Math.PI * j / num + .618 * Math.PI * 2 * i + angle));
					break;
				}
			}
			
			//Add all of the generated enemies to the wave queue
			for (int j = 0; j < enemies.size(); j++){
				if (i == 0 && j == 0 && diffFactor != 1) waveQueue.add(new Wave(5000, enemies.get(j)));
				else waveQueue.add(new Wave(0, enemies.get(j)));
			}
			i += num;
			
		}
		//Increase and cap the difficulty factor
		diffFactor += (int)( Math.random() * 2);
		if (hardMode && diffFactor > 75) diffFactor = 75;
		else if (diffFactor > 50) diffFactor = 50;
		
	}

	/**
	 * Render all the GameObjects and GUI
	 */
	@Override
	public void render() {
		GL11.glTranslated(Display.getWidth()/2 - player.getX(), Display.getHeight()/2 - player.getY(), 0);
		for(GameObject ob : objects) ob.render();
		GL11.glLoadIdentity();
		health.render(20, 580);
		new VectorString((int)(player.getHealth()/100 < 0 ? 0 : (int)(player.getHealth()/100)) +"", 5).render(140, 580);
		score.render(200, 580);
		new VectorString(GameObject.getScore()+""  , 5).render(300, 580);
	}

	/**
	 * Transition to the score screen if he player is dead
	 */
	@Override
	public GameState transitionTo() {
		if (player.isDead()){ 
			return new State_FadeTransition(this, new State_Score(GameObject.getScore(), hardMode), 3000);
		}
		else return null;
	}

	@Override
	public void destroy() {
		
		
	}

	

}
