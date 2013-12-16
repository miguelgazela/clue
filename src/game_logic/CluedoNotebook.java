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
	
	private HashMap<String, CluedoCard> cardsShownToOtherPlayers;
	private HashMap<String, String> otherPlayersKnownCards;
	private HashMap<String, ArrayList<String>> cardsNotOwnedByPlayer;
	
	// Have both the unchecked cards and the suggested cards 
	// duplicated when they also have not been checked yet
	private ArrayList<String> suspectsSuggestedByOthers;
	private ArrayList<String> weaponsSuggestedByOthers;
	
	private Random r = new Random(System.currentTimeMillis());

	public CluedoNotebook() {
		cards_state = new HashMap<>();
		cardsShownToOtherPlayers = new HashMap<>();
		otherPlayersKnownCards = new HashMap<>();
		cardsNotOwnedByPlayer = new HashMap<>();
		suspectsSuggestedByOthers = new ArrayList<>();
		weaponsSuggestedByOthers = new ArrayList<>();
		initCardsState();
	}

	public void addPlayerCards(ArrayList<CluedoCard> cards) {
		for(CluedoCard card: cards) {
			cards_state.put(card.getName(), HAS_CARD);
		}
		
		for(String card : getNotCheckedSuspects())
			suspectsSuggestedByOthers.add(card);
		for(String card : getNotCheckedWeapons())
			weaponsSuggestedByOthers.add(card);
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

		if (Arrays.asList(Cluedo.suspects).contains(card))
			while (suspectsSuggestedByOthers.contains(card))
				suspectsSuggestedByOthers.remove(card);
		else if (Arrays.asList(Cluedo.weapons).contains(card))
			while (weaponsSuggestedByOthers.contains(card))
				weaponsSuggestedByOthers.remove(card);
	}

	public HashMap<String, Integer> getCardsState() {
		return cards_state;
	}
	
	public void addSuspectSuggestedByOtherPlayer(String card) {
		if (suspectsSuggestedByOthers.contains(card))
			suspectsSuggestedByOthers.add(card);
	}
	
	public void addWeaponSuggestedByOtherPlayer(String card) {
		if (weaponsSuggestedByOthers.contains(card))
			weaponsSuggestedByOthers.add(card);
	}
	
	public String getMostProbableSolutionSuspect() {
		return suspectsSuggestedByOthers.get(r.nextInt(suspectsSuggestedByOthers.size()));
	}
	
	public String getMostProbableSolutionWeapon() {
		return weaponsSuggestedByOthers.get(r.nextInt(weaponsSuggestedByOthers.size()));
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
		if(notOwnedCards == null)
			notOwnedCards = new ArrayList<String>();
		
		notOwnedCards.add(card);	
		
		cardsNotOwnedByPlayer.put(player,notOwnedCards);
	}

	public ArrayList<String> getCardNotOwnedByPlayer(String player) {
		return cardsNotOwnedByPlayer.get(player);
	}

	public void updateCardNotOwnedByPlayer(String player, String card) {

		for(String otherPlayer : cardsNotOwnedByPlayer.keySet()){
			if(!otherPlayer.equals(player))		
				saveCardNotOwnedByPlayer(card,otherPlayer);						
		}
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
