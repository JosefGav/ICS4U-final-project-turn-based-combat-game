import java.awt.Color;

/*
 * Basic class stores important information about a line such as start and end coordinates, thickness, color, etc
 * Josef Gavronskiy ICS4U FINAL PROJ
 * 2024/2025
 * */

public class Line {
	// a (start x and y)
	private int ax;
	private int ay;
	
	// b (end x and y)
	private int bx;
	private int by;
	
	// start and end coordinates
	private Coordinate start;
	private Coordinate end;
	
	// visual fields
	private Color color;
	private int thickness;
	private double opacity = 0.35;
	
	
	private boolean isVisible;
	
	public Line(Coordinate start, Coordinate end, Tile[][] grid) {
		// sets start and end coordinates
		this.start = start;
		this.end = end;
		
		// generates x and y location based of row and column start and end coordinates
		this.resetCoordinates(grid);
		
		this.thickness = 2;
		this.color = new Color(0,0,0,(int)(opacity*255)); // black by default
	}

	public Line(Coordinate start, Coordinate end, int thickness, double opacity,Color c, Tile[][] grid) {
		// sets start and end coordinates
		this.start = start;
		this.end = end;
		
		// generates x and y location based of row and column start and end coordinates
		this.resetCoordinates(grid);
		
		this.thickness = thickness;
		this.color = c;
		this.opacity = c.getAlpha()/255.0;
	}
	
	/**
	 * sets the x and y location of the line based on the row and column of the start and end coordinates
	 * */
	public void resetCoordinates(Tile[][] grid) {
		// 1 (start) , 2 (end)
		Tile t1 = grid[this.start.getRow()][this.start.getCol()];
		Tile t2 = grid[this.end.getRow()][this.end.getCol()];

		// sets coordinates
		this.ax = t1.getX() + t1.getWidth()/2;
		this.ay = t1.getY() + t1.getWidth()/2;
		
		this.bx = t2.getX() + t2.getWidth()/2;
		this.by = t2.getY() + t2.getWidth()/2;
	}
	
	/**
	 * Sets the start coordinate of the line
	 * */
	public void setStartCoordinate(Coordinate c) {
		this.start = c;
	}
	
	/**
	 * Sets the end coordinate of the line
	 * */
	public void setEndCoordinate(Coordinate c) {
		this.end = c;
	}
	
	/**
	 * Returns if the line is visible or not.
	 * */
	public boolean isVisible(){
		return this.isVisible;
	}
	
	/**
	 * Sets the lines visibility to false
	 * */
	public void hide() {
		this.isVisible = false;
	}
	
	/**
	 * Sets the lines visibility to true
	 * */
	public void show() {
		this.isVisible = true;
	}
	
	/**
	 * Toggles the lines visibility
	 * */
	public void toggle() {
		if (isVisible()) hide();
		else show();
	}
	
	/**
	 * sets the color of the line
	 * */
	public void setColor(Color c) {
		this.color = c;
	}
	
	/**
	 * returns the lines color
	 * */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the thickness of the line
	 * */
	public void setThickness(int th) {
		this.thickness = th;
	}
	
	/**
	 * returns the thickness of the line
	 * */
	public int getThickness() {
		return this.thickness;
	}
	
	/**
	 * returns the x coordinate of the start of the line
	 * */
	public int getStartX() {
		return this.ax;
	}
	
	/**
	 * returns the y coordinate of the start of the line
	 * */
	public int getStartY() {
		return this.ay;
	}
	
	/**
	 * returns the x coordinate of the end of the line
	 * */
	public int getEndX() {
		return this.bx;
	}
	
	/**
	 * returns the y coordinate of the end  of the line
	 * */
	public int getEndY() {
		return this.by;
	}
}
