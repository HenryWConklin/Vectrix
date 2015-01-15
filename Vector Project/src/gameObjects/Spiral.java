package gameObjects;

import main.Start;
import main.State_Game;
import main.VectorGeometry;
/**
 * Represents an enemy which spirals in towards the player
 * @author Henry
 *
 */
public class Spiral extends Enemy{

	//Time, fed to functions
	private double time;
	//Target to spiral around, player
	private GameObject c;
	//Current radius of rotation, decreases over time to spiral in
	private double radius;
	//Period of rotation
	private double period;
	//Rate at which the radius decreases
	private double dRadius;
	//Angle offset by which the object enters the screen
	private double offset;
	
	/**
	 * Constructor, called internally
	 * @param center Object to target
	 * @param timeIn Time before the object collides with center
	 * @param rotSpeed Speed at which the object orbits the center
	 * @param stagger angle by which the object is staggered and enters the screen
	 */
	public Spiral(GameObject center, double timeIn, double rotSpeed, double stagger) {
		super(new VectorGeometry("/geomDefs/spiral.geom").scale(-.25), State_Game.spawnRadius * Math.cos(stagger) + center.x, State_Game.spawnRadius * Math.sin(stagger) + center.y);
		c = center;
		radius = State_Game.spawnRadius;
		time = 0;
		period = 2 * Math.PI/rotSpeed;
		dRadius = 1000.0/timeIn;
		vRot = 4 * Math.PI;
		offset = stagger;
		scoreValue = 3;
	}
	
	/**
	 * Constructor, called in enemy generation
	 * @param center object to target/orbit around, player
	 * @param entryAngle Angle to enter the screen
	 */
	public Spiral(Player center, double entryAngle){
		this(center, 7000, 10000, entryAngle);
	}
	
	/**
	 * Update the object's state
	 */
	public void update(){
		//Update time, not modulated because it is always destroyed soon
		time += Start.deltaTime();
		//Decrease the radius, move to center
		radius -= Start.deltaTime()*dRadius;
		
		//Set x and y position according to parametric equation
		x = radius * Math.cos((time) * period + offset) + c.x;
		y = radius * Math.sin((time) * period + offset) + c.y;
		super.update();
	}

	
}
