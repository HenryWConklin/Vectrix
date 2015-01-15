package gameObjects;

import main.VectorGeometry;
/**
 * Provides base class for all enemies, defines collision logic and scoring for all enemies
 * @author Henry
 *
 */
public class Enemy extends GameObject{
	
	//Amount of score the player gains for killing this enemy
	protected int scoreValue;

	/**
	 * 
	 * @param g The geometry for this enemy, passed to super constructor
	 * @param x initial x position
	 * @param y initial y position
	 */
	public Enemy(VectorGeometry g, double x, double y) {
		super(g, x, y);
		scoreValue = 1;
	}
	
	/**
	 * Called when a collision is detected, handles logic for all enemies
	 * @param other Object that this object has collided with
	 * Postcondition: other not changed
	 */
	public void collide(GameObject other){
		//If the other object is a bullet that this object did not fire
		if (other instanceof Bullet && ((Bullet)other).source != this){
			
			//This object dies
			this.split(other.vx, other.vy);
			
			//If the bullet's source was the player, and the player is not dead, add to the score.
			if (((Bullet)other).source instanceof Player){ 
				Player p = (Player) ((Bullet)other).source;
				if(!p.isDead())
				GameObject.score+= this.scoreValue;
			
			}
		}
		//Die if this object collides with the player, the player handles health loss
		if (other instanceof Player){
			this.split(other.vx, other.vy);
		}
		
	}
	
	

}
