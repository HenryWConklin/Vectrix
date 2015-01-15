package gameObjects;

import java.util.ArrayList;
import java.util.Scanner;

import main.Start;
import main.Sounds;
import main.VectorGeometry;

/**
 * Represents a game object, provides some common instance methods
 * Also contains some static methods to manage score and object interaction
 * @author Henry
 *
 */
public abstract class GameObject {
	
	//List of all active GameObjects
	private static ArrayList<GameObject> gameObjects;

	//Queues to avoid adding or removing objects in the middle of an update sequence
	private static ArrayList<GameObject> queue;
	private static ArrayList<GameObject> removeQueue;
	
	//Stores the score for this game
	protected static int score;

	//The geometry (shape) of this GameObject
	protected VectorGeometry geom;
	
	//Position
	protected double x;
	protected double y;
	
	//Velocity
	protected double vx;
	protected double vy;
	
	//Rotation and velocity of rotation
	protected double rot;
	protected double vRot;
	
	//Handles to OpenAL sound sources, used to play sound
	protected int shotSource;
	protected int killedSource;
	protected int hurtSource;

	/**
	 * 
	 * @param geomPath Path to a geometry definition file
	 * @param x Initial x position
	 * @param y Initial y position
	 */
	public GameObject(String geomPath, double x, double y){
		geom = new VectorGeometry(new Scanner(this.getClass().getResourceAsStream(geomPath)));
		this.x = x;
		this.y = y;
		this.vx = 0;
		this.vy = 0;
		rot = 0;
		vRot = 0;
	}
	
	/**
	 * 
	 * @param g pre-created VectorGeometry for this object
	 * @param x initial x position
	 * @param y initial y position
	 */
	public GameObject(VectorGeometry g, double x, double y){
		geom = g;
		this.x = x;
		this.y = y;
		this.vx = 0;
		this.vy = 0;
		rot = 0;
		vRot = 0;
	}
	
	/**
	 * Updates the game object's state, modifies position and rotation according to velocity
	 */
	public void update(){
		x += vx * Start.deltaTime()/1000.0;
		y += vy * Start.deltaTime()/1000.0;
		rot += vRot * Start.deltaTime()/1000.0;
		rot %= 2 * Math.PI;
	}
	
	/**
	 * Renders this GameObject with its current rotation and at its current position
	 */
	public void render(){
		if (geom == null){
			GameObject.removeObject(this);
			return;
		}
		geom.rotate(rot).render(x, y);
	}
	
	/**
	 * 
	 * @return Current x position
	 */
	public double getX(){
		return x;
	}
	/**
	 * 
	 * @return Current y position
	 */
	public double getY(){
		return y;
	}
	
	/**
	 * Handles object collisions, called internally in static checkCollisions() method
	 * @param other Object that this object has collided with
	 */
	public abstract void collide(GameObject other);
	
	/**
	 * Kills this enemy, splits it into Peices which eventually fade away and are removed
	 * @param impactVX Velocity of the object that killed this object
	 * @param impactVY Velocity of the object that killed this object
	 */
	public void split(double impactVX, double impactVY){
		//Play killed sound
		killedSource = Sounds.genSource(Sounds.KILLED, this.x, this.y, this.vx, this.vy);
		Sounds.playSound(killedSource);
		
		//Free memory used by sound sources
		Sounds.deleteSource(shotSource);
		Sounds.deleteSource(hurtSource);
		
		//Split this object's geometry into pieces and create Piece objects
		ArrayList<GameObject> peices = new ArrayList<GameObject>();
		VectorGeometry[] geoms = geom.split();
		
		/* Attach the handle to the killed sound to one of the peices so that it will finish 
		 * playing before being destroyed to clear memory
		 * 
		 */
		Piece peice = new Piece(geoms[0], x, y, 5000, killedSource);
		//Add some variation in velocity to the Piece
		peice.vx = vx + impactVX/2 + Math.random() * 20;
		peice.vy = vy + impactVY/2 + Math.random() * 20;
		peice.rot = rot;
		peices.add(peice);
		
		/*
		 * Create a Piece using each element of geoms, which is an array of
		 * the pieces of this object's original geometry
		 */
		for (int i = 1; i < geoms.length; i++){
			peice = new Piece(geoms[i], x, y, 5000);
			peice.vx = vx + impactVX/2 + Math.random() * 20;
			peice.vy = vy + impactVY/2 + Math.random() * 20;
			peice.rot = rot;
			peices.add(peice);
		}
		//Add all the peices, remove this object
		GameObject.addAllObjects(peices);
		GameObject.removeObject(this);
	}
	
