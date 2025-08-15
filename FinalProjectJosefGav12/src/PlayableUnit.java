import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.json.JSONObject;

/*
 * PlayableUnit class blueprint for game characters
 * Josef Gavronskiy ICS4U
 * 2024/2025
 * */

public class PlayableUnit {
	private String name;
	private int attackRadius;
	private int moveRadius;
	private int damage;
	
	private int fullHealth;
	private int currentHealth;
	
	public Tile currentTile; // tile that this PlayableUnit occupies.
	
	private ImageIcon sprite; // small sprite
	private ImageIcon spriteDouble; // medium sprite
	private ImageIcon spriteFullSize; // large sprite
	
	private int width;
	
	private JSONObject stats; // player stats
	
	// I was planning to implement archetypes and elements but because i didnt have time these are purely cosmetic
	public enum Archetypes {THROWER, MELEE, SHOOTER}
	private Archetypes archetype;
	public enum Elements {FIRE, WATER, ICE, EARTH};
	private Elements element;
	
	// stores if this object has moved or attacked in this turn
	private boolean hasMoved;
	private boolean hasAttacked;
	
	/**
	 * moves to the next turn
	 * */
	public void nextTurn() {
		this.hasMoved = false;
		this.hasAttacked = false;
	}
	
	/**
	 * returns if this playable unit has moved this turn
	 * */
	public boolean hasMoved() {
		return this.hasMoved;
	}
	
	/**
	 * returns if this playable unit has attacked this turn
	 * */
	public boolean hasAttacked() {
		return this.hasAttacked;
	}

	/**
	 * returns if this tile is within the movement radius
	 * */
	public boolean inBoundsToMove(Tile tile) {
		if (Math.abs(tile.getRow()-this.currentTile.getRow())<=this.moveRadius &&
			Math.abs(tile.getCol()-this.currentTile.getCol())<=this.moveRadius)
			return true;
		else return false;
	}
	
	/**
	 * returns the distance (in pixels) to a tile
	 * */
	public int getDistance(Tile tile) {
		int dx = this.currentTile.getCenterX() -tile.getCenterX();
		int dy =  this.currentTile.getCenterY() -tile.getCenterY();
		
		return (int)Math.sqrt(dx*dx+dy*dy);
	}
	
	
	/**
	 * moves to a tile if possible. returns boolean representing if the move was successful.  
	 * */
	public boolean move(Tile tileToMoveToo, Map map) {
		// cancels move if move is out of bounds, object has already moved, the destination tile = this tile etc
		if (!this.inBoundsToMove(tileToMoveToo)||this.hasMoved || tileToMoveToo == this.currentTile || tileToMoveToo.hasUnit() || !tileToMoveToo.isVisitable()) return false;

		
		Tile tileStart = this.currentTile;
		
		// stores visited tiles
		ArrayList<Tile> visited = new ArrayList<Tile>();
		
		// stores the shortest path
		ArrayList<PathNode> shortestPath = new ArrayList<PathNode>();
		
		// queue for nodes that are up next to be visited
		ArrayList<PathNode>  queue = new ArrayList<PathNode>();
		
		// starts the queue
		queue.add(new PathNode(this.currentTile, 0, null));
		
		// continue while the queue has nodes to be explored
		while (queue.size() > 0) {
			PathNode node = queue.get(0);
			Tile tile = node.tile;
			int dist = node.distance;
			
			queue.remove(0); // removes bottom node from queue
			
			// destination was reached
			if (tile == tileToMoveToo) {
				// add nodes leading up to the destination to the shortest path array
				while (node.tile!=tileStart) {
					shortestPath.add(0, node);
					node = node.previous;
				}
				shortestPath.add(0, node);
				
				// displays shortest path
				for (PathNode pn: shortestPath) {
					// call move directly too
					moveDirectlyTo(pn.tile,map);
				}
				
				// re-establishes the position of unit
				currentTile.removeUnit();
				tileToMoveToo.addUnit(this);
				this.currentTile = tileToMoveToo;
				this.hasMoved = true;
				
				// removes shortest path visual indicator after 1.5s
				Timer timer = new Timer(1500, new ActionListener() {
		           
					@Override
					public void actionPerformed(ActionEvent e) {

		                map.refreshCharacterIcons();
					}
		        });
				
				timer.setRepeats(true);
				timer.start();
				

				return true;				
			}
			
			// adds tiles to queue
			for (Tile adj : tile.adjacent) {
				if (!visited.contains(adj)&& adj.isVisitable()) {
					visited.add(adj);
					queue.add(new PathNode(adj,dist+1,node));
				}
			}
			
		}
		return false; // no shortest path found
	}
	
