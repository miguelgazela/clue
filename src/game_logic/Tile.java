package game_logic;

import java.util.ArrayList;
import java.util.List;

public class Tile {
	private int x;
	private int y;
	
	private List<Coordinates> validNeighbours;
	private String playerOccupying;
	private boolean isOccupied;
	private boolean valid;
	private boolean isDoor;
	private String room;
	
	public Tile(int xPos, int yPos) {
		x = xPos;
		y = yPos;
		valid = true;
		playerOccupying = "";
		isOccupied=false;
		isDoor = false;
		room = "";
		validNeighbours = new ArrayList<Coordinates>();
	}
	
	public Coordinates getCoordinates() {
		return new Coordinates(x,y);
	}
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public String getPlayerOccupying() {
		return playerOccupying;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public String getRoom() {
		return room;
	}
	
	public List<Coordinates> getNeighbours() {
		return validNeighbours;
	}
	public void setValid(boolean state) {
		valid = state;
	}
	public void setOccupied(String player) {
		playerOccupying = player;
		isOccupied = true;
	}
	
	public void removePlayer() {
		playerOccupying = "";
		isOccupied = false;
	}
	
	public void setDoor(String room) {
		valid = true;
		isDoor=true;
		this.room = room;
	}
	
	public void addNeighbour(Coordinates coord) {
		validNeighbours.add(coord);
	}
	
	public void printTile() {
		String type = " ";
		if(valid == false)
			type = "X";
		else if(isDoor==true)
			type = "^";
		
		System.out.print("["+type+"]");
	}
}
