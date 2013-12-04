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
	private ArrayList<CluedoPlayer> players;
	private Board board;
	private Random r;
	
	public Cluedo(int numberOfPlayers) throws Exception {
		if(numberOfPlayers < 3 || numberOfPlayers > 6) {
			throw new Exception("Invalid number of players. min:3 max: 6");
		}
		numberPlayers = numberOfPlayers;
		board = new Board();
		logger = CluedoLogger.getInstance();
		r = new Random();
		
		initGameCards();
		initGameSolution();
		initPlayers();
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
	 * returns the board of the game
	 */
	public Board getBoard() {
		return board;
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
	private void initPlayers() {
		players = new ArrayList<CluedoPlayer>();
		
		// adds players to arraylist
		for(int i = 0; i < numberPlayers; i++) {
			logger.log("Initiating player: "+suspects[i]);
			players.add(new CluedoPlayer(suspects[i]));
		}
		
		// give one card to each player while there's still cards in the deck
		while(cards.size() > 0) {
			for(int i = 0; i < numberPlayers; i++) {
				if(cards.size() > 0) {
					int randomIndex = r.nextInt(cards.size());
					players.get(i).addCard(cards.get(randomIndex));
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
		for(CluedoPlayer player : players) {
			if(player.getName().equals(suspectName)) {
				return player.getCards();
			}
		}
		return null;
	}
	
	/**
	 * rolls an imaginary dice and saves the result in a variable
	 * @return
	 */
	private int rollDice() {
		diceResult = r.nextInt(6) + 1;
		return diceResult;
	}
	
	public void updateTurnPlayer() {
		if(turnPlayerIndex == (numberPlayers - 1)) {
			turnPlayerIndex = 0;
		} else {
			turnPlayerIndex++;
		}
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
		
		logger.log("Player that goes first: "+suspects[turnPlayerIndex]);
	}
	
//	//use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Cluedo cluedo = new Cluedo(6);
//		System.out.println("Next: "+cluedo.getTurnPlayerName());
//	}
}
