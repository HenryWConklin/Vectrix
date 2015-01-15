package main;


import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Scanner;

import org.newdawn.slick.Color;

/**
 * Represents a shape defined by a list of vertices. Contains methods for deforming the VectorGeometry
 * @author Henry
 *
 */
public class VectorGeometry {
	

	//List of vertices
	private Vertex[] verts;
	//Color
	private double r;
	private double g;
	private double b;
	//Width of rectangles between vertices
	private double weight;

	/**
	 * Loads the vector geometry from the given file path.
	 * @param sourcePath path to a geometry definition
	 */
	public VectorGeometry(String sourcePath){
		
		if (sourcePath == ""){
			verts = new Vertex[0];
			return;
		}
		parse(sourcePath);
	}
	
	/**
	 * Loads a vector geometry from the given scanner
	 * @param source scanner to use to read the geometry definition
	 */
	public VectorGeometry(Scanner source){
		parse(source);
	}
	
	/**
	 * Defines a new Vector geometry by parts
	 * @param v An array of vertices to use as the list of points for this geometry
	 * @param w The weight of the rectangle connecting the points of this geometry
	 * @param red A value between 0 and 1 representing the amount of red in the color of this geometry
	 * @param green A value between 0 and 1 representing the amount of green in the color of this geometry
	 * @param blue A value between 0 and 1 representing the amount of blue in the color of this geometry
	 */
	public VectorGeometry(Vertex[] v, double w, double red, double green, double blue){
		verts = v;
		r = red;
		g = green;
		b = blue;
		weight = w;
	}

	/**
	 * Creates a scanner from the given path, calls parse(Scanner)
	 * @param sourcePath Path to a geometry defintion
	 */
	private void parse(String sourcePath){
		Scanner in;
		in = new Scanner(this.getClass().getResourceAsStream(sourcePath));
		parse(in);
	}
	
	/**
	 * Loads a vector geometry from file
	 * @param in Scanner that refers to a geometry definition
	 */
	private void parse(Scanner in) {
		
		r = in.nextFloat();
		g = in.nextFloat();
		b = in.nextFloat();
		weight = in.nextFloat();
		int numVerts = in.nextInt();
		verts = new Vertex[numVerts];
		for (int i = 0; i < numVerts; i++){
			verts[i] = new Vertex(in.nextFloat(), in.nextFloat());
		}
		in.close();
	}
	
	/**
	 * Draws this vector geometry at the given coordinates
	 * @param x
	 * @param y
	 */
	public void render(double x, double y){
		Draw.geometry(this.translate(x, y));
	}
	
	//Accessor methods
	public Vertex[] getVerts(){
		return verts;
	}
	public Color getColor(){
		return new Color((float)r,(float)g,(float)b);
	}
	public double getWeight(){
		return weight;
	}
	
	/**
	 * Splits the vectorGeometry into pieces
	 * @return An array of the pieces that this geometry was split into
	 */
	public VectorGeometry[] split(){
		ArrayList<VectorGeometry> result = new ArrayList<VectorGeometry>();
		
		//The current vertex
		int i = 0;
		
		while (i < verts.length){
			//Randomly selects a number of vertices to use in the next piece
			int numVerts = (int)(Math.random() * (verts.length-1)/2)+1;
			
			//Limit numVerts so that it does not go past the last vertex
			if (i + numVerts >= verts.length) numVerts = verts.length-1-i;
			
			//End the loop if numVerts is 0, when all vertices have been used
			if (numVerts == 0) break;
			
			//Add the numVerts vertices to an array
			Vertex[] v = new Vertex[numVerts+3];
			v[0] = verts[i];
			for (int j = 1; j <= numVerts; j++){
				v[j] = verts[i+j];
			}
			//Add (0,0) and verts[i] to the list to make it a closed shape and make it appear to split from the center
			v[v.length-2] = new Vertex(0,0);
			v[v.length-1] = verts[i];
			
			i+= numVerts;
			result.add(new VectorGeometry(v,weight, r,g,b));
		}
		return result.toArray(new VectorGeometry[] {});
	}
	
	/**
	 * Translates and returns a copy of this VectorGeometry centered at (x,y),
	 * does not affect this VectorGeometry
	 * @param x x coordinate to translate to
	 * @param y y coordinate to translate to
	 * @return A vector geometry centered at (x,y)
	 */
	public VectorGeometry translate(double x, double y){
		Vertex[] v = new Vertex[verts.length];
		for (int i = 0; i < verts.length; i++){
			v[i]= verts[i].add(new Vertex(x,y));
		}
		return new VectorGeometry(v,weight,r,g,b);
	}
	
