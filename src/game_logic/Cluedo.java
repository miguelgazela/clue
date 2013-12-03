package game_logic;

import jade.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.naming.InitialContext;

public class Cluedo implements Serializable{

	private static final long serialVersionUID = 983890196820258856L;
	
	public static String[] suspects = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};
	public static String[] weapons = {"Candlestick", "Dagger", "Lead pipe", "Revolver", "Rope", "Wrench"};
	public static String[] rooms = {"Kitchen", "Ballroom", "Conservatory", "Dining Room", "Lounge", "Hall", "Study", "Library", "Billiard Room", "Corridor"};
	
	private ArrayList<CluedoCard> cards;
	private CluedoCard murderer = null, weapon = null, room = null;
	private final int numberPlayers;
	
	/**
	 * the index of the player that is playing this turn
	 */
	private int turnPlayerIndex = -1;
	private int diceResult = -1;
	private CluedoLogger logger;
	private HashMap<String, ArrayList<CluedoCard>> playerCards;
	private Board board;
	private Random r = new Random(System.currentTimeMillis());
	
	public Cluedo(int numberOfPlayers) throws Exception {
		if(numberOfPlayers < 3 || numberOfPlayers > 6) {
			throw new Exception("Invalid number of players. min:3 max: 6");
		}
		numberPlayers = numberOfPlayers;
		board = new Board();
		logger = CluedoLogger.getInstance();
		initGameCards();
		initGameSolution();
		initPlayersCards();
		findWhoPlaysFirst();
	}
	
	public CluedoCard getMurderWeapon() {
		return weapon;
	}

	public CluedoCard getMurderRoom() {
		return room;
	}

	public CluedoCard getMurderer() {
		return murderer;
	}
	
	public int getNumberPlayers() {
		return numberPlayers;
	}
	
	/**
	 * picks a solution for this instance of the game.
	 */
	private void initGameSolution() {
		while (murderer == null || weapon == null || room == null) {
			CluedoCard card = cards.get(r.nextInt(cards.size()));
			
			if(murderer == null && card.getType() == CluedoCard.SUSPECT) {
				murderer = card;
				cards.remove(card);
			} else if(weapon == null && card.getType() == CluedoCard.WEAPON) {
				weapon = card;
				cards.remove(card);
			} else if(room == null && card.getType() == CluedoCard.ROOM) {
				room = card;
				cards.remove(card);
			}
		}
		logger.log("The murderer is: "+murderer.getName());
		logger.log("The weapon used was: "+weapon.getName());
		logger.log("The room was: "+room.getName());
	}
	
	/**
	 * builds the deck of initial cards.
	 */
	private void initGameCards() {
		cards = new ArrayList<CluedoCard>();
		
		// add suspects
		for(String suspect: suspects) {
			cards.add(new CluedoCard(suspect, CluedoCard.SUSPECT));
		}
		
		// add weapons
		for(String weapon: weapons) {
			cards.add(new CluedoCard(weapon, CluedoCard.WEAPON));
		}
		
		// add rooms
		for(String room: rooms) {
			cards.add(new CluedoCard(room, CluedoCard.ROOM));
		}
	}
	
	/**
	 * distributes the remaining 19 cards for the players playing.
	 */
	private void initPlayersCards() {
		playerCards = new HashMap<>();
		
		// adds a new arraylist to the hashmap with the suspect's name as key
		for(int i = 0; i < numberPlayers; i++) {
			logger.log("Initiating cards for player: "+suspects[i]);
			playerCards.put(suspects[i], new ArrayList<CluedoCard>());
		}
		
		while(cards.size() > 0) {
			
			// give one card to each player while there's still cards in the deck
			for(int i = 0; i < numberPlayers; i++) {
				if(cards.size() > 0) {
					int randomIndex = r.nextInt(cards.size());
					playerCards.get(suspects[i]).add(cards.get(randomIndex));
					cards.remove(randomIndex);
				}
			}
		}
		logger.log("All cards were distributed among the "+numberPlayers+" players");
	}
	
	/**
	 * gets the cards given to a certain player or null if the player isn't playing
	 * @param args
	 */
	public ArrayList<CluedoCard> getPlayerCards(String suspectName) {
		return playerCards.get(suspectName);
	}
	
	/**
	 * rolls an imaginary dice and saves the result in a variable
	 * @return
	 */
	private int rollDice() {
		diceResult = r.nextInt(6) + 1;
		return diceResult;
	}
	
	public String getTurnPlayerName() {
		return suspects[turnPlayerIndex];
	}
	
	/**
	 * find which player makes the first play. The player with the higher result
	 * in the dice roll goes first, then is proceeds clockwise
	 */
	private void findWhoPlaysFirst() {
		boolean hasSingleMax = false;
		int[] results = new int[numberPlayers];
		
		while(!hasSingleMax) {
			hasSingleMax = true;
			
			// roll the dices for every player
			for(int i = 0; i < numberPlayers; i++) {
				results[i] = rollDice();
			}
			
			// check if there is a single max result
			int max = results[0];
			int occurrences = 1;
			turnPlayerIndex = 0;
			
			for(int i = 1; i < numberPlayers; i++) {
				if(results[i] > max) {
					max = results[i];
					occurrences = 1;
					turnPlayerIndex = i;
				} else if(results[i] == max) {
					occurrences++;
				}
			}
			hasSingleMax = (occurrences == 1);
		}
	}
	
	// use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Cluedo cluedo = new Cluedo(3);
//		System.out.println("Next: "+cluedo.getTurnPlayerName());
//	}
}
