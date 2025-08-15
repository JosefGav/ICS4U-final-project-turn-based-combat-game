import java.awt.Color;
import java.awt.Label;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/*
 * a tile can be a cover, obstruction(grass), or empty
 * it can also house a playable unit
 * josef gavronskiy ics4u 
 * 2024/2025
 * */

public class Tile {
	
	private int row;
	private int col;
	
	// local x and y of top left corner
	private int x;
	private int y;
	
	private ImageIcon overlayImage; // ex. fog, grass, or cover
	private JLabel overlayImageLabel;
	private Rectangle rect;
	// rect bottom
	
	private int width;
		
	public enum TileType {COVER, VISION_OBSTRUCTION, PLAIN};
	private TileType type;
	
	private PlayableUnit occupyingCharacter; // character that occupies this tile
	private ImageIcon occupyingCharacterImg;
	private JLabel characterOverlayImageLabel;  // use this instead it shoule be 1st layer do after i eat
	
	public ArrayList<Tile> adjacent;
	
	
	public Tile(int r, int c, TileType type) {
		row = r;
		col = c;
		
		
		this.adjacent = new ArrayList<Tile>();
		
		// Label for displaying the character image
		overlayImageLabel = new JLabel();
		overlayImageLabel.setSize(200, 200); // Set the size of the image label (I will make this relative to screen size in next version of the game)
		overlayImageLabel.setLocation(width / 2 - overlayImageLabel.getWidth() / 2, width / 2); // Center the image label
		
		// you can change order of overlayimage label and character overlay image label to change priority of them showing up
		characterOverlayImageLabel = new JLabel();
		characterOverlayImageLabel.setSize(200, 200); // Set the size of the image label (I will make this relative to screen size in next version of the game)
		characterOverlayImageLabel.setLocation(width / 2 - characterOverlayImageLabel.getWidth() / 2, width / 2); // Center the image label
        
        
        this.type = type;
		
      
	}

	
	public JLabel getOverlayImageLabel() {
		return overlayImageLabel;
	}
	public JLabel getCharacterOverlayLabel() {
		return characterOverlayImageLabel;
	}

    
    /**
     * Sets x, y coordinates and dimensions of image label
     * */
    public void setCoordinateAndDimensions(int x,int y,int width) {
    	this.x  = x;
    	this.y = y;

    	
    	this.width = width;
    	
		overlayImageLabel.setSize(width, width); // Set the size of the image label (I will make this relative to screen size in next version of the game)
		overlayImageLabel.setLocation(x,y); // Center the image label
		
		characterOverlayImageLabel.setSize(width, width); // Set the size of the image label (I will make this relative to screen size in next version of the game)
		characterOverlayImageLabel.setLocation(x,y); // Center the image label
    }
   
    public int getRow() {
    	return row;
    }
    
    public int getCol() {
    	return col;
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
    
    public TileType getType() {
    	return type;
    }
     
    
    /**
     * converts number to tiletype. 0 : plain 1: cover 2: obstruction
     * */
    public static TileType toType (int i) {
    	switch (i) {
    		case 0: return TileType.PLAIN;
    		case 1: return TileType.COVER;
    		case 2: return TileType.VISION_OBSTRUCTION;
    	}
    	return TileType.PLAIN;
    	
    }
    
    /**
     * adds a unit to this tile
     * */
    public void addUnit(PlayableUnit p) {
    	if (this.occupyingCharacter!= null || !this.isVisitable()) return;
    	p.currentTile = this;
    	this.occupyingCharacter = p;
    }
    
    /**
     * removes all references to the unit
     * */
    public void removeUnit() {
    	this.occupyingCharacter.currentTile = null;
    	this.occupyingCharacter = null;
    }
    
    public PlayableUnit getUnit() {
    	return this.occupyingCharacter;
    }
    
    public boolean hasUnit() {
    	return this.occupyingCharacter != null;
    }
    
    public boolean isVisitable() {
    	return this.type != TileType.COVER;
    }
    
    public int getCenterX() {
    	return this.x +this.width/2;
    }
    public int getCenterY() {
    	return this.y +this.width/2;
    }

}
