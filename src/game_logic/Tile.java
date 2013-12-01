package game_logic;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 */
public class Tile {
	
	//instance variables
	private int x;
	private int y;
	
	private List<Coordinates> validNeighbours;
	private String playerOccupying;
	private boolean isOccupied;
	private boolean valid;
	private boolean isDoor;
	private String room;
	
	/**
	 * 
	 * @param xPos
	 * @param yPos
	 */
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
	
	/**
	 * 
	 * @return
	 */
	public Coordinates getCoordinates() {
		return new Coordinates(x,y);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOccupied() {
		return isOccupied;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPlayerOccupying() {
		return playerOccupying;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRoom() {
		return room;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Coordinates> getNeighbours() {
		return validNeighbours;
	}
	
	/**
	 * 
	 * @param state
	 */
	public void setValid(boolean state) {
		valid = state;
	}
	
	/**
	 * 
	 * @param player
	 */
	public void setOccupied(String player) {
		playerOccupying = player;
		isOccupied = true;
	}
	
	/**
	 * 
	 */
	public void removePlayer() {
		playerOccupying = "";
		isOccupied = false;
	}
	
	/**
	 * 
	 * @param room
	 */
	public void setDoor(String room) {
		valid = true;
		isDoor=true;
		this.room = room;
	}
	
	/**
	 * 
	 * @param coord
	 */
	public void addNeighbour(Coordinates coord) {
		validNeighbours.add(coord);
	}
	
	/**
	 * 
	 */
	public void printTile() {
		String type = " ";
		
		if(valid == false) {
			type = "X";
		} else if(isDoor==true) {
			type = "^";
		}
		
		System.out.print("["+type+"]");
	}
}
