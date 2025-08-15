
/*
 * Allow user to select their characters
 * Josef Gav ICS4U
 * 2024/2025
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;



public class MenuScreen extends JPanel {
	public static final int X_DIM = 25;
    public static final int Y_DIM = 25;
    public static final int X_OFFSET = 30;
    public static final int Y_OFFSET = 30;
    public static final double MIN_SCALE = 0.25;
    public static final int GAP = 10;
    public static final int FONT_SIZE = 16;
    private int originalWidth;
    private int originalHeight;
    
    // list of characters
    public String[] characterList;
    
    private int characterIndex; // index of character being viewed 
    
	private JLabel characterLabel; // displays name of file for character
    private JLabel characterImageLabel; // Label to display character image
    
    // stores selected characters
    private PlayableUnit[] selectedCharacters = new PlayableUnit[5];
    
    private JButton[] slots; // 5 slots for units
	    
	public MenuScreen(Game g) {
		
  		super( true );

        // create a JFrame
        JFrame f = new JFrame( "MYTHICAL LEGION" );
        
        StatsReader reader = new StatsReader();
        
        // list of playable units
        characterList = reader.getCharacterList();
        
        FileEmbedment fe = new FileEmbedment();
        
        // sets cursor to fancy hand
        f.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        setLayout(null); // allows us to move buttons 
       
        // ensures the frame fills the screen
        this.setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize()  );
        f.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        f.setResizable(true);

        this.setFocusable(true);
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Fullscreen
        f.setLayout(new BorderLayout());

        f.setContentPane( this);
        f.pack();
        f.setVisible(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // bottom panel holds players character selections
        JPanel bottomPanel = new JPanel();

        bottomPanel.setSize(getWidth()/3, getHeight()/15);
        bottomPanel.setLocation(getWidth()/2-bottomPanel.getWidth()/2, getHeight()*7/10);
        
        // initializes the 5 slots
        slots = new JButton[this.selectedCharacters.length];
        for (int i = 0; i< slots.length; i++) {
        	slots[i] = new JButton("Player #"+ (i+1));
        	
        	slots[i].setSize(bottomPanel.getWidth()/5,bottomPanel.getHeight()/5);
        	slots[i].setLocation(bottomPanel.getX()+i*slots[i].getWidth(), bottomPanel.getY());
        	
        	bottomPanel.add(slots[i]);
        	
        	final int arg = i;
        	
        	slots[i].addActionListener(e -> showConfirmPopup(arg));
        }
        

        this.add(bottomPanel);
        
        // Create "Left Arrow" button with icon
        JButton leftArrowButton = new JButton(Misc.resizeImageIcon(fe.returnImageIcon("miscellaneous/left_arrow.png"),100,100));
        leftArrowButton.setSize(new Dimension(80, 80)); // Adjust size 
        leftArrowButton.setLocation((int)(getWidth()*.30), (int)(getHeight()*.55));
        
        leftArrowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Move to the previous character
                characterIndex = (characterIndex - 1 + characterList.length) % characterList.length;
                updateCharacterDisplay();
            }
        });
        this.add(leftArrowButton);

        // Create "Right Arrow" button with icon
        JButton rightArrowButton = new JButton(Misc.resizeImageIcon(fe.returnImageIcon("miscellaneous/right_arrow.png"),100,100));
        rightArrowButton.setSize(new Dimension(80, 80)); // Adjust size
        rightArrowButton.setLocation((int)(getWidth()*.70)-rightArrowButton.getWidth(), (int)(getHeight()*.55));
        
        rightArrowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Move to the next character
                characterIndex = (characterIndex + 1 + characterList.length) % characterList.length;
                updateCharacterDisplay();
            }
        });
        this.add(rightArrowButton);
        
        // Create start game button
        JLabel startGame = new JLabel("Press To Begin Game");
        startGame.setFont(new Font("Arial", Font.BOLD, 30));
        startGame.setSize((int)(getWidth()*.20), (int)(getHeight()*.3));
        startGame.setLocation((int)(getWidth()*.80), (int)(getHeight()*.65));
        
        startGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	// starts game if characters have been selected
            	
                boolean charactersHaveBeenSelected = true;
                for (PlayableUnit p: selectedCharacters) {
                	if (p==null) charactersHaveBeenSelected = false;
                }
                
                if (charactersHaveBeenSelected) {
                	g.startGame(selectedCharacters);
                	f.dispose();
                }
                else JOptionPane.showMessageDialog(null, "Please Select 5 Characters"); // warning!!!
              
            }
        });
        this.add(startGame);
        
        // Label for character file name
        characterLabel = new JLabel("Selected Character: " + characterList[characterIndex]);
        characterLabel.setForeground(Color.WHITE);
        characterLabel.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        characterLabel.setSize(300, 50);
        characterLabel.setLocation(getWidth() / 2 - characterLabel.getWidth() / 2, getHeight() / 3);
        this.add(characterLabel);
        
        
        // Label for displaying the character image
        characterImageLabel = new JLabel();
        characterImageLabel.setSize(200, 200); 
        characterImageLabel.setLocation(getWidth() / 2 - characterImageLabel.getWidth() / 2, getHeight() / 2); // Center the image label
        this.add(characterImageLabel);
        
        characterImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PlayableUnit pu = new PlayableUnit(characterList[characterIndex]);
                
                // asks character if they would like to add character
                if (pu.showImagePopupComfirmSelection()) {
                	addCharacter(pu); // add character
                }
            }
        });        
        
        
        setBackground(new Color(129,66,113));
        setBorder(new LineBorder(Color.BLACK, 20));
        
        updateCharacterDisplay();
	}
	
	
	/**
	 * asks user to confirm their unit selection. modifies selectedCharacters[]
	 * */
	public void showConfirmPopup(int i) {
		if (selectedCharacters[i] == null) return; 
		boolean keepCharacter = selectedCharacters[i].showConfirmPopup();
		if (!keepCharacter) {
			selectedCharacters[i] = null;
		}
		updateBottomPanel();
	}
	
	/**
	 * adds character to selected character
	 * */
	public void addCharacter(PlayableUnit character) {
		for (int i = 0; i<selectedCharacters.length;i++) {
			if (selectedCharacters[i]==null) {
				selectedCharacters[i] = character;
				updateBottomPanel();
				break;
			}
		}
	}
	
	/**
	 * update bottom panel to display the selected units
	 * */
	public void updateBottomPanel() {
		for (int i = 0; i<slots.length;i++) {
			if (this.selectedCharacters[i]!=null) {
				slots[i].setIcon(Misc.resizeImageIcon(this.selectedCharacters[i].getSprite(),25,25));
			} else {
				slots[i].setIcon(null);
			}
		}
	}
	
	public static void main(String[] args) {
		MenuScreen scrn = new MenuScreen(new Game());
	}
	
	/**
	 * Displays the character icon
	 * */
	 private void updateCharacterDisplay() {
        // Update the text of the character label
        characterLabel.setText("Selected Character: " + characterList[characterIndex]);
        
        FileEmbedment fe = new FileEmbedment();
        
        String jsonFileName = characterList[characterIndex];
        ImageIcon characterImage = fe.returnImageIcon("characters_icon/"+jsonFileName.substring(0,jsonFileName.indexOf("."))+".png");
              
        characterImageLabel.setIcon(Misc.resizeImageIcon(characterImage, characterImageLabel.getWidth(), characterImageLabel.getHeight()));
        
        // Revalidate and repaint to update the display
        revalidate();
        repaint();

	 }
}
