package game_logic;

import java.io.Serializable;

public class CluedoSuggestion implements Serializable {
	
	private static final long serialVersionUID = 9109058855930721465L;
	private final String room;
	private final String suspect;
	private final String weapon;
	private final String player;

	public CluedoSuggestion(String room, String suspect, String weapon, String player) {
		this.room = room;
		this.suspect = suspect;
		this.weapon = weapon;
		this.player = player;
	}

	public String getRoom() {
		return room;
	}

	public String getSuspect() {
		return suspect;
	}

	public String getWeapon() {
		return weapon;
	}

	public String getPlayer() {
		return player;
	}
}
