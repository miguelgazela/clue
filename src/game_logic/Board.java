package game_logic;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Random;

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
	private Random r = new Random(System.currentTimeMillis());

	/**
	 * Board constructor
	 */
	public Board(){
		tiles = new ArrayList<List<Tile>>();
		doors = new HashMap<>();

		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {
			tiles.add(new ArrayList<Tile>()); // adding new row

			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				tiles.get(i).add(new Tile(j,i));
			}
		}
		initBoard();
		setNeighbours();
		initPlayersPositions();
	}
	
	public Board(Board original) {
		tiles = new ArrayList<List<Tile>>();
		doors = new HashMap<>();

		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {
			tiles.add(new ArrayList<Tile>()); // adding new row

			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				tiles.get(i).add(new Tile(j,i));
			}
		}
		initBoard();
		setNeighbours();
		
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {
			tiles.add(new ArrayList<Tile>()); // adding new row

			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				tiles.get(i).add(new Tile(j,i));
				
				Tile originalTile = original.getTiles().get(i).get(j);
				if(originalTile.isOccupied()) {
					tiles.get(i).get(j).setOccupied(originalTile.getPlayerOccupying()); 
				}
			}
		}
	}

	/**
	 * 
	 * @return the tiles of this board
	 */
	public List<List<Tile>> getTiles() {
		return tiles;
	}
	
	public Tile getTileAtPosition(Coordinates pos) {
		return tiles.get(pos.getY()).get(pos.getX());
	}
	
	public ArrayList<Tile> getRoomDoors(String room) {
		return doors.get(room);
	}

	private boolean tryToEnterRoom(Tile destTile, Coordinates currentPos, int dicesResult, String player) {
		ArrayList<Tile> room_doors = doors.get(destTile.getRoom());

		for(Tile door: room_doors) {
			int distanceToDoor = (int) getDistance(currentPos, door.getCoordinates());

			if((distanceToDoor + 1) <= dicesResult && !door.isOccupied()) { // the + 1 is because the door is still outside of the room
				return true;
			} else if((distanceToDoor + 1) <= dicesResult && door.isOccupied() 
					&& (door.getPlayerOccupying().equals(player))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if a move is valid
	 */
	public boolean moveIsValid(Coordinates currentPos, Coordinates dest, int dicesResult, String player) {
		
		Tile destTile = tiles.get(dest.getY()).get(dest.getX());
		Tile currentTile = tiles.get(currentPos.getY()).get(currentPos.getX());
		
		if(currentTile.isRoom()) {
			if(destTile.isValid() && !destTile.isOccupied()) {
				
				ArrayList<Tile> room_doors = doors.get(currentTile.getRoom());
				
				ListIterator<Tile> it = room_doors.listIterator();
				
				while(true) {
					if(it.hasNext()) {
						Tile door = it.next();
						if(door.isOccupied()) {
							it.remove();
						}
					} else {
						break;
					}
				}

				ArrayList<Tile> reachableTiles = new ArrayList<Tile>();
				buildReachableTiles(room_doors, reachableTiles, dicesResult-1);
				return reachableTiles.contains(destTile);

			} else {
				if(destTile.isRoom()) {
					
					if(destTile.getRoom().equals(currentTile.getRoom())) { // if trying to go to the same room
						return false;
					} else {
						// for each door that this room has, it needs to check if it can get to any door of the other room
						ArrayList<Tile> room_doors = doors.get(currentTile.getRoom());
						for(int i = 0; i < room_doors.size(); i++) {
							if(tryToEnterRoom(destTile, room_doors.get(i).getCoordinates(), dicesResult - 1, player)) {
								return true;
							}
						}
						return false;
					}
					
				} 
				return false; // trying to go to the same room
			}
		}else {
			if(destTile.isValid() && !destTile.isOccupied()) {
				
				// builds an array with all the reachable tiles and returns true if destTile is present in it
				ArrayList<Tile> reachableTiles = new ArrayList<Tile>();
				buildReachableTiles(currentTile.getNeighbours(), reachableTiles, dicesResult-1);
				return reachableTiles.contains(destTile);
				
			} else {
				if(destTile.isRoom()) {
					return tryToEnterRoom(destTile, currentPos, dicesResult, player);
				} else {
					return false;
				}
			}
		}
	}
	
	public void buildReachableTiles(List<Tile> neighbours, ArrayList<Tile> result, int depth) {
		for(Tile neighbour: neighbours) {
			if(depth == 0) {
				if(neighbour.isValid() && !neighbour.isOccupied()) {
					result.add(neighbour);
				}
			} else {
				buildReachableTiles(neighbour.getNeighbours(), result, depth - 1);
			}
		}
	}
	
	/**
	 * moves a player to a different pos
	 * @param currentPos
	 * @param dest
	 */
	public Coordinates makeMove(Coordinates currentPos, Coordinates dest, String player) {
		
		tiles.get(currentPos.getY()).get(currentPos.getX()).removePlayer();
		
		// needs to see if the movement is to a room, needs to place the player in an empty place inside the room
		Tile destTile = tiles.get(dest.getY()).get(dest.getX());
		
		if(destTile.isRoom()) {			
			ArrayList<Tile> roomTiles = new ArrayList<>();

			// adds all tiles from this room that are not occupied and are not secret passages
			for(int i = 0; i < tiles.size(); i++) {
				for(int j = 0; j < tiles.get(i).size(); j++) {
					
					Tile currentTile = tiles.get(i).get(j);

					if(currentTile.getRoom().equals(destTile.getRoom())) {
						roomTiles.add(currentTile);
					}
				}
			}

			// picks a free pos from them
			while(true) {
				destTile = roomTiles.get(r.nextInt(roomTiles.size()));
				
				if(!destTile.isOccupied() && !destTile.isSecretPassage()) {
					destTile.setOccupied(player);
					return destTile.getCoordinates();
				}
			}
		} else {
			destTile.setOccupied(player);
			return dest;
		}
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
		tiles.get(7).get(4).setDoor(KITCHEN);
		ArrayList<Tile> kitchenDoors = new ArrayList<>();
		kitchenDoors.add(tiles.get(7).get(4));
		doors.put("Kitchen", kitchenDoors);
		tiles.get(1).get(5).setSecretPassage(true);
		

		//dining room
		setRoom(0, 4, 9, 9, "Dining Room");
		setRoom(0, 7, 10, 15, "Dining Room");
		tiles.get(16).get(6).setDoor(DINING);
		tiles.get(12).get(8).setDoor(DINING);
		ArrayList<Tile> diningRoomDoors = new ArrayList<>();
		diningRoomDoors.add(tiles.get(16).get(6));
		diningRoomDoors.add(tiles.get(12).get(8));
		doors.put("Dining Room", diningRoomDoors);

		//lounge
		setRoom(0, 6, 19, 24, "Lounge");
		tiles.get(18).get(6).setDoor(LOUNGE);
		ArrayList<Tile> loungeDoors = new ArrayList<>();
		loungeDoors.add(tiles.get(18).get(6));
		doors.put("Lounge", loungeDoors);
		tiles.get(19).get(0).setSecretPassage(true);

		//hall
		setRoom(9, 14, 18, 24, "Hall");
		tiles.get(17).get(11).setDoor(HALL);
		tiles.get(17).get(12).setDoor(HALL);
		tiles.get(20).get(15).setDoor(HALL);
		ArrayList<Tile> hallDoors = new ArrayList<>();
		hallDoors.add(tiles.get(17).get(11));
		hallDoors.add(tiles.get(17).get(12));
		hallDoors.add(tiles.get(20).get(15));
		doors.put("Hall", hallDoors);

		//study
		setRoom(17, 23, 21, 24, "Study");
		tiles.get(20).get(17).setDoor(STUDY);
		ArrayList<Tile> studyDoors = new ArrayList<>();
		studyDoors.add(tiles.get(20).get(17));
		doors.put("Study", studyDoors);
		tiles.get(21).get(23).setSecretPassage(true);

		//library
		setRoom(18, 23, 14, 18, "Library");
		setRoom(17, 17, 15, 17, "Library");
		tiles.get(16).get(16).setDoor(LIBRARY);
		tiles.get(13).get(20).setDoor(LIBRARY);
		ArrayList<Tile> libraryDoors = new ArrayList<>();
		libraryDoors.add(tiles.get(16).get(16));
		libraryDoors.add(tiles.get(13).get(20));
		doors.put("Library", libraryDoors);

		//billiard room
		setRoom(18, 23, 8, 12, "Billiard Room");
		tiles.get(9).get(17).setDoor(BILLIARD_ROOM);
		tiles.get(13).get(22).setDoor(BILLIARD_ROOM);
		ArrayList<Tile> billiardRoomDoors = new ArrayList<>();
		billiardRoomDoors.add(tiles.get(9).get(17));
		billiardRoomDoors.add(tiles.get(13).get(22));
		doors.put("Billiard Room", billiardRoomDoors);
		
		//conservatory
		setRoom(18, 23, 0, 4, "Conservatory");
		setRoom(19, 23, 5, 5, "Conservatory");
		setRoom(18, 18, 0, 4, "Conservatory");
		setRoom(17, 17, 0, 1, "Conservatory");
		setRoom(15, 16, 0, 0, "Conservatory");
		tiles.get(5).get(18).setDoor(CONVERVATORY);
		ArrayList<Tile> conservatoryDoors = new ArrayList<>();
		conservatoryDoors.add(tiles.get(5).get(18));
		doors.put("Conservatory", conservatoryDoors);
		tiles.get(5).get(23).setSecretPassage(true);

		//ball room
		setRoom(10, 13, 0, 1, "Ballroom");
		setRoom(8, 15, 2, 7, "Ballroom");
		tiles.get(8).get(9).setDoor(BALLROOM);
		tiles.get(8).get(14).setDoor(BALLROOM);
		tiles.get(5).get(7).setDoor(BALLROOM);
		tiles.get(5).get(16).setDoor(BALLROOM);
		ArrayList<Tile> ballroomDoors = new ArrayList<>();
		ballroomDoors.add(tiles.get(8).get(9));
		ballroomDoors.add(tiles.get(8).get(14));
		ballroomDoors.add(tiles.get(5).get(7));
		ballroomDoors.add(tiles.get(5).get(16));
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

	public ArrayList<Tile> djs(Coordinates source, Coordinates destination)
	{
		if(source.equals(destination)) {
			return new ArrayList<>(); // returns an empty path
		}
		PriorityQueue<Tile> queue = new PriorityQueue<>(10, new Comparator<Tile>() {
			public int compare(Tile t1, Tile t2) {

				if(t1.distance == t2.distance) {
					return 0;
				}
				return (t1.distance < t2.distance) ? -1 : 1;
			}
		});
		
		for(int i = 0; i < BOARD_HEIGHT; i++) {
			for(int j = 0; j < BOARD_WIDTH; j++) {
				Tile tile = tiles.get(i).get(j);
				tile.distance = 9999;
				tile.visited = false;
				tile.previous = null;
			}
		}

		Tile currentTile = tiles.get(source.getY()).get(source.getX());
		currentTile.distance = 0;
		queue.add(currentTile);

		while(!queue.isEmpty()) {
			Tile current = queue.poll();
			current.visited = true;

			ArrayList<Tile> neighbours = (ArrayList<Tile>) current.getNeighbours();
			for(Tile neighbour: neighbours) {
				int dist = current.distance + 1; // accumulate shortest dist from source
				if(dist < neighbour.distance) {
					neighbour.distance = dist; // keep the shortest dist from src to neighbour
					neighbour.previous = current;
					
//					if(!neighbour.visited && !neighbour.isOccupied()) {
					if(!neighbour.visited) { 
						queue.add(neighbour); // add unvisited neighbour into queue to be processed
					}
				}				
			}			
		}

		// build the actual arraylist of path
		ArrayList<Tile> minimumPath = new ArrayList<>();
		
		currentTile = tiles.get(destination.getY()).get(destination.getX());
		
		while(!currentTile.getCoordinates().equals(source)) {
			minimumPath.add(currentTile);
			currentTile = currentTile.previous;
		}
		minimumPath.add(currentTile); // adding the source as well
		
		Collections.reverse(minimumPath);
		return minimumPath;
	}

	private ArrayDeque<Coordinates> closestPath(Coordinates source, final Coordinates destination) { //TODO move to agents
		PriorityQueue<Tile> open = new PriorityQueue<Tile>(10,new Comparator<Tile>() {
			public int compare(Tile tile1, Tile tile2) {
				Coordinates c1 = tile1.getCoordinates(), c2 = tile2.getCoordinates();
				if(c1.equals(c2))
					return 0;

				return (getDistance(c1,destination) < getDistance(c2, destination)) ? -1 : 1; //use distance to destination to order
			}
		});

		HashMap<Coordinates, Tile> visited = new HashMap<Coordinates, Tile>();
		HashMap<Coordinates,Coordinates> parents = new HashMap<Coordinates,Coordinates>();

		open.add(tiles.get(source.getY()).get(source.getX()));
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

	public static double getDistance(Coordinates p1, Coordinates p2) {
		return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2)) + Math.sqrt(Math.pow(p1.getY() - p2.getY(), 2));
	}

	public void printPath(ArrayList<Tile> path, Coordinates source, Coordinates destination) { //debug TODO remove
		for(int i = 0; i < Board.BOARD_HEIGHT; i++) {			
			for(int j = 0; j < Board.BOARD_WIDTH; j++) {
				if(i==source.getY() && j==source.getX()) {
					System.out.print("[S]"); //source
				} else if(i==destination.getY() && j==destination.getX()) {
					System.out.print("[D]"); //destination
				} else if(tiles.get(i).get(j).isOccupied()) {
					System.out.print("[O]");
				} else if(path.contains(tiles.get(i).get(j))) {
					System.out.print("[N]"); //path
				} else {
					tiles.get(i).get(j).printTile();
				}
			}
			System.out.println("");
		}
	}
	
	public static void printPath(ArrayList<Tile> path) {
		for(Tile tile: path) {
			System.out.println(tile.getCoordinates().toString());
		}
	}
	
	//	//use this to test specific functions without having to run the entire game TODO remove in the end
//	public static void main(String[] args) throws Exception {
//		Board board = new Board();
//		board.printBoard();
//		board.getTileAtPosition(new Coordinates(8,8)).setOccupied("aa");
//		ArrayList<Tile> path = board.djs(new Coordinates(0, 7), new Coordinates(17, 20));
//		for(Coordinates c : path)
//		System.out.println(c.getX() + " " + c.getY());
//		System.out.println("Turnos: " + (path.size()-1));
//		board.printPath(path, new Coordinates(0,7), new Coordinates(17,20));
//		
//		
//	}
}