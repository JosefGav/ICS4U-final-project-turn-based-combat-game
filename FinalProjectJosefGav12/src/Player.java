/*
 * Player class manages users playable units
 * Josef Gav 2024/2025
 * */


public class Player {
	private PlayableUnit[] myTeam = new PlayableUnit[5];
	private boolean myTurn = true;
	
	/**
	 * returns team size
	 * */
	public int teamSize() {
		return this.myTeam.length;
	}
	
	/**
	 * returns the team member at a given index
	 * */
	public PlayableUnit getTeamMember(int index) {
		if (index < this.teamSize()) return this.myTeam[index];
		else return null;
	}
	
	/**
	 * returns width of the team units (same since they are all equal)
	 * */
	public int getWidths() {
		return this.myTeam[0].getSprite().getIconWidth();
	}
	
	/**
	 * returns true of the unit is on this team
	 * */
	public boolean isOnTeam(PlayableUnit pu) {		
		for (PlayableUnit p: myTeam) {
			if (pu == p) return true;
		}
		
		return false;
	}
	
	public Player(PlayableUnit[] team, Map map) {
		this.myTeam = team;
		
		for (int i = 0; i< this.myTeam.length; i++) {
			map.boardMap[0][i*4+2].addUnit(this.myTeam[i]);
		}
	}
	
	/**
	 * resizes sprites of all the characters
	 * */
	public void resizeSprites(int width) {
		for (PlayableUnit pu: this.myTeam) {
			pu.resizeSprite(width);
		}
	}
	
	/**
	 * ends the turn
	 * */
	public void finishTurn() {
		myTurn = false;
	}
	
	/**
	 * returns true if it is the users turn
	 * */
	public boolean myTurn() {
		return this.myTurn;
	}
	
	
	/**
	 * resets turn
	 * */
	public void newTurn() {
		resetPlayerAttackAndMove();
	}
	
	/**
	 * resets turn
	 * */
	private void resetPlayerAttackAndMove() {
		for (PlayableUnit pu : myTeam) {
			pu.resetAttackAndMove();
		}
	}
	
	
	
	
}
