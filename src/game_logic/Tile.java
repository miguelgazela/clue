package game_logic;

import jade.util.leap.Set;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
public class Tile implements Serializable {
	
	private static final long serialVersionUID = 1010908308279299799L;
	
	//instance variables
	private Coordinates pos;
	
	private List<Tile> validNeighbours;
	private String playerOccupying;
	private boolean isOccupied;
	private boolean valid;
	private boolean isDoor;
	private boolean isSecretPassage;
	private String room;
	
	public Tile(int xPos, int yPos) {
		pos = new Coordinates(xPos, yPos);
		valid = true;
		playerOccupying = "";
		isOccupied=false;
		isDoor = false;
		isSecretPassage = false;
		room = "";
		validNeighbours = new ArrayList<Tile>();
	}
	
	public Coordinates getCoordinates() {
		return pos;
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
	
	public boolean isSecretPassage() {
		return isSecretPassage;
	}
	
	public Tile setSecretPassage(boolean state) {
		isSecretPassage = state;
		return this;
	}
	
	public String getRoom() {
		return room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public boolean isRoom() {
		return !room.equals("");
	}
	
	public List<Tile> getNeighbours() {
		return validNeighbours;
	}
	public Tile setValid(boolean state) {
		valid = state;
		return this;
	}
	public void setOccupied(String player) {
		playerOccupying = player;
		isOccupied = true;
	}
	
	public void removePlayer() {
		playerOccupying = "";
		isOccupied = false;
	}
	
	public Tile setDoor(String room) {
		isDoor=true;
		return this;
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
		return (pos.getX() * 31) ^ pos.getY();
	}
	@Override
	public boolean equals(Object o){
		if (o instanceof Tile) {
			Tile other = (Tile) o;
			return (pos.getX() == other.getCoordinates().getX() && pos.getY() == other.getCoordinates().getY());
		}
		return false;
	}
}