	/**
	 * Temporarily displays itself at a tile on the map
	 * */
	public void moveDirectlyTo(Tile tileToMoveToo, Map map) {
		tileToMoveToo.getCharacterOverlayLabel().setIcon(this.sprite);
		map.revalidate();
		map.repaint();
	}

	/**
	 * returns if an attack on a tile is possible
	 * */
	public boolean canAttack(Tile enemy, Map map) {
		if (this.hasAttacked) return false;

		int dx = enemy.getCenterX()-this.currentTile.getCenterX();
		int dy = enemy.getCenterY()-this.currentTile.getCenterY();
		
		
		
		double y = this.currentTile.getCenterY();
		double x = this.currentTile.getCenterX();
		
		int row = map.getRow((int)y);
		int col = map.getCol((int)x);
		
		int width = this.currentTile.getWidth();
		double dc = width/5;
		
		// draws an imaginary line and tests if it crosses any cover tiles
		if (dx ==0) {
			int tileChange = dy/width;
			
			if (tileChange > 0) {
				for (int i = 0; i < tileChange; i++) {
					if (map.boardMap[row+i][col].getType()==Tile.TileType.COVER) return false;
				}
			} else if (tileChange < 0) {
				for (int i = 0; i > tileChange; i--) {
					if (map.boardMap[row+i][col].getType()==Tile.TileType.COVER) return false;
				}
			}
		}
		
		double slope = 1.0*dy/dx;
		
		if (dx>0) {
			for (; map.boardMap[row][col]!=enemy; ) {
				double dr = dc * slope;
				
				y += dr;
				x += dc;
				
				row = map.getRow((int)y);
				col = map.getCol((int)x);
				
								
				if (map.boardMap[row][col].getType()==Tile.TileType.COVER) return false;
			}
		} else if (dx<0) {
			for (; map.boardMap[row][col]!=enemy; ) {
				double dr = -dc * slope;
				
				y += dr;
				x += -dc;
				
				row = map.getRow((int)y);
				col = map.getCol((int)x);
				
				System.out.println("("+row+","+col+")");
								
				if (map.boardMap[row][col].getType()==Tile.TileType.COVER) return false;
			}
		} 
			
		return true;
	}
	
	/**
	 * sets has attacked and moved to false
	 * */
	public void resetAttackAndMove() {
		this.hasAttacked = false;
		this.hasMoved = false;
	}
	
	
	/**
	 * removes health points from a character
	 * */
	public void removeHealth(int hp) {
		this.currentHealth -= hp;
		if (this.currentHealth<=0) this.selfDestruct();
	}
	
	
	/**
	 * attacks a character
	 * */
	public void attack(Tile tile) {
		tile.getUnit().removeHealth(this.damage);
		this.hasAttacked = true;
	}
	
	/**
	 * Deletes itself and removes all its references to any tiles
	 * */
	public void selfDestruct() {
		this.currentTile.removeUnit();
		this.currentTile = null;
	}
	

