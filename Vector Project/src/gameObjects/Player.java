package gameObjects;

import main.Start;
import main.Sounds;
import main.VectorGeometry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class Player extends GameObject {

	//Time, given to functions
	double time;
	
	//Manage fire rate
	long fireDelay;
	long fireRate;
	
	//Player health and booleat set if the health drops below 0
	double health;
	boolean dead;
	
	/**
	 * Creates a player at the position (x,y)
	 * @param x Initial x position
	 * @param y Initial y position
	 */
	public Player(double x, double y){
		super (new VectorGeometry("/geomDefs/geomStressTest.geom").scale(-.8),x,y);
		fireRate = 200;
		fireDelay = fireRate;
		health = 10000;
		dead = false;
		
		shotSource = Sounds.genSource(Sounds.SHOT, this.x, this.y, this.vx, this.vy);
		hurtSource = Sounds.genSource(Sounds.HURT, x, y,vx, vy);
	}
	
	/**
	 * Update the object's state, handle player input in game
	 */
	@Override
	public void update(){
		//If the player is pressing W or S accelerate to a maximum velocity in the y component of velocity
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && vy < 240) vy+=Start.deltaTime()/1000.0 * 480;
		else if (Keyboard.isKeyDown(Keyboard.KEY_S) && vy > -240) vy-=Start.deltaTime()/1000.0 * 480;
		//Otherwise decelerate to 0 velocity on the y component of velocity
		else{
			if (vy < -5) vy += Start.deltaTime()/1000.0 * 480;
			else if (vy > 5) vy -= Start.deltaTime()/1000.0 * 480;
			else vy = 0;
					}
		
		//If the player is pressing A or D accelerate to a maximum velocity in the x component of velocity
		if (Keyboard.isKeyDown(Keyboard.KEY_D) && vx < 240) vx+=Start.deltaTime()/1000.0 * 480;
		else if (Keyboard.isKeyDown(Keyboard.KEY_A) && vx > -240) vx-=Start.deltaTime()/1000.0 * 480;
		//Otherwise decelerate to 0 velocity on the x component of velocity
		else{
			if (vx < -5) vx += Start.deltaTime()/1000.0 * 480;
			else if (vx > 5) vx -= Start.deltaTime()/1000.0 * 480;
			else vx = 0;
		}
		
		//Get mouse position relative to the center of the screen
		int mouseX = Mouse.getX() - Display.getWidth()/2;
		int mouseY = Mouse.getY() - Display.getHeight()/2;
		//Set the player to face to mouse
		rot = GameObject.getAngle(mouseX, mouseY);

		//Update the position of the sound source
		Sounds.updateSource(shotSource, x, y, vx, vy);
		Sounds.updateSource(hurtSource, x, y, vx, vy);
		
		//If the player is pressing the left mouse button, try to fire
		if (Mouse.isButtonDown(0) && fireDelay <=0){
			GameObject.addObject(new Bullet(this,10, 2000));
			fireDelay = fireRate;
			
			Sounds.playSound(shotSource);
		}
		fireDelay -= Start.deltaTime();
		if (fireDelay < 0) fireDelay = 0;
		
		//Update time
		time += Start.deltaTime();
		time %= Math.PI * Math.PI * 120;
		
		super.update();
		
		//Update health
		if (health <= 0){ 
			dead = true;
			this.split(0, 0);
		}
		health += Start.deltaTime();
		if (health > 10000) health = 10000;

	}
	
	/**
	 * Render the player so that it deforms according to the player's current health.
	 * Deforms more with less health. 
	 */
	@Override
	public void render(){
		geom.scaleX((10000-health)/10000 * .25 * Math.sin(time / Math.PI / 60) + .25).scaleY((10000-health)/10000 * .25 * Math.cos(time / Math.PI / 60) + .25).rotate(rot).render(x, y);
	}
	
	/**
	 * Handle collisions
	 * @param other Object that the player has collided with
	 */
	public void collide(GameObject other){
		//If the player has collided with an enemy, subtract 4900 from health and play a hurt sound
		if (other instanceof Enemy){
			health -= 4900;
			Sounds.playSound(hurtSource);
		}
		//If the player has collided with a bullet that is not its own, subtract 3000 from health
		if (other instanceof Bullet && ((Bullet)other).source != this){ 
			health -= 3000;
			Sounds.playSound(hurtSource);
		}
	}
	
	//Provide access to instance variables
	public boolean isDead(){
		return dead;
	}
	public double getHealth(){
		return health;
	}
	public double getVX() {
		return vx;
	}
	public double getVY() {
		return vy;
	}
}
