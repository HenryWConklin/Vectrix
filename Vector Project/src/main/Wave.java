package main;

import gameObjects.GameObject;
/**
 * Represents a GameObject to be added at a delay
 * @author Henry
 *
 */
public class Wave {

	//The game object to be added
	private GameObject go;
	
	//Delay at which to add the object
	private double delay;
	
	//Whether this object has been added yet
	public boolean added;
	
	/**
	 * Creates a game object that will be added after time milliseconds
	 * @param time Time before this game object will be added, in milliseconds
	 * @param enemy Game object to be added
	 */
	public Wave(double time, GameObject enemy){
		go = enemy;
		delay = time;
		added = false;
	}
	
	/**
	 * Waits for delay milliseconds, then adds go to the GameObject list and sets added to true
	 */
	public void update(){
		delay -= Start.deltaTime();
		if (delay <= 0){ 
			GameObject.addObject(go);
			added = true;
		}
	}
	
}
