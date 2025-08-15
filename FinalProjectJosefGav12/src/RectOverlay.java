import java.awt.Color;
import java.awt.Rectangle;

/*
 * overlay  shows attack and movement radius visually
 * Josef gav 2024/2025
 * */

public class RectOverlay {
	
	private boolean isOverlayVisible;
	private int x;
	private int y;
	private int width; 
	private int height;
	private Color color = new Color(0, 0, 255, 102);

	
	 /**
	  * Method to show the rectangle (overlay) with a specific color
	  * */
    public void revealOverlay() {
        this.isOverlayVisible = true;
    }

    /**
     * Method to hide the rectangle (overlay)
     * */
    public void hideOverlay() {
        this.isOverlayVisible = false;
    }

    /**
     * Method to hide the rectangle (overlay)
     * */
    public boolean isOverlayVisible() {
        return isOverlayVisible;
    }


    /**
     * sets the coordinates and dimensions of the overlay
     * */
    private void setCoordinateAndDimensions(int x,int y,int width,int height) {
    	this.x  = x;
    	this.y = y;
 
    	
    	this.width = width;
    	this.height = height;
    	
    }
    
    /**
     * sets the boundry of the overlay (affects x and y)
     * */
    public void setBounds(Coordinate center, int radius, Tile[][] grid) {
    	int r = center.getRow();
    	int c = center.getCol();
    	
    	Tile currTile = grid[r][c];
    	int radiusLeft = radius;
    	int radiusUp = radius;
    	int radiusRight = radius;
    	int radiusDown = radius;
    	
    	if (c-radius < 0) radiusLeft = c;
    	if (c+radius >=grid.length) radiusRight = grid.length - c -1 ;
    	if (r-radius < 0) radiusUp = r;
    	if (r+radius >=grid.length)  radiusDown = grid.length - r -1;
    	
    	this.setCoordinateAndDimensions(currTile.getX() - radiusLeft*currTile.getWidth(),
    									currTile.getY() - radiusUp*currTile.getWidth(),
    									currTile.getWidth() * (radiusRight+radiusLeft+1),currTile.getWidth() * (radiusDown+radiusUp+1));
    }
    
    public Color getColor() {
    	return this.color;
    }
    
    public void setColor() {
    	this.color = new Color(0, 0, 255, 102);
    }
    
    public void setColor(Color c) {
    	this.color = c;
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight()
    {
    	return height;
    }
    public void resize(int width) {
    
    	this.width = width;
    	
    }
}
