package game_logic;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Board implements Serializable {
	
	private static final long serialVersionUID = 4371088632882334921L;
	
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
	private HashMap<String, ArrayList<Tile>> doors;
	private HashMap<String, Coordinates> playersStartingPos;

	/**
	 * Board constructor
	 */
	public Board(){
		tiles = new ArrayList<List<Tile>>();
		doors = new HashMap<>();

		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {
			tiles.add(new ArrayList<Tile>()); // adding new row

			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				tiles.get(i).add(new Tile(i,j));
			}
		}
		initBoard();
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
	 * checks if a move is valid
	 */
	public boolean moveIsValid(Coordinates currentPos, Coordinates dest, int dicesResult) {
		
		Tile destTile = tiles.get(dest.getY()).get(dest.getX());
		
		if(destTile.isValid()) {
			return (getDistance(currentPos, dest) <= dicesResult);
		} else {
			if(destTile.isRoom()) {
				ArrayList<Tile> room_doors = doors.get(destTile.getRoom());
				
				for(Tile door: room_doors) {
					if(getDistance(currentPos, door.getCoordinates()) <= dicesResult && !door.isOccupied()) {
						return true;
					}
				}
				return false;
			}
			return false;
		}
	}
	
	/**
	 * moves a player to a different pos
	 * @param currentPos
	 * @param dest
	 */
	public void makeMove(Coordinates currentPos, Coordinates dest, String player) {
		tiles.get(currentPos.getY()).get(currentPos.getX()).removePlayer();
		tiles.get(currentPos.getY()).get(currentPos.getX()).setOccupied(player);
	}

	/**
	 * 
	 */
	public void printBoard() {

		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {			
			for(int j = 0; j < Board.BOARD_WIDTH; j++)
				tiles.get(i).get(j).printTile();

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
		tiles.get(24).get(7).setOccupied("Miss Scarlett");
		
		playersStartingPos.put("Colonel Mustard", new Coordinates(0, 17));
		tiles.get(17).get(0).setOccupied("Colonel Mustard");
		
		playersStartingPos.put("Mrs. White", new Coordinates(9, 0));
		tiles.get(0).get(9).setOccupied("Mrs. White");
		
		playersStartingPos.put("Reverend Green", new Coordinates(14, 0));
		tiles.get(0).get(14).setOccupied("Reverend Green");
		
		playersStartingPos.put("Mrs. Peacock", new Coordinates(23, 6));
		tiles.get(6).get(23).setOccupied("Mrs. Peacock");
		
		playersStartingPos.put("Professor Plum", new Coordinates(23, 19));
		tiles.get(19).get(23).setOccupied("Professor Plum");
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
	private void initBoard() {

		
		
		//starting points
		tiles.get(0).get(9).setValid(true);
		tiles.get(0).get(14).setValid(true);
		tiles.get(24).get(7).setValid(true);
		tiles.get(17).get(0).setValid(true);
		tiles.get(19).get(23).setValid(true);
		tiles.get(6).get(23).setValid(true);

		//rooms

		//kitchen
		setRoom(0, 5, 1, 5, "Kitchen");
		setRoom(1, 5, 6, 6, "Kitchen");
		tiles.get(6).get(4).setDoor(KITCHEN).setRoom("Kitchen");
		ArrayList<Tile> kitchenDoors = new ArrayList<>();
		kitchenDoors.add(tiles.get(6).get(4));
		doors.put("Kitchen", kitchenDoors);
		

		//dining room
		setRoom(0, 4, 9, 9, "Dining Room");
		setRoom(0, 7, 10, 15, "Dining Room");
		tiles.get(15).get(6).setDoor(DINING).setRoom("Dining Room");
		tiles.get(12).get(7).setDoor(DINING).setRoom("Dining Room");
		ArrayList<Tile> diningRoomDoors = new ArrayList<>();
		diningRoomDoors.add(tiles.get(15).get(6));
		diningRoomDoors.add(tiles.get(12).get(7));
		doors.put("Dining Room", diningRoomDoors);

		//lounge
		setRoom(0, 6, 19, 24, "Lounge");
		tiles.get(19).get(6).setDoor(LOUNGE).setRoom("Lounge");
		ArrayList<Tile> loungeDoors = new ArrayList<>();
		loungeDoors.add(tiles.get(19).get(6));
		doors.put("Lounge", loungeDoors);

		//hall
		setRoom(9, 14, 18, 24, "Hall");
		tiles.get(18).get(11).setDoor(HALL).setRoom("Hall");
		tiles.get(18).get(12).setDoor(HALL).setRoom("Hall");
		tiles.get(20).get(14).setDoor(HALL).setRoom("Hall");
		ArrayList<Tile> hallDoors = new ArrayList<>();
		hallDoors.add(tiles.get(18).get(11));
		hallDoors.add(tiles.get(18).get(12));
		hallDoors.add(tiles.get(20).get(14));
		doors.put("Hall", hallDoors);

		//study
		setRoom(17, 23, 21, 24, "Study");
		tiles.get(21).get(17).setDoor(STUDY).setRoom("Study");
		ArrayList<Tile> studyDoors = new ArrayList<>();
		studyDoors.add(tiles.get(21).get(17));
		doors.put("Study", studyDoors);

		//library
		setRoom(18, 23, 14, 18, "Library");
		setRoom(17, 17, 15, 17, "Library");
		tiles.get(16).get(17).setDoor(LIBRARY).setRoom("Library");
		tiles.get(14).get(20).setDoor(LIBRARY).setRoom("Library");
		ArrayList<Tile> libraryDoors = new ArrayList<>();
		libraryDoors.add(tiles.get(16).get(17));
		libraryDoors.add(tiles.get(14).get(20));
		doors.put("Library", libraryDoors);

		//billiard room
		setRoom(18, 23, 8, 12, "Billiard Room");
		tiles.get(9).get(18).setDoor(BILLIARD_ROOM).setRoom("Billiard Room");
		ArrayList<Tile> billiardRoomDoors = new ArrayList<>();
		billiardRoomDoors.add(tiles.get(9).get(18));
		doors.put("Billiard Room", billiardRoomDoors);
		

		//conservatory
		setRoom(18, 23, 0, 4, "Conservatory");
		setRoom(19, 23, 5, 5, "Conservatory");
		setRoom(18, 18, 0, 4, "Conservatory");
		setRoom(17, 17, 0, 1, "Conservatory");
		setRoom(15, 16, 0, 0, "Conservatory");
		tiles.get(4).get(18).setDoor(CONVERVATORY).setRoom("Conservatory");
		ArrayList<Tile> conservatoryDoors = new ArrayList<>();
		conservatoryDoors.add(tiles.get(4).get(18));
		doors.put("Conservatory", conservatoryDoors);

		//ball room
		setRoom(10, 13, 0, 1, "Ballroom");
		setRoom(8, 15, 2, 7, "Ballroom");
		tiles.get(7).get(9).setDoor(BALLROOM).setRoom("Ballroom");
		tiles.get(7).get(14).setDoor(BALLROOM).setRoom("Ballroom");
		tiles.get(5).get(8).setDoor(BALLROOM).setRoom("Ballroom");
		tiles.get(5).get(15).setDoor(BALLROOM).setRoom("Ballroom");
		ArrayList<Tile> ballroomDoors = new ArrayList<>();
		ballroomDoors.add(tiles.get(7).get(9));
		ballroomDoors.add(tiles.get(7).get(14));
		ballroomDoors.add(tiles.get(5).get(8));
		ballroomDoors.add(tiles.get(5).get(15));
		doors.put("Ballroom", ballroomDoors);

		//middle
		setRoom(10, 14, 10, 16, "");

		// others
		setRoom(0, 8, 0, 0, "");
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
	private void setRoom(int initialX, int finalX, int initialY, int finalY, String room) {
		for(int i = initialY; i <= finalY; i++) {
			for(int j = initialX; j <= finalX; j++) {
				tiles.get(i).get(j).setValid(false).setRoom(room);
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
					currentTile.addNeighbour(tiles.get(i).get(j+1));
				}
				if( (i+1) < BOARD_HEIGHT && tiles.get(i+1).get(j).isValid()) { //South
					currentTile.addNeighbour(tiles.get(i+1).get(j));
				}
				if( (j-1) >= 0 && tiles.get(i).get(j-1).isValid()) { //West
					currentTile.addNeighbour(tiles.get(i).get(j-1));
				}
				if( (i-1) >= 0 && tiles.get(i-1).get(j).isValid()) { //North
					currentTile.addNeighbour(tiles.get(i-1).get(j));
				}
			}
		}
	}

	private ArrayDeque<Coordinates> closestPath(Coordinates source, final Coordinates destination) { //TODO move to agents
		PriorityQueue<Tile> open = new PriorityQueue<Tile>(10,new Comparator<Tile>() {
			public int compare(Tile tile1, Tile tile2) {
				Coordinates c1 = tile1.getCoordinates(), c2 = tile2.getCoordinates();
				if(c1.equals(c2))
					return 0;

				return (getDistance(c1,destination) < getDistance(c2, destination)) ? 1 : -1; //use distance to destination to order
			}
		});

		HashMap<Coordinates, Tile> visited = new HashMap<Coordinates, Tile>();
		HashMap<Coordinates,Coordinates> parents = new HashMap<Coordinates,Coordinates>();

		open.add(tiles.get(source.getX()).get(source.getY()));
		boolean firstMove = true;

		while(!open.isEmpty()) {
			Tile current = open.poll();
			if(current.getCoordinates().equals(destination))
			{
				ArrayDeque<Coordinates> path = new ArrayDeque<Coordinates>();//extract parents from map 
				path.push(current.getCoordinates());
				Coordinates parent = parents.get(current.getCoordinates());
				while(parent != null)
				{
					path.push(parent);
					parent = parents.get(parent);
				}

				return path;
			}
			visited.put(current.getCoordinates(), current);
			for(Tile neighboor : current.getNeighbours()) {
				if(!visited.containsKey(neighboor.getCoordinates()) 
						&& !(firstMove && neighboor.isOccupied())) //verificar se na primeira jogada as posicoes estao livres
				{
					open.add(neighboor);
					parents.put(neighboor.getCoordinates(), current.getCoordinates());
				}				
			}
			if(firstMove)
				firstMove = false;
		}		 

		return new ArrayDeque<Coordinates>();
	}

	public double getDistance(Coordinates p1, Coordinates p2) {
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)) + Math.sqrt(Math.pow(p1.getY() - p2.getY(), 2));
	}

	public void printPath(ArrayDeque<Coordinates> path, Coordinates source, Coordinates destination) { //debug TODO remove
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {			
			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				if(i==source.getX() && j==source.getY()) {
					System.out.print("[S]"); //source
				} else if(i==destination.getX() && j==destination.getY()) {
					System.out.print("[D]"); //destination
				} else if(path.contains(tiles.get(i).get(j).getCoordinates())) {
					System.out.print("[N]"); //path
				} else {
					tiles.get(i).get(j).printTile();
				}				
			}
			System.out.println("");
		}
	}
	//	//use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Board board = new Board();
//		board.printBoard();
//
//		System.out.println("Turnos: " + (board.closestPath(new Coordinates(7,0), new Coordinates(19,23)).size()-1));
//		board.printPath(board.closestPath(new Coordinates(7,0), new Coordinates(19,23)), new Coordinates(7, 0), new Coordinates(19,23));
//
//	}
}