	/**
	 * Adds an object to the queue to be added to the list of GameObjects when this
	 * update sequence ends
	 * @param o the object to be added
	 * @return Whether the object was sucessfully added
	 */
	public static boolean addObject(GameObject o){
		if (gameObjects == null) return false;
		
		queue.add(o);
		return true;
	}
	
	/**
	 * Add a list of objects to the queue to be added to the list of GameObjects
	 * when this update sequence ends
	 * @param os list of objects to be added
	 * @return Whether the objects were successfully added
	 */
	public static boolean addAllObjects(ArrayList<GameObject> os){
		if(gameObjects == null) return false;
		return queue.addAll(os);
	}
	
	/**
	 * Initialize the GameObject list and queues when the game starts
	 * @param gos Reference to the list of GameObjects
	 */
	public static void initGameObjectList(ArrayList<GameObject> gos){
		gameObjects = gos;
		queue = new ArrayList<GameObject>();
		removeQueue = new ArrayList<GameObject>();
		score = 0;
	}
	
	/**
	 * Finds the last index of a Piece in the GameObject list so that other objects
	 * can be added on top
	 * @return index of the last Piece in the GameObject list
	 */
	public static int lastPeiceIndex(){
		for (int i = gameObjects.size()-1; i >= 0; i--){
			if (gameObjects.get(i) instanceof Piece) return i;
		}
		return -1;
	}
	
	/**
	 * Destroy the GameObject lists when the game ends
	 */
	public static void destroyGameObjectList(){
		gameObjects = null;
		queue = null;
		removeQueue = null;
	}
	
	/**
	 * Add all of the objects in the queue to the GameObject list, remove
	 * all objects in the removeQueue from the list of GameObjects
	 */
	public static void updateQueue() {
		queue.removeAll(removeQueue);
		if (queue.size() > 0 && queue.get(0) instanceof Piece) gameObjects.addAll(lastPeiceIndex()+1, queue);
		else gameObjects.addAll(gameObjects.size()-1,queue);
		queue.clear();
		gameObjects.removeAll(removeQueue);
		removeQueue.clear();
	}
	
	/**
	 * Add an object to the removeQueue to be removed from the GameObject
	 * list when this update sequence ends
	 * @param o object to be removed
	 * @return Whether the object was successfully added to the queue
	 */
	public static boolean removeObject(GameObject o){
		if (gameObjects == null) return false;
		removeQueue.add(o);
		return true;
	}
	
	/**
	 * Check for and handle collisions between objects
	 */
	public static void checkCollisions(){
		if (gameObjects == null) return;
		for (int i = 0; i < gameObjects.size()-1; i++){
			for (int j = i+1; j < gameObjects.size(); j++){
				GameObject g1 = gameObjects.get(i);
				GameObject g2 = gameObjects.get(j);
				
				//If either object is a Piece, ignore the collision
				if (!(g1 instanceof Piece || g2 instanceof Piece)){
					
					//If the distance between the object is less than the sum of radiuses of their geometries, do a collision
					double dist = Math.sqrt(Math.pow(g1.x - g2.x, 2) + Math.pow(g1.y - g2.y, 2));
					if (dist < g1.geom.getAverageRadius() + g2.geom.getAverageRadius()){
						g1.collide(g2);
						g2.collide(g1);
					}
				}
			}
		}
	}
	
	/**
	 * Utility method to find the angle between two points
	 * @param dx distance between the points on the x axis
	 * @param dy distance between the points on the y axis
	 * @return Angle between the points in radians in the range [-PI,PI]
	 */
	public static double getAngle(double dx, double dy){
		double ang = 0;
		//If dx is 0, the angle is vertical either up or down depending on dy
		if(dx == 0){
			if (dy > 0) ang = Math.PI/2;
			else ang = -Math.PI/2;
		}
		//If dy is 0, the angle is horizontal either left or right depending on dx
		else if(dy == 0){
			if (dx > 0) ang = 0;
			if (dx < 0) ang = Math.PI;
		}
		//Otherwise, the angle is equal to the arctangent of the slope between the points
		else{
			ang = Math.atan(dy/dx);
			//if dx is negative, the angle is to the left and either up or down depending on dy
			if (dx < 0 && dy > 0) ang += Math.PI;
			else if (dx < 0 && dy < 0) ang -= Math.PI;
		}
		return ang;
	}
	
	/**
	 * 
	 * @return the current score of the game
	 */
	public static int getScore() {
		return score * 10;
	}
}
