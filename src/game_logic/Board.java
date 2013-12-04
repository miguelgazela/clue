package game_logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {
	
	public static final int BOARD_HEIGHT = 25;
	public static final int BOARD_WIDTH = 24;
	
	public static final String KITCHEN = "KITCHEN";
	public static final String DINING = "DINING_ROOM";
	public static final String LOUNGE = "LOUNGE";
	public static final String HALL = "HALL";
	public static final String STUDY = "STUDY";
	public static final String LIBRARY = "LIBRARY";
	public static final String BILLIARD_ROOM = "BILLIARD_ROOM";
	public static final String CONVERVATORY = "CONSERVATORY";
	public static final String BALLROOM = "BALLROOM";
	
	// instance variables
	private List<List<Tile>> tiles;
	private HashMap<String, Coordinates> playersStartingPos;
	
	/**
	 * Board constructor
	 */
	public Board(){
		tiles = new ArrayList<List<Tile>>();
		
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {
			tiles.add(new ArrayList<Tile>()); // adding new row
			
			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				tiles.get(i).add(new Tile(i,j));
			}
		}
		setInvalid();
		setNeighbours();
		initPlayersPositions();
	}

	/**
	 * 
	 * @return the tiles of this board
	 */
	public List<List<Tile>> getTiles() {
		return tiles;
	}
	
	/**
	 * 
	 */
	public void printBoard() {
		
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {			
			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
//				if(i==8 && j==4) { // TODO remove this
//					System.out.print("[A]");
//				} else if(tiles.get(8).get(4).getNeighbours().contains(tiles.get(i).get(j).getCoordinates())) {
//					System.out.print("[N]");
//				} else {
					tiles.get(i).get(j).printTile();
//				}
			}
			System.out.println("");
		}
	}
	
	/**
	 * initiates the players starting positions. Adds them to a hashmap mapped with the name
	 * of the player
	 */
	private void initPlayersPositions() {
		playersStartingPos = new HashMap<>();
		
		playersStartingPos.put("Miss Scarlett", new Coordinates(7, 24));
		playersStartingPos.put("Colonel Mustard", new Coordinates(0, 17));
		playersStartingPos.put("Mrs. White", new Coordinates(9, 0));
		playersStartingPos.put("Reverend Green", new Coordinates(14, 0));
		playersStartingPos.put("Mrs. Peacock", new Coordinates(23, 6));
		playersStartingPos.put("Professor Plum", new Coordinates(23, 19));
	}
	
	/**
	 * returns the starting position of the player or null if the player doesn't exists
	 * @param player
	 * @return
	 */
	public Coordinates getPlayerStartingPos(String player) {
		return playersStartingPos.get(player);
	}
	
	/**
	 * 
	 */
	private void setInvalid() {
		
		//starting points
		// TODO change to players
		tiles.get(0).get(9).setValid(true);
		tiles.get(0).get(14).setValid(true);
		tiles.get(24).get(7).setValid(true);
		tiles.get(17).get(0).setValid(true);
		tiles.get(19).get(23).setValid(true);
		tiles.get(6).get(23).setValid(true);
		
		//rooms
		
		//kitchen
		setRoom(0, 5, 1, 5);
		setRoom(1, 5, 6, 6);
		tiles.get(6).get(4).setDoor(KITCHEN);
		
		//dining room
		setRoom(0, 4, 9, 9);
		setRoom(0, 7, 10, 15);
		tiles.get(15).get(6).setDoor(DINING);
		tiles.get(12).get(7).setDoor(DINING);
		
		//lounge
		setRoom(0, 6, 19, 24);
		tiles.get(19).get(6).setDoor(LOUNGE);
		
		//hall
		setRoom(9, 14, 18, 24);
		tiles.get(18).get(11).setDoor(HALL);
		tiles.get(18).get(12).setDoor(HALL);
		tiles.get(20).get(14).setDoor(HALL);
		
		//study
		setRoom(17, 23, 21, 24);
		tiles.get(21).get(17).setDoor(STUDY);
		
		//library
		setRoom(18, 23, 14, 18);
		setRoom(17, 17, 15, 17);
		tiles.get(16).get(17).setDoor(LIBRARY);
		tiles.get(14).get(20).setDoor(LIBRARY);
		
		//billiard room
		setRoom(18, 23, 8, 12);
		tiles.get(9).get(18).setDoor(BILLIARD_ROOM);
		
		//conservatory
		setRoom(18, 23, 0, 4);
		setRoom(19, 23, 5, 5);
		setRoom(18, 18, 0, 4);
		setRoom(17, 17, 0, 1);
		setRoom(15, 16, 0, 0);
		tiles.get(4).get(18).setDoor(CONVERVATORY);
		
		//ball room
		setRoom(10, 13, 0, 1);
		setRoom(8, 15, 2, 7);
		tiles.get(7).get(9).setDoor(BALLROOM);
		tiles.get(7).get(14).setDoor(BALLROOM);
		tiles.get(5).get(8).setDoor(BALLROOM);
		tiles.get(5).get(15).setDoor(BALLROOM);
		
		//middle
		setRoom(10, 14, 10, 16);
		
		// others
		setRoom(0, 8, 0, 0);
		tiles.get(1).get(6).setValid(false);
		tiles.get(6).get(0).setValid(false);
		tiles.get(8).get(0).setValid(false);
		tiles.get(16).get(0).setValid(false);
		tiles.get(18).get(0).setValid(false);
		tiles.get(24).get(8).setValid(false);
		tiles.get(24).get(15).setValid(false);
		tiles.get(7).get(23).setValid(false);
		tiles.get(13).get(23).setValid(false);
		tiles.get(20).get(23).setValid(false);
	}

	/**
	 * 
	 * @param initialX
	 * @param finalX
	 * @param initialY
	 * @param finalY
	 */
	private void setRoom(int initialX, int finalX, int initialY, int finalY) {
		for(int i = initialY; i <= finalY; i++) {
			for(int j = initialX; j <= finalX; j++) {
				tiles.get(i).get(j).setValid(false);
			}
		}
	}
	
	/**
	 * 
	 */
	private void setNeighbours() {
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			for(int j = 0; j < BOARD_WIDTH; j++) {
				Tile currentTile = tiles.get(i).get(j);

				if( (j+1) < BOARD_WIDTH && tiles.get(i).get(j+1).isValid()) { //East
					currentTile.addNeighbour(new Coordinates(i, j+1));
				}
				if( (i+1) < BOARD_HEIGHT && tiles.get(i+1).get(j).isValid()) { //South
					currentTile.addNeighbour(new Coordinates(i+1, j));
				}
				if( (j-1) >= 0 && tiles.get(i).get(j-1).isValid()) { //West
					currentTile.addNeighbour(new Coordinates(i, j-1));
				}
				if( (i-1) >= 0 && tiles.get(i-1).get(j).isValid()) { //North
					currentTile.addNeighbour(new Coordinates(i-1, j));
				}
			}
		}
	}
	
//	//use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Board board = new Board();
//		board.printBoard();
//	}
}