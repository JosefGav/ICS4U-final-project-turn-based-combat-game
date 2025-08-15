
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import java.util.Random;
import java.util.Arrays;

/*
 * map class controls the games logic
 * JOSEF GAVRONSKIY ICS4U FINAL PROJ
 * 2024/2025
 * */

public class Map  extends JPanel 
{
    // 2D array stores the board
    public  Tile[][] boardMap;
    
    // opaque overlay of the board
    public RectOverlay overlay = new RectOverlay();
    	
    // scaling constants
    public static final int X_DIM = 25;
    public static final int Y_DIM = 25;
    public static final int X_OFFSET = 30;
    public static final int Y_OFFSET = 30;
    public static final double MIN_SCALE = 0.25;
    public static final int GAP = 10;
    public static final int FONT_SIZE = 16;
    
    private int originalWidth;
    private int originalHeight;
    public double scale;
    
   
    // Colour to use if a match is not found
    private static final Color DEFAULT_COLOUR = Color.BLACK;
   

    private int columns, rows;
    
    // this line stores the data for the line that will show the projectile path for our Characters
    private Line projectilePathLine;
    
    // original sized icons
    private ImageIcon cover;
    private ImageIcon visionObstruction; // (grass)

    // icons resized to fit within a tile
    private ImageIcon resizedCover;
    private ImageIcon resizedVisionObstruction;
    
    // game state
    public enum GameState {SELECT, ATTACK, MOVE,BOSS_TURN};
    private GameState currentState = GameState.SELECT;
    
    // select, move, attack, next turn
    private JPanel buttonPanel;
    
    // displays character statistics
    private JLabel sidePanel;
    
    // character currently selected by the player
    private PlayableUnit characterSelected;
    
    // player object you contains and manages all of the users playable unit objects
    private Player you;
    
    // boss
    private Boss boss;
    private final String BOSS_FILE = "boss.json";


