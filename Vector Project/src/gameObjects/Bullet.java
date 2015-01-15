package gameObjects;

import org.newdawn.slick.Color;

import main.Start;
import main.VectorGeometry;
import main.Vertex;

/**
 * Represents a projectile fired by enemies or the player
 * @author Henry
 *
 */
public class Bullet extends GameObject {
	
	//Time the bullet lives before despawning
	long liveTime;
	//GameObject that the bullet was fired from, used to make sure that it does not collide with its firer
	protected GameObject source;
	
	/**
	 * Depracted constructor, called inside the class only
	 * @deprecated
	 * @param x initial x position
	 * @param y initial y position
	 * @param vx x component of velocity
	 * @param vy y component of velocity
	 * @param rot rotation
	 * @param scale length of the bullet
	 * @param c color of the bullet
	 * @param weight width of the bullet
	 * @param liveTime time before the bullet despawns, keeps them from collecting off screen
	 */
	public Bullet(double x, double y, double vx, double vy, double rot,  double scale, Color c, double weight, long liveTime) {
		super(new VectorGeometry(new Vertex[] {new Vertex(-.5,0), new Vertex(.5,0)},weight , c.r, c.g, c.b).scaleX(scale), x, y);
		this.rot = rot;
		this.vx = vx;
		this.vy = vy;
		this.liveTime = liveTime;
	}
	
	/**
	 * Prefered constructor, calls the depractated constructor using information from the given source
	 * @param src GameObject that the bullet was fired from
	 * @param scale size of the bullet
	 * @param liveTime time before the bullet despawns
	 */
	public Bullet(GameObject src, double scale, long liveTime){
		this(src.x + src.geom.getAverageRadius() * Math.cos(src.rot),src.y + src.geom.getAverageRadius() * Math.sin(src.rot),
				480 * Math.cos(src.rot) + src.vx, 480 * Math.sin(src.rot) + src.vy, src.rot, scale, src.geom.getColor(), 
				src.geom.getWeight(), liveTime);
		source = src;
	}
	
	
	/**
	 * Updates the bullet's state, updates despawn timer and calls the super class update method
	 */
	public void update(){
		liveTime -= Start.deltaTime();
		if (liveTime <= 0) GameObject.removeObject(this);
		super.update();
	}
	
	/**
	 * Logic for collision with another object, called when a collision is detected
	 * @param other the object that this object has collided with
	 * Postcondition: other not changed
	 */
	public void collide(GameObject other){
		if (!(other == source || other instanceof Bullet)) GameObject.removeObject(this);
	}

}
