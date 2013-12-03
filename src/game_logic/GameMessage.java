package game_logic;

import java.io.Serializable;
import java.util.ArrayList;

public class GameMessage implements Serializable {
	private static final long serialVersionUID = -5733727091301695981L;
	
	public static final String READY_PLAY = "READY_PLAY";
	public static final String DISTRIBUTE_CARDS = "DISTRIBUTE_CARDS";
	
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
