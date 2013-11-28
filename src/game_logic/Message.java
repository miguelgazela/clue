package game_logic;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -5733727091301695981L;
	private String type;
	private Object object;
	
	public Message(String type, Object obj) {
		setType(type);
		setObject(obj);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
