package game_logic;

import java.util.ArrayList;
import java.util.List;

public class Tile {
	private int x;
	private int y;
	
	private List<Tile> validNeighbours;
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
		validNeighbours = new ArrayList<Tile>();
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
	
	public List<Tile> getNeighbours() {
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
	
	public void addNeighbour(Tile tile) {
		validNeighbours.add(tile);
	}
	
	public void printTile() {
		String type = " ";
		if(valid == false)
			type = "X";
		else if(isDoor==true)
			type = "^";
		
		System.out.print("["+type+"]");
	}
	
	@Override
	public int hashCode() {
		return (x * 31) ^ y;
	}
	@Override
	public boolean equals(Object o){
		if (o instanceof Tile) {
			Tile other = (Tile) o;
			return (x == other.getCoordinates().getX() && y == other.getCoordinates().getY());
		}
		return false;
	}
}