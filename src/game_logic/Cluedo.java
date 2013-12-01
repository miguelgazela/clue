package game_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Cluedo implements Serializable{

	private static final long serialVersionUID = 983890196820258856L;
	
	public static String[] suspects = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};
	public static String[] weapons = {"Candlestick", "Dagger", "Lead pipe", "Revolver", "Rope", "Wrench"};
	public static String[] rooms = {"Kitchen", "Ballroom", "Conservatory", "Dining Room", "Lounge", "Hall", "Study", "Library", "Billiard Room", "Corridor"};
	
	private ArrayList<String> cards;
	
	private String murderer, weapon, room;
	
	private Random r = new Random(System.currentTimeMillis());
	
	private Cluedo() {
		
	}
	
	public Cluedo(int numberOfPlayers) {
		pickGameSolution();
	}
	
	private void pickGameSolution() {
		murderer = suspects[r.nextInt(suspects.length)];
		weapon = weapons[r.nextInt(weapons.length)];
		room = rooms[r.nextInt(rooms.length)];
	}
	
	
}
