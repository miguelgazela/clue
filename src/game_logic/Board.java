package game_logic;

import java.util.ArrayList;
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
				if(i==8 && j==4) { // TODO remove this
					System.out.print("[A]");
				} else if(tiles.get(8).get(4).getNeighbours().contains(tiles.get(i).get(j).getCoordinates())) {
					System.out.print("[N]");
				} else {
					tiles.get(i).get(j).printTile();
				}
			}
			System.out.println("\n");
		}
	}
	
	/**
	 * 
	 */
	private void setInvalid() {
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {			
			tiles.get(i).get(0).setValid(false);
			tiles.get(i).get(23).setValid(false);
		}
		for(int i = 0; i < Board.BOARD_WIDTH; i++) {			
			tiles.get(0).get(i).setValid(false);
			tiles.get(24).get(i).setValid(false);
		}
		
		//starting points
		// TODO change to players
		tiles.get(0).get(9).setValid(true);
		tiles.get(0).get(14).setValid(true);
		tiles.get(24).get(7).setValid(true);
		tiles.get(24).get(16).setValid(true);
		tiles.get(7).get(0).setValid(true);
		tiles.get(17).get(0).setValid(true);
		tiles.get(19).get(23).setValid(true);
		tiles.get(6).get(23).setValid(true);
		
		//rooms
		
		//kitchen
		setRoom(1, 6, 1, 5);
		tiles.get(1).get(6).setValid(false);
		tiles.get(6).get(4).setDoor(KITCHEN);
		
		//dining room
		for(int i = 1; i < 5; i++) {
			tiles.get(9).get(i).setValid(false);
		}
		setRoom(10, 15, 1, 7);
		tiles.get(15).get(6).setDoor(DINING);
		tiles.get(12).get(7).setDoor(DINING);
		
		//lounge
		setRoom(19, 24, 1, 6);
		tiles.get(19).get(6).setDoor(LOUNGE);
		
		//hall
		setRoom(18, 24, 9, 14);
		tiles.get(18).get(11).setDoor(HALL);
		tiles.get(18).get(12).setDoor(HALL);
		tiles.get(20).get(14).setDoor(HALL);
		
		//study
		setRoom(21, 23, 17, 22);
		tiles.get(21).get(17).setDoor(STUDY);
		
		//library
		setRoom(14, 18, 18, 22);
		tiles.get(15).get(17).setValid(false);
		tiles.get(16).get(17).setDoor(LIBRARY);
		tiles.get(17).get(17).setValid(false);
		tiles.get(14).get(21).setDoor(LIBRARY);
		
		//billiard room
		setRoom(8, 12, 18, 22);
		tiles.get(9).get(18).setDoor(BILLIARD_ROOM);
		
		//conservatory
		setRoom(1, 5, 18, 22);
		tiles.get(5).get(18).setValid(true);
		tiles.get(4).get(18).setDoor(CONVERVATORY);
		tiles.get(1).get(17).setValid(false);
		
		//ball room
		setRoom(2, 7, 8, 15);	
		tiles.get(1).get(10).setValid(false);
		tiles.get(1).get(11).setValid(false);
		tiles.get(1).get(12).setValid(false);
		tiles.get(1).get(13).setValid(false);
		tiles.get(7).get(9).setDoor(BALLROOM);
		tiles.get(7).get(14).setDoor(BALLROOM);
		tiles.get(5).get(8).setDoor(BALLROOM);
		tiles.get(5).get(15).setDoor(BALLROOM);
		
		//middle
		setRoom(10, 16, 10, 14);		
	}

	/**
	 * 
	 * @param initialX
	 * @param finalX
	 * @param initialY
	 * @param finalY
	 */
	private void setRoom(int initialX, int finalX, int initialY, int finalY) {
		for(int i = initialX; i <= finalX; i++) {
			for(int j = initialY; j <= finalY; j++) {
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
}