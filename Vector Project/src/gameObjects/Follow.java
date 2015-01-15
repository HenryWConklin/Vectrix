package gameObjects;

import main.State_Game;
import main.VectorGeometry;
/**
 * Represents an enemy that follows its given target, generally the player
 * @author Henry
 *
 */
public class Follow extends Enemy {

	//This object's target
	private GameObject target;
	/*
	 * The angle at which the object should enter the screen relative to the player, 
	 * stored because the object may be created before it is used, making the initial 
	 * position invalid as the player moves
	 * 
	 * firstUpdate used to allow this object to reposition itself on the first update
	 */
	double entryAngle;
	private boolean firstUpdate;
	
	/**
	 * Constructor, called internally, was used in testing
	 * @param x initial x position
	 * @param y initial y position
	 * @param target GameObject to follow
	 */
	public Follow(double x, double y, GameObject target) {
		super(new VectorGeometry("/geomDefs/spikey.geom").scale(-.5), x, y);
		this.target = target;
		firstUpdate = true;
	}
	
	/**
	 * Constructor, used in enemy spawning
	 * @param player Used as the target
	 * @param entryAngle angle at which to enter the screen
	 */
	public Follow(Player player, double entryAngle) {
		this(State_Game.spawnRadius * Math.cos(entryAngle) + player.x, State_Game.spawnRadius * Math.sin(entryAngle) + player.y, player);
		this.entryAngle = entryAngle;
	}

	
	public void update(){
		//If this is the first update, put the object back at its intended original position
		if (firstUpdate){
			x = State_Game.spawnRadius * Math.cos(entryAngle) + target.x;
			y = State_Game.spawnRadius * Math.sin(entryAngle) + target.y;
			firstUpdate = false;
		}
		//Calculate and set this objects rotation to be facing the player
		double dx = target.x- this.x;
		double dy =  target.y -this.y;
		rot = GameObject.getAngle(dx, dy);
		
		//Update the velocity to be towards the player
		vx = 120 * Math.cos(rot);
		vy = 120 * Math.sin(rot);
		
		//Call GameObject update()
		super.update();
	}
	
	

}