	/**
	 * constructs object from stats_file.json
	 * */
	public PlayableUnit(String statsFile) {
		StatsReader reader = new StatsReader();
		
		stats = reader.readJSON(statsFile);
		
		name = stats.getString("name");
		
		
		moveRadius = stats.getInt("moveRadius");
		attackRadius = stats.getInt("attackRadius");
		damage = stats.getInt("damage");
		
		fullHealth = stats.getInt("health");
		currentHealth = fullHealth;
		
		archetype = Archetypes.valueOf(stats.getString("archetype"));
		
		element = Elements.valueOf(stats.getString("element"));
		
		FileEmbedment fe = new FileEmbedment();
		
		sprite = fe.returnImageIcon("characters_icon/"+this.name.replace(' ', '_')+".png");
		spriteFullSize = sprite;
		spriteDouble = sprite;
	
		/*
		 * Example json format:
		 * {
  "name": "winter soldier",
  "attackRadius": 5,
  "moveRadius":4,
  "damage": 15,
  "health": 100,
  "archetype": "SHOOTER",
  "element": "ICE"
}
		 * */
	}
	
	
	/**
	 * returns full sized image icon
	 * */
	public ImageIcon getSpriteFullSize() {
		return this.spriteFullSize;
	}
	
	public int getHealth(){ 
		return currentHealth;
	}
	
	/**
	 * returns health percent as an integer / 100
	 * */
	public int getHealthPercent() {
		return (int)(100.0 * currentHealth/ fullHealth);
	}
	
	public int getAttackRadius() {
		return attackRadius;
	}
	
	public int getMoveRadius() {
		return moveRadius;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public Archetypes getArchetype(){
		return archetype;
	}
	
	public Elements getElement(){
		return element;
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * returns medium sized image icon
	 * */
	public ImageIcon getSpriteDouble() {
		return this.spriteDouble;
	}
	
	
	/**
	 * adjusts the sprites size based on tile width
	 * */
	public void resizeSprite(int width) {
    	this.sprite = Misc.resizeImageIcon(this.spriteFullSize, width,width);// I know ill have to resize it properly to prevent lag
    	this.spriteDouble = Misc.resizeImageIcon(this.spriteFullSize, width*5,width*5);
	}
	
	/**
	 * displays statistics message using html and joptionpane
	 * */
    public void showImagePopup() {
        String statsMessage = this.getInfoPane(); // gets html
        // Display the image and stats in a popup dialog
        JOptionPane.showMessageDialog(null, statsMessage, name + " Stats", JOptionPane.INFORMATION_MESSAGE, this.getSprite());
    }
    
    /**
     * opens dialogue. returns true if user would like to keep this object as part of their team.
     * */
    public boolean showConfirmPopup() {
        String statsMessage = this.getInfoPane(); // gets html

        
        int result = JOptionPane.showConfirmDialog(
                null, 
                "WOULD YOU LIKE TO KEEP "+name+"?\n" + statsMessage, 
                "", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                this.getSprite()
            );

        return result == JOptionPane.YES_OPTION || result == JOptionPane.CLOSED_OPTION;
    }
    
    /**
     * opens dialogue. returns true if user would like to select this object as part of their team.
     * */
    public boolean showImagePopupComfirmSelection () {
        String statsMessage = this.getInfoPane(); // gets html

        
        int result = JOptionPane.showConfirmDialog(
                null, 
                "WOULD YOU LIKE TO SELECT "+name+"?\n" + statsMessage, 
                "Character Selection", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                this.getSprite()
            );

        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * returns a string with html code that displays information about this unit when loaded
     * */
    public String getInfoPane() {
    	StatsReader reader = new StatsReader();
    	return String.format(
    			reader.getGenericFileContents("miscellaneous/infotest.html"), // html file
        	    name, currentHealth, fullHealth, getHealthPercent(),
        	    getHealthPercent(), damage, attackRadius, moveRadius, archetype.toString(), element.toString()
        	);
    }
    
    /**
     * returns tile sized sprite
     * */
    public ImageIcon getSprite() {
    	return this.sprite;
    }
}
