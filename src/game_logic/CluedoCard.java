package game_logic;

import java.io.Serializable;

public class CluedoCard implements Serializable {
	
	public static final int SUSPECT = 1;
	public static final int WEAPON = 2;
	public static final int ROOM = 3;
	
	private final String name;
	private final int type;
	
	public CluedoCard(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}
}
