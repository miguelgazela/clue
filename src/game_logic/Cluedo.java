package game_logic;

import jade.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

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
	private int suggestionContradictorIndex = -1;
	private int dicesResult = -1;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private ArrayList<CluedoPlayer> players;
	private Board board;
	private Random r;
	
	public Cluedo(int numberOfPlayers) throws Exception {
		if(numberOfPlayers < 3 || numberOfPlayers > 6) {
			throw new Exception("Invalid number of players. min: 3 max: 6");
		}
		numberPlayers = numberOfPlayers;
		board = new Board();
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
	
	public int getTurnPlayerIndex() {
		return turnPlayerIndex;
	}
	
	public GameState getGameState() {
		return new GameState(numberPlayers, turnPlayerIndex, players, new Board(board));
	}
	
	/**
	 * removes the player that has lost from the game board
	 * @param playerName
	 */
	public void playerHasLost(String playerName) {
		for(CluedoPlayer player: players) {
			if(player.getName().equals(playerName)) {
				Coordinates currentPos = player.getPosOnBoard();
				board.getTileAtPosition(currentPos).removePlayer();
				player.setPosOnBoard(null);
			}
		}
	}
	
	/**
	 * returns the array with the players of this game
	 * @return
	 */
	public ArrayList<CluedoPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * returns the board of the game
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * returns if the 3 cards received are the solution to this game
	 * @param room
	 * @param suspect
	 * @param weapon
	 * @return
	 */
	public boolean isGameSolution(String room, String suspect, String weapon) {
		return (this.room.getName().equals(room) && this.murderer.getName().equals(suspect) && this.weapon.getName().equals(weapon));
	}
	
	/**
	 * checks if the move to the position received is valid for the current turn player
	 * @param x
	 * @param y
	 * @return
	 */
	
	private boolean moveIsValid(Coordinates dest) {
		CluedoPlayer turnPlayer = players.get(turnPlayerIndex);
		return board.moveIsValid(turnPlayer.getPosOnBoard(), dest, dicesResult, turnPlayer.getName());
	}
	
	/**
	 * tries to make a move, if the move is valid makes it and returns the coordinates of the new position,
	 * or else it returns null
	 * @param dest
	 * @return
	 */
	public Coordinates makeMove(Coordinates dest) {
		if(moveIsValid(dest)) {
			CluedoPlayer turnPlayer = players.get(turnPlayerIndex);
			Coordinates move = board.makeMove(turnPlayer.getPosOnBoard(), dest, turnPlayer.getName());
			turnPlayer.setPosOnBoard(move);
			return move;
		}
		return null;
	}
	
	public String getGameSolution() {
		return murderer.getName()+" used a "+weapon.getName()+" in the "+room.getName();
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
			} else if(room == null && card.getType() == CluedoCard.ROOM && !card.getName().equals("Corridor")) {
				room = card;
				cards.remove(card);
			}
		}
		myLogger.log(Logger.INFO, "Cluedo - The murderer is: "+murderer.getName());
		myLogger.log(Logger.INFO, "Cluedo - The weapon used was: "+weapon.getName());
		myLogger.log(Logger.INFO, "Cluedo - The room was: "+room.getName());
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
			myLogger.log(Logger.INFO, "Cluedo - Initiating player: "+suspects[i]);
			players.add(new CluedoPlayer(suspects[i], board.getPlayerStartingPos(suspects[i])));
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
		myLogger.log(Logger.INFO, "Cluedo - All cards were distributed among the "+numberPlayers+" players");
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
	public int rollDices() {
		dicesResult = (r.nextInt(6) + 1) + (r.nextInt(6) + 1);
		return dicesResult;
	}
	
	/**
	 * updates the turn player, clockwise
	 */
	public void updateTurnPlayer() {
		if(turnPlayerIndex == (numberPlayers - 1)) {
			turnPlayerIndex = 0;
		} else {
			turnPlayerIndex++;
		}
		setSuggestionContradictor();
	}
	
	/**
	 * updates the index of the player that must give a contradiction to a suggestion
	 */
	public void updateSuggestionContradictor() {
		if(suggestionContradictorIndex == (numberPlayers - 1)) {
			suggestionContradictorIndex = 0;
		} else {
			suggestionContradictorIndex += 1;
		}
	}
	
	/**
	 * sets the index of the player that must contradict to be the one to the left of the current turn player index
	 */
	private void setSuggestionContradictor() {
		if(turnPlayerIndex == (numberPlayers - 1)) {
			suggestionContradictorIndex = 0;
		} else {
			suggestionContradictorIndex = turnPlayerIndex + 1;
		}
	}
	
	public String getSuggestionContradictorName() {
		return suspects[suggestionContradictorIndex];
	}
	
	/**
	 * returns the name of the player that must play
	 * @return
	 */
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
				results[i] = rollDices();
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
		
		setSuggestionContradictor();
		myLogger.log(Logger.INFO, "Cluedo - Player that goes first: "+suspects[turnPlayerIndex]);
	}
	
	public class GameState implements Serializable {
		private static final long serialVersionUID = -7446685256646116162L;
		
		public int numberPlayers;
		public int turnPlayerIndex;
		public ArrayList<CluedoPlayer> players;
		public Board board;
		
		public GameState(int nP, int tPI, ArrayList<CluedoPlayer> players_, Board board) {
			this.numberPlayers = nP;
			this.turnPlayerIndex = tPI;
			this.board = board;
			
			this.players = new ArrayList<CluedoPlayer>();
			
			for(CluedoPlayer player: players_) {
				this.players.add(new CluedoPlayer(player.getName(), player.getPosOnBoard()));
			}
		}
	}
	
//	//use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Cluedo cluedo = new Cluedo(6);
//		System.out.println("Next: "+cluedo.getTurnPlayerName());
//	}
}
