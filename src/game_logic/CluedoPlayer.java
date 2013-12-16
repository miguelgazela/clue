package game_logic;

import java.io.Serializable;
import java.util.ArrayList;

public class CluedoPlayer implements Serializable{

	private static final long serialVersionUID = 7184916309544098886L;

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
