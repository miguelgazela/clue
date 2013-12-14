package game_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class CluedoNotebook implements Serializable {

	private static final long serialVersionUID = -1318267273831185904L;

	public static final int CLEAR = -1;
	public static final int HAS_CARD = 0;
	public static final int NOT_SOLUTION = 1;
	public static final int POSSIBLE_SOLUTION = 2;

	private HashMap<String, Integer> cards_state;

	private ArrayList<CluedoSuggestion> otherPlayersSuggestions;	
	private HashMap<String, CluedoCard> cardsShownToOtherPlayers;
	private HashMap<String, String> otherPlayersKnownCards;
	private HashMap<String, ArrayList<String>> cardsNotOwnedByPlayer;
	private Random r = new Random(System.currentTimeMillis());

	public CluedoNotebook() {
		cards_state = new HashMap<>();
		otherPlayersSuggestions = new ArrayList<>();
		cardsShownToOtherPlayers = new HashMap<>();
		otherPlayersKnownCards = new HashMap<>();
		cardsNotOwnedByPlayer = new HashMap<>();
		initCardsState();
	}

	public void addPlayerCards(ArrayList<CluedoCard> cards) {
		for(CluedoCard card: cards) {
			cards_state.put(card.getName(), HAS_CARD);
		}
	}

	public void updateCardState(String card) {
		int currentState = cards_state.get(card).intValue();

		if(currentState == HAS_CARD) {
			return;
		} else if(currentState == POSSIBLE_SOLUTION) {
			cards_state.put(card, CLEAR);
		} else if(currentState == CLEAR){
			cards_state.put(card, NOT_SOLUTION);
		} else if(currentState == NOT_SOLUTION) {
			cards_state.put(card, POSSIBLE_SOLUTION);
		}
	}

	public void updateCardState(String card, int state) {
		if (cards_state.containsKey(card))
			cards_state.put(card, state);
	}

	public HashMap<String, Integer> getCardsState() {
		return cards_state;
	}

	public ArrayList<CluedoSuggestion> getOtherPlayerSuggestions() {
		return otherPlayersSuggestions;
	}

	public void addOtherPlayerSuggestion(CluedoSuggestion suggestion) {
		otherPlayersSuggestions.add(suggestion);
	}

	public void updateCheckedCardsWithSuggestions() {

	}

	/**
	 * Saves the card contradicted and the player who made the contradiction,
	 * so this player can later make deductions from other players plays
	 * 
	 * @param player
	 * @param card
	 */
	public void saveOtherPlayerCard(String card, String player) {		
		otherPlayersKnownCards.put(card, player);
	}
	
	public String getPlayerWhoHasCard(String card) {
		return otherPlayersKnownCards.get(card);
	}
	
	public void saveCardNotOwnedByPlayer(String card, String player) {
		ArrayList<String> notOwnedCards = cardsNotOwnedByPlayer.get(player);
		notOwnedCards.add(card);
		cardsNotOwnedByPlayer.put(player,notOwnedCards);
	}
	
	public ArrayList<String> getCardNotOwnedByPlayer(String player) {
		return cardsNotOwnedByPlayer.get(player);
	}

	public boolean hasShownCardToPlayer(String player, CluedoCard card) {
		return cardsShownToOtherPlayers.containsKey(player) &&
				cardsShownToOtherPlayers.containsValue(card);
	}

	public void addCardShownToPlayer(String player, CluedoCard card) {
		cardsShownToOtherPlayers.put(player, card);
	}

	public ArrayList<String> getNotCheckedRooms() {
		ArrayList<String> cardsStrings = new ArrayList<String>();

		for (String card : Cluedo.rooms)
			if (cards_state.containsKey(card) && cards_state.get(card) == CLEAR)
				cardsStrings.add(card);
		return cardsStrings;
	}

	public ArrayList<String> getNotCheckedSuspects() {
		ArrayList<String> cardsStrings = new ArrayList<String>();

		for (String card : Cluedo.suspects)
			if (cards_state.containsKey(card) && cards_state.get(card) == CLEAR)
				cardsStrings.add(card);
		return cardsStrings;
	}

	public ArrayList<String> getNotCheckedWeapons() {
		ArrayList<String> cardsStrings = new ArrayList<String>();

		for (String card : Cluedo.weapons)
			if (cards_state.containsKey(card) && cards_state.get(card) == CLEAR)
				cardsStrings.add(card);
		return cardsStrings;
	}

	private void initCardsState() {
		cards_state.put("Miss Scarlett", new Integer(-1));
		cards_state.put("Colonel Mustard", new Integer(-1));
		cards_state.put("Mrs. White", new Integer(-1));
		cards_state.put("Reverend Green", new Integer(-1));
		cards_state.put("Mrs. Peacock", new Integer(-1));
		cards_state.put("Professor Plum", new Integer(-1));

		cards_state.put("Candlestick", new Integer(-1));
		cards_state.put("Dagger", new Integer(-1));
		cards_state.put("Lead pipe", new Integer(-1));
		cards_state.put("Revolver", new Integer(-1));
		cards_state.put("Rope", new Integer(-1));
		cards_state.put("Wrench", new Integer(-1));

		cards_state.put("Kitchen", new Integer(-1));
		cards_state.put("Ballroom", new Integer(-1));
		cards_state.put("Conservatory", new Integer(-1));
		cards_state.put("Dining Room", new Integer(-1));
		cards_state.put("Lounge", new Integer(-1));
		cards_state.put("Hall", new Integer(-1));
		cards_state.put("Study", new Integer(-1));
		cards_state.put("Library", new Integer(-1));
		cards_state.put("Billiard Room", new Integer(-1));
	}
}
