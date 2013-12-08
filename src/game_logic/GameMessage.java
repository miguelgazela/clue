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

	public Object getObjects() {
		return objects;
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
