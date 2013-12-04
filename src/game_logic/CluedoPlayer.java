package game_logic;

import java.util.ArrayList;

import javax.swing.text.Position;

public class CluedoPlayer {
	
		private String name;
		private Coordinates posOnBoard;
		private ArrayList<CluedoCard> cards;

		public CluedoPlayer(String name, Coordinates initialPos) {
			this.name = name;
			posOnBoard = initialPos;
			cards = new ArrayList<>();
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Coordinates getPosOnBoard() {
			return posOnBoard;
		}

		public void setPosOnBoard(Coordinates posOnBoard) {
			this.posOnBoard = posOnBoard;
		}
		
		public ArrayList<CluedoCard> getCards() {
			return cards;
		}
		
		public void addCard(CluedoCard card) {
			cards.add(card);
		}
		
}
