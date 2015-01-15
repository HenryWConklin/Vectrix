package main;

/**
 * Represents a String to be displayed on screen using VectorGeometries
 * @author Henry
 *
 */
public class VectorString {
	
	//An array of VectorGeometries which represent the characters of this String
	private VectorGeometry[] chars;
	
	/**
	 * Constructs a VectorString with the given String for text and font size
	 * @param s String for this VectorString to represent
	 * @param point Font size of this VectorString
	 */
	public VectorString(String s, double point){
		chars = new VectorGeometry[s.length()];
		char[] c = s.toCharArray();
		//Load a VectorGeometry for each character of s
		for (int i = 0; i < c.length; i++){
			//If the character is a-z or 0-9, load a vector geometry for it
			if((c[i] >= 'a' && c[i] <= 'z') || (c[i] >= '0' && c[i] <= '9'))
				chars[i] = new VectorGeometry("/geomDefs/characters/" + c[i] + ".geom").scale(point).setWeight(point);
			//Otherwise set that character to null, it will be ignored in rendering
			else chars[i] = null;
		}
	}
	
	/**
	 * Draw this VectorString to the screen starting at the point (x,y)
	 * @param x x coordinate to draw this VectorString at
	 * @param y y coordinate to draw this VectorString at
	 */
	public void render(double x, double y){
		if(chars.length > 0){
			//Set the character width as the width of the first character, assume first character is not null
			double charWidth = chars[0].getBoundingRect().getWidth();
			
			//VectorGeometries render centered at the given point, so increase the x position by half of the width
			double pos = x + charWidth/2;
			
			for (int i = 0; i < chars.length; i++){
				//If the character is null, don't try to draw it but still increase the position by the width of a character
				if (chars[i] != null) chars[i].render(pos, y);
				pos += charWidth * 3 / 2;
			}
		}
	}
	
	/**
	 * Draw this VectorString to the screen centered at the point (x,y)
	 * @param x x Coordinate to draw this VectorString at
	 * @param y y Coordinate to draw this VectorString at
	 */
	public void renderCentered(double x, double y){
		if (chars.length > 0){
			
			//Use the width of the first character as the width of a character, assume first character is not null
			double charWidth = chars[0].getBoundingRect().getWidth();
			
			//Set the initial position to be the given position minus the width of half of the characters of this VectorString
			double pos = x - chars.length/2 * charWidth*3/2 + charWidth/2;
			for (int i = 0; i < chars.length; i++){
				//If the character is null, don't try to draw it but still increase the position by the width of a character
				if (chars[i] != null) chars[i].render(pos, y);
				pos += charWidth * 3 / 2;
			}
		}
		
	}
}