    /** A constructor to build a 2D board
     */
    public Map (int rows, int cols, PlayableUnit[] selectedCharacters)
    {
        super( true );

        // create a JFrame
        JFrame f = new JFrame( "MYTHICAL LEGION" );
        
        // changes the cursors appearance
        f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        FileEmbedment fe = new FileEmbedment();
        
        // set original sized icons for cover and grass
        this.cover = fe.returnImageIcon("maps/cover_default.png");
        this.visionObstruction = fe.returnImageIcon("maps/vision_obstruction_default.png");
       
        
        // initialize the board array
        this.boardMap = new Tile[rows][cols];
        
        StatsReader reader = new StatsReader();
        
        // template contains (0,1,2) each representing a tile type
        int[][] template = reader.returnMapGrid();

        // populate the board array with tiles
        for (int r = 0; r < rows; r++ ) {
            for (int c = 0; c < cols; c ++) {
                this.boardMap[r][c] = new Tile(r,c, Tile.toType(template[r][c]));
                this.add(this.boardMap[r][c].getOverlayImageLabel());
                this.add(this.boardMap[r][c].getCharacterOverlayLabel());
            }
        }
        
        // each tile has a list of adjacent tiles for pathfinding
        // ensures that they are setup properly
        this.populateAgacency();
        
        // creates the boss and adds it to the board
        boss = new Boss(BOSS_FILE);
        this.boardMap[10][10].addUnit(boss);
        
        // Add a ComponentListener to the JFrame to detect resize
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                overlay.hideOverlay(); // removes the overlay
            }
        });
        
        // initialize projectile path line
        projectilePathLine = new Line(new Coordinate(0,0), new Coordinate(4,4),this.boardMap);

        // to track mouse clicks and movement
        this.addMouseListener(returnSwipeInputAdaptar());
        this.addMouseMotionListener(returnSwipeInputAdaptar());
        
        // initializes your playable unit team manager
        this.you = new Player(selectedCharacters,this);

        // sets rows and columns properties of the instantiated board object
        this.columns = cols;
        this.rows = rows;

        
        originalWidth = 2*X_OFFSET+X_DIM*(cols+15);
        originalHeight = 2*Y_OFFSET+Y_DIM*rows+GAP+FONT_SIZE;

        // sets size
        this.setPreferredSize( new Dimension( originalWidth, originalHeight ) );

        f.setResizable(true);
        this.setFocusable(true);
        

        // board class boilerplate
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane( this );
        f.pack();
        f.setVisible(true);
        
        
        this.setBackground(new Color(129,66,113));
        this.setBorder(new LineBorder(Color.BLACK, 20));
        
        // Panel contains buttons (select,attack,next turn)
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        // make buttons
        JButton attackButton = new JButton("Attack");
        JButton selectButton = new JButton("Select");
        JButton moveButton = new JButton("Move");
        JButton finishTurnButton = new JButton("Finish Turn");
        
        // add action listeners
        attackButton.addActionListener(e -> setGameState(GameState.ATTACK));
        selectButton.addActionListener(e -> setGameState(GameState.SELECT));
        moveButton.addActionListener(e -> setGameState(GameState.MOVE));
        finishTurnButton.addActionListener(e -> setGameState(GameState.BOSS_TURN));

        // add buttons to button panel
        buttonPanel.add(attackButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(finishTurnButton);

        // add button panel to the map
        this.add(buttonPanel);
        
        // side panel jlabel displays character statistics
        this.sidePanel = new JLabel();
        this.add(this.sidePanel);
    }

    


    public MouseInputAdapter returnSwipeInputAdaptar() {
    	return new MouseInputAdapter() {
    		/**
    		 * Method is called when the mouse is pressed
    		 * */
            public void mousePressed(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();

                // We need to be synchronized to the parent class so we can wake
                // up any threads that might be waiting for us
                synchronized (Map.this) {
                	
                	int row = getRow(y);
                    int col = getCol(x);
             
                    
                    // checks if the tile is within the board
                    if (inBounds(row,col)) {
                    	// calls the tile click handling method 
                    	tileClicked(boardMap[row][col]);
                    }
                    
                    repaint();
                } /* synchronized */
            } /* mousePressed */
            
            /**
             * Method is called whenever the mouse is moved
             */
            public void mouseMoved(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();
                                
                	synchronized (Map.this) {
                        // curent row and column of the tile our cursor is hovering over
                        int row = getRow(y);
                        int col = getCol(x);
                        
                        
                        // checks if tile is within the board
                        // manages overlay and projectile path line
                        if (inBounds(row,col)) {
                        	if (currentState == GameState.SELECT) overlay.setBounds(new Coordinate(row,col),0,boardMap); // 1 tile overlay
                        	else if (characterSelected!=null) {
                        		// tile is within boundary of movement radius
                        		if (currentState==GameState.MOVE && 
                        			Math.abs(row- characterSelected.currentTile.getRow()) <= characterSelected.getMoveRadius() && 
                        			Math.abs(col - characterSelected.currentTile.getCol()) <= characterSelected.getMoveRadius()) {
		                    			projectilePathLine.hide();
		                    			overlay.setBounds(new Coordinate(characterSelected.currentTile.getRow(),
		                        				characterSelected.currentTile.getCol()),
		                        				characterSelected.getMoveRadius(),boardMap);
		                    			
		                    	// tile is within boundary of attack radius
                        		} else if (currentState==GameState.ATTACK && 
                            		Math.abs(row- characterSelected.currentTile.getRow()) <= characterSelected.getAttackRadius() && 
                            		Math.abs(col - characterSelected.currentTile.getCol()) <= characterSelected.getAttackRadius() &&
                            		!characterSelected.hasAttacked()
                        				) {
                        			overlay.setBounds(new Coordinate(characterSelected.currentTile.getRow(),
                            				characterSelected.currentTile.getCol()),
                            				characterSelected.getAttackRadius(),boardMap);
                        			projectilePathLine.setStartCoordinate(new Coordinate(characterSelected.currentTile.getRow(),characterSelected.currentTile.getCol()));
                        			projectilePathLine.setEndCoordinate(new Coordinate(row,col));
                        			projectilePathLine.show();
                        			
                        			if (characterSelected.canAttack(boardMap[row][col], Map.this)) projectilePathLine.setColor(new Color(0,255,0,122)); // can attack line is green
                        			else projectilePathLine.setColor(new Color(255,150,0,122));  // cant attack line is red
                        			
                        		} else {
	                        		overlay.hideOverlay();
	                        		projectilePathLine.hide();
	                        		return;
		                        }
                        	}	
                    	} else {
                    		overlay.hideOverlay();
                    		projectilePathLine.hide();
                    		return;
                    	}
                        			
                        
                        	
                 
                        				
                        	
                        	// changes overlay color based on game state
                        	switch (currentState) {
                        		case SELECT: overlay.setColor(); break;
                        		case ATTACK: 
                        			if (!characterSelected.hasAttacked()) overlay.setColor(new Color(255, 0, 0, 102)); 
                        			else JOptionPane.showMessageDialog(null, "Character Has Already Attacked");
                        			break;
                        		case MOVE: 
                        			if (!characterSelected.hasMoved()) overlay.setColor(new Color(0, 255, 0, 102)); 
                        			else JOptionPane.showMessageDialog(null, "Character Has Already Moved");
                        			break;
                        		
                        	}
                            overlay.revealOverlay(); // embed this in reveal tile overlay!
                            
                            // Repaint to show the updated position
                            repaint();
                        }
                        
                     


                        
                	} /* synchronized */
               

    	};}
    
    /**
     * Gets tile row based on y position
     * */
    public int getRow(int y) {
    	return (int) Math.floor((y - Y_OFFSET * scale) / Y_DIM / scale);
    }
    
    /**
     * Gets tile column based on x position
     * */
    public int getCol(int x) {
    	return (int) Math.floor((x - X_OFFSET * scale) / X_DIM / scale);
    }

    /**
     * returns true of the row and column of the tile are within the grid
     * */
    public boolean inBounds(int r, int c) {
    	if (r<this.boardMap.length && c<this.boardMap.length && r>=0 && c>=0 ) return true;
    	return false;
    }
    
    /**
     * checks if any imageicons have to be resized and resizes them
     * */
    public void resizeMap() {
    	// dimension (w=h)
    	int dim = (int)Math.round(X_DIM*scale);
    	
    	// resizes any sprites or image icons if there size differs from what they are supposed to be set too
    	if (resizedCover==null) {
        	resizedCover = Misc.resizeImageIcon(cover, dim, dim);
    	} else if (dim!=resizedCover.getIconWidth()) {
        	resizedCover = Misc.resizeImageIcon(cover, dim, dim);
    	}
    	
    	if (this.you!=null&&dim!=this.you.getWidths()) {
        	this.you.resizeSprites(dim);
    	}
    	
    	if (this.boss!=null&&dim!=this.boss.getSprite().getIconWidth()) {
        	this.boss.resizeSprite(dim);
    	}
    	
    	if (resizedVisionObstruction==null) {
        	resizedVisionObstruction = Misc.resizeImageIcon(visionObstruction, (int)Math.round(X_DIM*scale), (int)Math.round(Y_DIM*scale));
    	} else if (dim!=resizedVisionObstruction.getIconWidth()) {
        	resizedVisionObstruction = Misc.resizeImageIcon(visionObstruction, (int)Math.round(X_DIM*scale), (int)Math.round(Y_DIM*scale));
    	}
    	
    }
    
    /**
     * Removes all character icons from the screen temporarily
     * */
    public void refreshCharacterIcons() {
    	for (Tile[] tiles : this.boardMap) {
    		for (Tile tile: tiles) {
    			tile.getCharacterOverlayLabel().setIcon(null);
    		}
    	}
    }

    private void paintGrid(Graphics g)
    {
    	Color standard = new Color(203, 104, 67);
    	
    	
        for (int r = 0; r < this.boardMap.length; r++)
        {
            for (int c = 0; c < this.boardMap[r].length; c++)
            {
            	g.setColor(standard);
                
                int curX = (int)Math.round((X_OFFSET+X_DIM*c)*scale);
                int curY = (int)Math.round((Y_OFFSET+Y_DIM*r)*scale);
                int nextX = (int)Math.round((X_OFFSET+X_DIM*(c+1))*scale);
                int nextY = (int)Math.round((Y_OFFSET+Y_DIM*(r+1))*scale);
                int deltaX = nextX-curX;
                int deltaY = nextY-curY;

                // displays the tile
                g.fillRoundRect( curX, curY, deltaX, deltaY,5*(int)scale,5*(int)scale);
                
                
                // sets the tiles coordinates
                boardMap[r][c].setCoordinateAndDimensions(curX,curY,deltaX);
                
                // displays cover, vision obstruction and playable units
                if (boardMap[r][c].getType()==Tile.TileType.COVER) {
                    boardMap[r][c].getOverlayImageLabel().setIcon(resizedCover);
                    revalidate();
                    repaint();
                  
                } else if (boardMap[r][c].hasUnit()) {//if (boardMap[r][c].hasUnit())
                	if (this.you.isOnTeam(this.boardMap[r][c].getUnit())) {
                    	g.setColor(new Color(67, 104, 203)); // friendlies have a light background
                    } else {
                    	g.setColor(new Color(10, 20, 40)); // enemies have a dark  background
                    }
                	g.fillRoundRect( curX, curY, deltaX, deltaY,5*(int)scale,5*(int)scale);
                	
                	// ensures that character is visible
                	boardMap[r][c].getCharacterOverlayLabel().setIcon(boardMap[r][c].getUnit().getSprite());
                	boardMap[r][c].getOverlayImageLabel().setIcon(null);
                	revalidate();
                    repaint();
                } else if (boardMap[r][c].getType()==Tile.TileType.VISION_OBSTRUCTION) {
                	boardMap[r][c].getOverlayImageLabel().setIcon(resizedVisionObstruction);
                    revalidate();
                    repaint();
                } 
            }
        }
        
        // sets the stats panels location and icon (if possible)
        this.sidePanel.setLocation(this.getWidth()*13/20, this.getHeight()*5/15);
        if (this.characterSelected!=null) this.sidePanel.setIcon(this.characterSelected.getSpriteDouble());
        
        // sets the button panel (attack,select, move, etc) just below.
        buttonPanel.setLocation(this.sidePanel.getX(), this.sidePanel.getY() +this.sidePanel.getHeight());
        

        // https://docs.oracle.com/javase/tutorial/2d/geometry/strokeandfill.html#:~:text=To%20set%20the%20stroke%20attribute,rendered%20with%20the%20draw%20method.
        //  + casting graphics
        ((Graphics2D) g).setStroke( new BasicStroke( 5*(float)scale) );
        
        g.setColor(Color.DARK_GRAY);
        
        int curX = (int)Math.round(X_OFFSET*scale)-5*(int)scale;
        int curY = (int)Math.round(Y_OFFSET*scale)-5*(int)scale;
        int nextX = (int)Math.round((X_OFFSET+X_DIM*boardMap.length)*scale)+5*(int)scale;
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*boardMap[0].length)*scale)+5*(int)scale;
        g.drawRoundRect(curX, curY, nextX-curX, nextY-curY,15*(int)scale,15*(int)scale);
        
    }
    

 

    private void drawLine(Graphics g)
    {
    	// checks if the projectile path is visible
    	if (!projectilePathLine.isVisible()) return;
    	
    	projectilePathLine.resetCoordinates(this.boardMap); // makes sure that the coordinates (x,y) of line correspond to the (row,col)
    	
    	// draws a dashed line if it is visible
    	Graphics2D g2d = (Graphics2D) g;
    	
    	float[] dashPattern = {10f, 5f};
    	
    	g2d.setStroke( new BasicStroke( projectilePathLine.getThickness(),BasicStroke.CAP_ROUND,  // Line cap style (no round ends)
                BasicStroke.JOIN_MITER,// Join style (sharp corners)
                10f,                   // Miter limit (distance before miter join turns into a bevel)
                dashPattern,           // Dash pattern (defined above)
                0f   )   );
    	g2d.setColor(projectilePathLine.getColor());
    	g2d.drawLine(projectilePathLine.getStartX(),projectilePathLine.getStartY(),projectilePathLine.getEndX(),projectilePathLine.getEndY());
    }

   


    /** The method that draws everything
     */
    public void paintComponent( Graphics g )
    {
    	
        super.paintComponent(g);
        
        // ensures everything is to scale
        this.setScale();
        
        // first white background layor for grid
        g.setColor(Color.WHITE);
        int curX = (int)Math.round(X_OFFSET*scale)-5*(int)scale;
        int curY = (int)Math.round(Y_OFFSET*scale)-5*(int)scale;
        int nextX = (int)Math.round((X_OFFSET+X_DIM*boardMap.length)*scale)+5*(int)scale;
        int nextY = (int)Math.round((Y_OFFSET+Y_DIM*boardMap[0].length)*scale)+5*(int)scale;
        g.fillRoundRect(curX, curY, nextX-curX, nextY-curY,15*(int)scale,15*(int)scale);
        
        
        this.paintGrid(g);
        this.drawLine(g); 
        
        // draws the overlay of the map
        if (overlay.isOverlayVisible()) {
        	 g.setColor(overlay.getColor());  
             g.fillRect(overlay.getX(), overlay.getY(), overlay.getWidth(), overlay.getHeight());
        }
    }
    
    public void setScale()
    {
    	// sets scale for map
        double width = (0.0+this.getSize().width) / this.originalWidth;
        double height = (0.0+this.getSize().height) / this.originalHeight;
        this.scale = Math.max( Math.min(width,height), MIN_SCALE );
        
        //resize icons and sprites
        resizeMap();
    }

    /**
     * Method handles logic for when a tile is clicked
     * */
    public void tileClicked(Tile tile) {
    	switch (this.currentState) {
    		case BOSS_TURN: break;
    		case MOVE: 
    			// checks if a character is selected
    			if (this.characterSelected!=null) {
    				// moves the character if possible
    				if (!this.characterSelected.move(tile, this)) {
    					if (this.characterSelected.hasMoved()) JOptionPane.showMessageDialog(null, "Character Has Already Moved"); // if move was unsuccessful due to the move already being completed
    					else JOptionPane.showMessageDialog(null, "Invalid Tile");
    				}
    			} else {
    				JOptionPane.showMessageDialog(null, "Please Select A Character Before Moving"); // warning message!!!
    			}
    			this.currentState = GameState.SELECT;
    			break;
    		case SELECT:
    			
    			// check to make sure tile has a unit
    			if (tile.hasUnit()) {
    				// make sure the character on the tile is friendly
    				if (this.you.isOnTeam(tile.getUnit())) {
    					// select character
        				this.characterSelected = tile.getUnit();
        				this.sidePanel.setText(this.characterSelected.getInfoPane());
        				this.sidePanel.setIcon(this.characterSelected.getSpriteDouble());
        			} 
        			tile.getUnit().showImagePopup(); // show popup image (regardless of friendly/enemy)
    			}
    		    			
    			break;
    		case ATTACK:
    			// checks if the unit that you are trying to attack is on your team
    			if (this.you.isOnTeam(tile.getUnit())) {
    				JOptionPane.showMessageDialog(null, "Dont Attack Your Own Player :(");
    				return;
    			}
    			
    			// makes sure you have selected a character
    			if(this.characterSelected!=null) {
    				// attacks if possible
    				if (this.characterSelected.canAttack(tile, this)) {
    					this.characterSelected.attack(tile);
    					this.currentState =  GameState.SELECT;
    			} else if (this.characterSelected.hasAttacked()) {
    				JOptionPane.showMessageDialog(null, "Character Has Already Attacked"); // warning msg!!
    			}
    				
    			}
    			if (tile.hasUnit()) tile.getUnit().showImagePopup(); // show popup after damage has been inflicted
    			break;
    	}
    }
    
    /**
     * Method changes currentState of the map
     * */
    private void setGameState(GameState mode) {
    	if (mode == GameState.MOVE) {
    		if (this.characterSelected == null) {
    			JOptionPane.showMessageDialog(null, "Please Select A Character Before Moving");
    			return;
    		} else if (this.characterSelected.hasMoved()) {
    			JOptionPane.showMessageDialog(null, "Character Has Already Moved");
    			return;
    		}
    	} else if (mode == GameState.ATTACK) {
    		if (this.characterSelected == null) {
    			JOptionPane.showMessageDialog(null, "Please Select A Character Before Attacking");
    			return;
    		} else if (this.characterSelected.hasAttacked()) {
    			JOptionPane.showMessageDialog(null, "Character Has Already Attacked");
    			return;
    		}
    	} else if (mode == GameState.BOSS_TURN) {
    		this.currentState = mode;
    		
    		this.you.finishTurn();
    		
    		this.boss.executeTurn(this,this.you);
    		
    		this.currentState = GameState.SELECT;
    		
    		this.you.newTurn();
    		
    		return;
    	}
    	
        this.currentState = mode;
    }

    /**
     * returns the currentState
     * */
    public GameState getGameState() {
        return this.currentState;
    }
    
    /**
     * each tile has a list of adjacent tiles for pathfinding. this method ensures that they are setup properly
     * */
    public void populateAgacency() {
    	for (Tile[] row: this.boardMap) {
    		for (Tile tile : row) {
				if (tile.getCol() +1  < this.boardMap.length && this.boardMap[tile.getY()][tile.getX()+1].isVisitable()) 
					tile.adjacent.add(this.boardMap[tile.getRow()][tile.getCol()+1]);
				if (tile.getCol() -1  >= 0 && this.boardMap[tile.getRow()][tile.getCol()-1].isVisitable()) 
					tile.adjacent.add(this.boardMap[tile.getRow()][tile.getCol()-1]);
				if (tile.getRow() +1 < this.boardMap.length && this.boardMap[tile.getRow()+1][tile.getCol()].isVisitable()) 
					tile.adjacent.add(this.boardMap[tile.getRow()+1][tile.getCol()]);
				if (tile.getRow() -1 >= 0  && this.boardMap[tile.getRow()-1][tile.getCol()].isVisitable()) 
					tile.adjacent.add(this.boardMap[tile.getRow()-1][tile.getCol()]);
    		}
    	}
    }
}
