package main;
import static org.lwjgl.opengl.GL11.*;
import gameObjects.GameObject;

import org.newdawn.slick.Color;
/**
 * Draws VectorGeometries 
 * @author Henry
 *
 */
public class Draw {
	
	/**
	 * Draws the given VectorGeometry to the screen using rectangles of width 'weight'
	 * between each vertex in 'verts'
	 * @param vg VectorGeometry to draw
	 */
	public static void geometry(VectorGeometry vg){
		
		//Values from the VectorGeometry
		Vertex[] verts = vg.getVerts();
		Color color = vg.getColor();
		double weight = vg.getWeight();
		double r = color.r;
		double g = color.g;
		double b = color.b;
		
		//Store vertexes from previous rectangles to cover gaps between rectangles
		Vertex lastp4 = null,lastp6=null, firstp3=null, firstp5 = null;
		
		for (int i = 0; i < verts.length-1; i++){
			
			//End points of the rectangle
			Vertex p1 = verts[i];
			Vertex p2 = verts[i+1];
			
			//Get the angle that is perpendicular to the angle between the end points
			double degree = GameObject.getAngle(p1.y-p2.y, p2.x - p1.x);
			
			//Create the points on either side of the end points
			Vertex p3,p4,p5,p6;
			double dx,dy;
				
			dx    = (double)Math.cos(degree)*weight/2;
			dy    = (double)Math.sin(degree)*weight/2;
			p3 = new Vertex(p1.x+dx, p1.y+dy);
			p4 = new Vertex(p2.x+dx, p2.y+dy);
			p5 = new Vertex(p1.x-dx, p1.y-dy);
			p6 = new Vertex(p2.x-dx, p2.y-dy);
			
			
			//Draw the rectangle
			glBegin(GL_QUADS);
				glColor3d(r,g,b);
				glVertex2d(p1.x,p1.y);
				glVertex2d(p2.x,p2.y);
				glColor4d(0,0,0,0);
				glVertex2d(p4.x,p4.y);
				glVertex2d(p3.x,p3.y);
				
				glColor3d(r,g,b);
				glVertex2d(p1.x,p1.y);
				glVertex2d(p2.x,p2.y);
				glColor4d(0,0,0,0);
				glVertex2d(p6.x,p6.y);
				glVertex2d(p5.x,p5.y);
				
			glEnd();
			
			//If not on the first rectangle, cover the gap between this rectangle and the last rectangle
			if (i!=0){
			
				//Find the direction of the gap
				double angle1 = GameObject.getAngle(p2.x - p1.x, p2.y - p1.y);
				double angle2 = GameObject.getAngle(verts[i-1].x - p1.x, verts[i-1].y - p1.y);
				if (angle1 < 0) angle1 += 2 * Math.PI;
				if (angle2 < 0) angle2 += 2 * Math.PI;
				double direction = (angle1 + angle2)/2;
				if (Math.abs(direction - angle1) < Math.PI/2) direction += Math.PI;
				while (direction > Math.PI) direction -= 2 * Math.PI;
				while (direction < -Math.PI) direction += 2 * Math.PI; 

				Vertex pa = null;
				Vertex pb = null;
				
				//Set pa and pb to the vertices closest to a point closest to a point created in the direction of the gap
				Vertex d = new Vertex(p1.x + weight*Math.cos(direction), p1.y + weight*Math.sin(direction));
				if (p3.distance(d) < p5.distance(d)) pa = p3;
				else pa = p5;
				if (lastp4.distance(d) < lastp6.distance(d)) pb = lastp4;
				else pb = lastp6;
				
				//Draw a triangle between the center, pa and pb to cover the gap
				glBegin(GL_TRIANGLES);
				glColor3d(r,g,b);
				glVertex2d(p1.x, p1.y);
				glColor4d(0,0,0,0);
				glVertex2d(pa.x, pa.y);
				glVertex2d(pb.x, pb.y);
				glEnd();

				
			}
			
			//Save last outside points for use on next iteration
			lastp4 = p4;
			lastp6 = p6;
			//Save first points for use below
			if (i==0){
				firstp5 = p5;
				firstp3 = p3;
			}
			
		}
		
		//If the first Vertex is the same as the last vertex, cover the gap between them (same code as above)
		if (verts[0].equals(verts[verts.length-1])){

			Vertex p1 = verts[0];
			Vertex p2 = verts[1];
			
			int i = verts.length-1;
			
			double angle1 = GameObject.getAngle(p2.x - p1.x, p2.y - p1.y);
			double angle2 = GameObject.getAngle(verts[i-1].x - p1.x, verts[i-1].y - p1.y);
			if (angle1 < 0) angle1 += 2 * Math.PI;
			if (angle2 < 0) angle2 += 2 * Math.PI;
			double direction = (angle1 + angle2)/2;
			if (Math.abs(direction - angle1) < Math.PI/2) direction += Math.PI;
			while (direction > Math.PI) direction -= 2 * Math.PI;
			while (direction < -Math.PI) direction += 2 * Math.PI; 

			Vertex pa;
			Vertex pb;
			
			Vertex d = new Vertex(p1.x + weight*Math.cos(direction), p1.y + weight*Math.sin(direction));
			if (firstp3.distance(d) < firstp5.distance(d)) pa = firstp3;
			else pa = firstp5;
			if (lastp4.distance(d) < lastp6.distance(d)) pb = lastp4;
			else pb = lastp6;
			
			glBegin(GL_TRIANGLES);
			glColor3d(r,g,b);
			glVertex2d(verts[0].x, verts[0].y);
			glColor4d(0,0,0,0);
			glVertex2d(pa.x, pa.y);
			glVertex2d(pb.x, pb.y);
			glEnd();
			
		}
	}
}
