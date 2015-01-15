package gameObjects;

import main.Start;
import main.Sounds;
import main.State_Game;
import main.VectorGeometry;

/**
 * Represents an enemy that orbits and shoots at the player
 * @author Henry
 *
 */
public class Orbiter extends Enemy {

	//Time, used in parametric equation to get position, modulated to period of the equation
	double time;
	//Center of the object's orbit, generally the player
	GameObject c;
	//Orbit Velocity
	double velocity;
	
	double radius;
	double targetRadius;
	
	//Time to next firing and constant which stores the amount of time between each firing
	double fireDelay;
	double fireRate;
	
	//Angle of offset to spawn at
	double offSet;

	/**
	 * Constructor, called internally
	 * @param center Object to orbit
	 * @param radius starting radius
	 * @param velocity orbit velocity
	 * @param fireRate rate of fire, given in time between shots in milliseconds
	 * @param entryAngle Angle to enter the screen at in radians
	 */
	public Orbiter(GameObject center, double radius, double velocity, double fireRate, double entryAngle) {
		super(new VectorGeometry("/geomDefs/ship.geom").scale(-.5).rotate(-Math.PI/2), 0, 0);
		c = center;
		
		this.velocity = velocity / 1000;
		this.targetRadius = radius;
		this.radius = State_Game.spawnRadius;
		time = 0;
		offSet = entryAngle;
		fireDelay = fireRate;
		this.fireRate = fireRate;
		
		shotSource = Sounds.genSource(Sounds.SHOT, x, y, vx, vy);
	
	}

	/**
	 * Constructor, called in enemy generation
	 * @param player reference to the player
	 * @param entryAngle angle at which to enter the screen
	 */
	public Orbiter(Player player, double entryAngle) {
		this(player, 250, 120, 3000, entryAngle);
	}

	/**
	 * Updates this object's state
	 */
	@Override
	public void update() {
		//Set the position according to a parametric equation
		x = radius * Math.cos(time * velocity / radius + offSet) + c.getX();
		y = radius * Math.sin(time * velocity / radius + offSet) + c.getY();
		
		//If the radius is at the target radius, set the radius to the target radius
		if (radius <= targetRadius) radius = targetRadius;
		//Otherwise decrease the radius
		else radius -= Start.deltaTime() * Math.abs(velocity);
		
		//Add to time, modulate to the period of the position function
		time += Start.deltaTime();
		time %= 2 * Math.PI * radius / velocity;
		
		//Set the rotation to be facing the player
		rot = GameObject.getAngle(c.x - this.x, c.y - this.y);
		
		//If the object can fire, create a new bullet and play the firing sound
		if (fireDelay <= 0){
			GameObject.addObject(new Bullet(this, 10, 3000));
			fireDelay = fireRate;
			Sounds.updateSource(shotSource, x, y, c.vx, c.vy);
			Sounds.playSound(shotSource);
		}
		//Update the fire timer
		fireDelay -= Start.deltaTime();
		
	}

}
