package game_logic;

public class CluedoCard {
	
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
