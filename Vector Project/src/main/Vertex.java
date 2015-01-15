package main;

/**
 * Represents a 2D point at (x,y)
 * @author Henry
 *
 */
public class Vertex {
	
	public double x;
	public double y;
	
	/**
	 * 
	 * @param x x Coord
	 *
	 * @param y y Coord
	 */
	public Vertex(double x, double y){
		this.x = x; 
		this.y = y;
	}
	
	/**
	 * @return a string representation of this Vertex
	 */
	public String toString(){
		return "("+x+","+y+")";
	}
	
	/**
	 * Create a Vertex that is the sum of this vertex and another
	 * @param other Vertex to add
	 * @return Sum of this Vertex and other
	 */
	public Vertex add(Vertex other){
		return new Vertex(this.x + other.x, this.y + other.y);
	}
	
	/**
	 * Calculates the distance between this Vertex and another Vertex
	 * @param other Vertex to find the distance to
	 * @return The distance between this Vertex and other
	 */
	public double distance(Vertex other){
		return Math.sqrt(Math.pow(this.x- other.x,2) + Math.pow(this.y - other.y, 2));
	}
	
	/**
	 * Determines whether this vertex and other represent the same coordinate within .5
	 * @param other Vertex to compare this vertex to
	 * @return Whether these vertices are equal
	 */
	public boolean equals(Vertex other){
		return Math.abs(this.x - other.x) < .5 && Math.abs(this.y - other.y) < .5; 
	}
}
