/*
 * Extension of PlayableUnit class 
 * Boss controls his own movement and attacks
 * Josef Gavronskiy FINAL PROJECT
 * ICS4U 2024/2025
 * */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Boss extends PlayableUnit{

	public Boss(String statsFile) {
		super(statsFile);
	}
	
	
	/**
	 * moves towards the nearest enemy and attacks it
	 * */
	public void executeTurn(Map map, Player player) {
		int smallestDist = 1000000000; // huge dist temporary
		int closestPlayerIndex = 0;
		
		for (int i = 0; i < player.teamSize();i++) {
			if (player.getTeamMember(i).getDistance(this.currentTile)< smallestDist) {
				smallestDist = player.getTeamMember(i).getDistance(this.currentTile);
				closestPlayerIndex = i;
			}
		}
		
		PlayableUnit closestPlayer = player.getTeamMember(closestPlayerIndex);
				
		Tile availableAdjacentTile = null;
		
		for (Tile t: closestPlayer.currentTile.adjacent) {
			System.out.println(t.getRow()+","+t.getCol());
			if (!t.hasUnit()&&t.isVisitable()) {
				System.out.println("found");
				availableAdjacentTile = t;
				break;
			}
		}
		
		// moves to nearest available tile
		if (availableAdjacentTile!= null) {
			this.currentTile.removeUnit();
			availableAdjacentTile.addUnit(this);
		}
		
		availableAdjacentTile = null;
		
		for (Tile t: this.currentTile.adjacent) {
			if (t.hasUnit()) {
				availableAdjacentTile = t;
				break;
			}
		}
		
		// attacks nearest player
		if (availableAdjacentTile!= null) this.attack(availableAdjacentTile);
		
		// removes old sprite after 1s
		Timer timer = new Timer(1000, new ActionListener() {
	           
			@Override
			public void actionPerformed(ActionEvent e) {

                map.refreshCharacterIcons();
			}
        });
		
		timer.setRepeats(true);
		timer.start();
		
	}
}