	/**
	 * Scales this VectorGeometry along the x axis and returns a copy, does not affect
	 * this VectorGeometry
	 * @param factor Amount by which to scale this vector geometry, factor = 0 indicates
	 *  no scale, factor > 0 indicates larger, factor < 0 indicates smaller
	 * @return A vector geometry scaled by factor along the x axis
	 */
	public VectorGeometry scaleX(double factor){
		Vertex[] v = new Vertex[verts.length];
		for (int i = 0; i < verts.length; i++){
			v[i] = new Vertex(verts[i].x * (1+factor), verts[i].y);
		}
		return new VectorGeometry(v,weight,r,g,b);
	}
	
	/**
	 * Scales this VectorGeometry along the y axis and returns a copy,
	 * does not affect this VectorGeometry
	 * @param factor Amount by which to scale this vector geometry, factor = 0 indicates
	 *  no scale, factor > 0 indicates larger, factor < 0 indicates smaller
	 * @return A vector geometry scaled by factor along the y axis
	 */
	public VectorGeometry scaleY(double factor){
		Vertex[] v = new Vertex[verts.length];
		for (int i = 0; i < verts.length; i++){
			v[i] = new Vertex(verts[i].x, verts[i].y * (1+factor));
		}
		return new VectorGeometry(v,weight,r,g,b);
	}
	
	/**
	 * Scales this VectorGeometry along both the x and y axis and returns a copy,
	 * does not affect this VectorGeometry
	 * @param factor Amount by which to scale this vector geometry, factor = 0 indicates
	 *  no scale, factor > 0 indicates larger, factor < 0 indicates smaller
	 * @return A vector geometry scaled by factor
	 */
	public VectorGeometry scale(double factor){
		Vertex[] v = new Vertex[verts.length];
		for (int i = 0; i < verts.length; i++){
			v[i] = new Vertex(verts[i].x * (1+factor), verts[i].y * (1+factor));
		}
		return new VectorGeometry(v,weight,r,g,b);
	}
	
	/**
	 * Rotates this VectorGeometry about (0,0), returns a copy, does not affect this VectorGeometry
	 * @param angle Angle by which to rotate this VectorGeometry, in radians
	 * @return A VectorGeometry rotated by angle radians
	 */
	public VectorGeometry rotate(double angle){
		Vertex[] v = new Vertex[verts.length];
		for (int i = 0; i < verts.length; i++){
			double x = (Math.cos(angle) * (verts[i].x) - Math.sin(angle) * (verts[i].y));
			double y = (Math.sin(angle) * (verts[i].x) + Math.cos(angle) * (verts[i].y));
			v[i] = new Vertex(x,y);
		}
		return new VectorGeometry(v, weight,r,g,b);
	}
	
	/**
	 * Darkens the color of this VectorGeometry
	 * @param factor Factor by which to darken this VectorGeometry, 
	 * may cause errors in rendering if factor is less than 0
	 * @return A copy of this VectorGeometry darkened by factor
	 */
	public VectorGeometry darken(double factor){
		return new VectorGeometry(verts, weight, r * (1-factor), g * (1-factor), b* (1-factor));
	}
	
	/**
	 * Calculates the average distance of the vertices in this VectorGeometry from (0,0)
	 * @return The average distance of the vertices in verts from (0,0)
	 */
	public double getAverageRadius(){
		double sum = 0;
		for (Vertex v : verts){
			sum += Math.sqrt(v.x*v.x + v.y*v.y);
		}
		return sum/verts.length;
	}
	
	/**
	 * Finds the maximum and minimum x and y coordinates in verts, creates a rectangle
	 * that encloses all points and is in line with the x and y axis
	 * @return A rectangle with the minimum x value and minimum y value for the top left corner
	 * and width and height that place the bottom right corner at the maximum x value and maximum y value
	 */
	public Rectangle2D getBoundingRect(){
		double minX = verts[0].x;
		double minY = verts[0].y;
		double maxX = verts[0].x;
		double maxY = verts[0].y;
		for (int i = 1; i < verts.length; i++){
			if (verts[i].x < minX) minX = verts[i].x;
			else if (verts[i].x > maxX) maxX = verts[i].x;
			if (verts[i].y < minY) minY = verts[i].y;
			else if (verts[i].y > maxY) maxY = verts[i].y;
		}
		return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
		
	}
	
	/**
	 * Create a new  VectorGeometry that is a copy of this VectorGeometry but
	 * has a new Weight
	 * @param w New weight
	 * @return A new VectorGeometry with weight w and other parameters copies of this VectorGeometry
	 */
	public VectorGeometry setWeight(double w){
		return new VectorGeometry(verts, w, r,g,b);
	}
	
	
}
