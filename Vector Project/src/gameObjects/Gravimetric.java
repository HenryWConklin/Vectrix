package gameObjects;

import main.Start;
import main.State_Game;
import main.VectorGeometry;
import main.Vertex;
/**
 * Represents an enemy which accelerates towards the player
 * @author Henry
 *
 */
public class Gravimetric extends Enemy {

	//Object to move towards, player
	GameObject center;
	//Constant of acceleration
	private static final double GRAV_FACTOR = 1/2000.0;
	
	//Used to make sure that the initial position is current when the object is created before it is used
	boolean firstUpdate;
	double entryAngle;
	
	/**
	 * Constructor called in enemy generation
	 * @param p Reference to the player
	 * @param entryAngle angle at which to enter the screen
	 */
	public Gravimetric(Player p, double entryAngle) {
		super(new VectorGeometry("/geomDefs/asteroid.geom"), State_Game.spawnRadius * Math.cos(entryAngle) + p.x,
				State_Game.spawnRadius * Math.sin(entryAngle) + p.y);
		center = p;
		firstUpdate = true;
		this.entryAngle = entryAngle;
		scoreValue = 2;
	}
	
	/**
	 * Update the object's state
	 */
	public void update(){
		//Reset the objects posion on first update
		if (firstUpdate){
			x = State_Game.spawnRadius * Math.cos(entryAngle) + center.x;
			y = State_Game.spawnRadius * Math.sin(entryAngle) + center.y;
			firstUpdate = false;
		}
		//Find angle and distance to player
		double angleToPlayer = GameObject.getAngle(center.x - this.x, center.y - this.y);
		double distance = new Vertex(center.x, center.y).distance(new Vertex(this.x, this.y));
		
		//Accelerate towards the player, accelerate more if the player is further away
		vx += Start.deltaTime() * GRAV_FACTOR * distance * Math.cos(angleToPlayer);
		vy += Start.deltaTime() * GRAV_FACTOR * distance * Math.sin(angleToPlayer);
		
		rot = angleToPlayer;
		
		super.update();
	}

}
