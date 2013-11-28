package game_logic;

import java.io.Serializable;

public class Cluedo implements Serializable{

	private static final long serialVersionUID = 983890196820258856L;
	
	public static String[] suspects = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};
	public static String[] weapons = {"Candlestick", "Dagger", "Lead pipe", "Revolver", "Rope", "Wrench"};
	public static String[] rooms = {"Kitchen", "Ballroom", "Conservatory", "Dining Room", "Lounge", "Hall", "Study", "Library", "Billiard Room", "Corridor"};
	
	public Cluedo() {
		// default constructor
	}
	
	public Cluedo(int numberOfPlayers) {
		
	}
	
	
}
