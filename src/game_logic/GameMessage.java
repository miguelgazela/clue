package game_logic;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMessage implements Serializable {
	private static final long serialVersionUID = -5733727091301695981L;
	
	public static final String READY_PLAY = "READY_PLAY";
	public static final String DISTRIBUTE_CARDS = "DISTRIBUTE_CARDS";
	public static final String ACK_DISTRIBUTE_CARDS = "ACK_DISTRIBUTE_CARDS";
	public static final String TURN_PLAYER = "TURN_PLAYER";
	public static final String ASK_DICE_ROLL = "ASK_DICE_ROLL";
	public static final String RSLT_DICE_ROLL = "RSLT_DICE_ROLL";
	public static final String MAKE_MOVE = "MAKE_MOVE";
	public static final String VALID_MOVE = "VALID_MOVE";
	public static final String INVALID_MOVE = "INVALID_MOVE";
	public static final String GAME_STATE_UPDATE = "GAME_STATE_UPDATE";
	public static final String END_TURN = "END_TURN";
	public static final String MAKE_SUGGESTION = "MAKE_SUGGESTION";
	public static final String PLAYER_MADE_SUGGESTION = "PLAYER_MADE_SUGGESTION";
	public static final String CONTRADICT_SUGGESTION = "CONTRADICT_SUGGESTION";
	public static final String NO_CONTRADICTION_CARD = "NO_CONTRADICTION_CARD";
	public static final String HAVE_CONTRADICTION_CARD = "HAVE_CONTRADICTION_CARD";
	public static final String CONTRADICT_CARD = "CONTRADICT_CARD";
	public static final String GAME_OVER = "GAME_OVER";
	public static final String RESET = "RESET";
	public static final String ACK_RESET = "ACK_RESET";
	public static final String MAKE_ACCUSATION = "MAKE_ACCUSATION";
	public static final String PLAYER_MADE_ACCUSATION = "PLAYER_MADE_ACCUSATION";
	public static final String WRONG_ACCUSATION = "WRONG_ACCUSATION";
	
	private final String type;
	private ArrayList<Object> objects = null;
	
	public GameMessage(String type) {
		this.type = type;
	}
	
	public GameMessage(String type, Object obj) {
		this.type = type;
		objects = new ArrayList<Object>();
		objects.add(obj);
	}

	public String getType() {
		return type;
	}

	public ArrayList<Object> getObjects() {
		return objects;
	}
	
	public void setObjects(ArrayList<Object> objects) {
		this.objects = objects;
	}
	
	public Object getObject(int index) {
		return objects.get(index);
	}

	public void addObject(Object obj) {
		if(objects == null) {
			objects = new ArrayList<Object>();
		}
		objects.add(obj);
	}
	
	public void clearObjects() {
		objects.clear();
	}
}
