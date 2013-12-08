package game_logic;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.Position;

public class CluedoNotebook {
	
	public static final int CLEAR = -1;
	public static final int HAS_CARD = 0;
	public static final int NOT_SOLUTION = 1;
	public static final int POSSIBLE_SOLUTION = 2;
	
	private HashMap<String, Integer> cards_state;
	
	public CluedoNotebook() {
		cards_state = new HashMap<>();
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
	
	public HashMap<String, Integer> getCardsState() {
		return cards_state;
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
