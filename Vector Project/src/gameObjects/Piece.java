package gameObjects;

import main.Start;
import main.Sounds;
import main.VectorGeometry;
/**
 * Represents a Piece of debris
 * @author Henry
 *
 */
public class Piece extends GameObject {

	//Factor by which the piece darkens over time
	double darkFactor;
	//Handle to the killed sound
	int killedSound;
	//has the source represented by killedSound been deleted
	boolean deleted;
	
	/**
	 * General constructor
	 * @param g Geometry of this Piece
	 * @param x Initial x position
	 * @param y Initial y position
	 * @param timeOut Time before the piece is deleted
	 */
	public Piece(VectorGeometry g, double x, double y, long timeOut) {
		super(g, x, y);
		if (g == null) GameObject.removeObject(this);
		
		//Sets the darken factor so that the piece fades out after timeOut milliseconds
		darkFactor = 1 - Math.pow(.01, 1.0/timeOut);
		deleted = true;
	}
	
	/**
	 * Constructor used if a sound source needs to be attached
	 * @param vectorGeometry Geometry of this object
	 * @param x Initial x position
	 * @param y Initial y position
	 * @param timeOut Time before the Piece is deleted
	 * @param killedSource Handle to a killed sound to be deleted when it is done playing
	 */
	public Piece(VectorGeometry vectorGeometry, double x, double y, int timeOut, int killedSource) {
		this(vectorGeometry, x, y, timeOut);
		killedSound = killedSource;
		deleted = false;
	}

	/**
	 * Update the state of this object
	 */
	public void update(){
		this.geom = geom.darken(darkFactor * Start.deltaTime());
		if (geom.getColor().r < .01 && geom.getColor().g < .01 && geom.getColor().b < .01) GameObject.removeObject(this);
		super.update();
		if (!deleted){
			if (!Sounds.isPlaying(killedSound)){ 
				Sounds.deleteSource(killedSound); 
				deleted = true;
			}
		}
		
	}

	/**
	 * Ignore collisions
	 */
	@Override
	public void collide(GameObject other) {
	}
	

}
