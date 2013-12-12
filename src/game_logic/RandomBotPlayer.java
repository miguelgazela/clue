package game_logic;

import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.Random;

public class RandomBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 3588603829094518352L;
	private String targetRoom = null;
	private Coordinates targetCoord = null;
	private Tile doorToExit = null;
	private Random r = new Random(System.currentTimeMillis());
	
	public void setup() {
		super.setup();
	}

	@Override
	public void makePlay(ACLMessage msg) {
		if(targetRoom == null) { // 
			
			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
			int minDistance = 9999;
			targetCoord = null;
			
			if(currentTile.isRoom()) {
				ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(currentTile.getRoom());
				
				for(Tile door: roomDoors) {
					
					if(!door.isOccupied()) {
						
						while(true) {
							String randomRoom = Cluedo.rooms[r.nextInt(Cluedo.rooms.length)];
							if(!randomRoom.equals("Corridor") && !randomRoom.equals(currentTile.getRoom())) {
								ArrayList<Tile> roomDoors_2 = gameState.board.getRoomDoors(randomRoom);

								for(Tile door_2: roomDoors_2) {
									if(!door_2.isOccupied()) {
										int dist = (int) Board.getDistance(door.getCoordinates(), door_2.getCoordinates());
										if(dist < minDistance) {
											targetRoom = randomRoom;
											minDistance = dist;
											targetCoord = door_2.getCoordinates();
											doorToExit = door;
										}
									}
								}
							}
							
							if(targetRoom != null) {
								System.out.println("Trying to go to room"+targetRoom);
								System.out.println("Current posOnBoard: "+posOnBoard.toString());
								System.out.println("Trying to reach coord: "+targetCoord.toString());
								System.out.println("Leaving through door at: "+doorToExit.getCoordinates().toString());
								break;
							}
						}
						
					}
				}
				
				if(targetRoom != null) {
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
					
					askDiceRoll();
				} else { // the player is blocked in the room
					// TODO temporary
					// it should make a new suggestion then
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - BLOCKED INSIDE THIS ROOM: "+currentTile.getRoom());
					endMyTurn();
				}
			} else {

				// calculate the closest room and door for it that the bot player hasn't visited yet
				
				while(true) {
					String randomRoom = Cluedo.rooms[r.nextInt(Cluedo.rooms.length)];
					if(!randomRoom.equals("Corridor")) {
						ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(randomRoom);

						for(Tile door: roomDoors) {
							if(!door.isOccupied()) {
								int dist = (int) Board.getDistance(posOnBoard, door.getCoordinates());
								if(dist < minDistance) {
									targetRoom = randomRoom;
									minDistance = dist;
									targetCoord = door.getCoordinates();
								}
							}
						}
						
						if(targetRoom != null) {
							break;
						}
					}
				}
				
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING TO RANDOM ROOM: "+targetRoom);
				askDiceRoll();
			}
		} else {
			// TODO has to do different things?
			askDiceRoll();
		}
	}
	
	@Override
	public void handleDiceRollResult(ACLMessage msg, int diceResult) {
		if(targetCoord != null) {
			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
			boolean enterRoom = false;

			if(currentTile.isRoom()) {
				
				if(Board.getDistance(posOnBoard, targetCoord) < diceResult) { // he can get to the room
					enterRoom = true;
				} else { // can't get to the room this turn, goes closer
					ArrayList<Tile> reachableTiles = new ArrayList<>();
					gameState.board.buildReachableTiles(doorToExit.getNeighbours(), reachableTiles, diceResult-1);

					Coordinates destCoord = null;
					int minDistance = 9999;

					for(Tile tile: reachableTiles) {
						int dist = (int) Board.getDistance(targetCoord, tile.getCoordinates());
						if(dist < minDistance) {
							destCoord = tile.getCoordinates();
							minDistance = dist;
						}
					}

					// make the move to the tile closest to the target coord
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - getting closer to the targetCoord: "+targetCoord.getX()+"-"+targetCoord.getY()+" by going to "+destCoord.getX()+"-"+destCoord.getY());
					makeMove(destCoord.getX(), destCoord.getY());
				}
				
			} else {
				if(Board.getDistance(posOnBoard, targetCoord) < diceResult) { // he can get to the room
					enterRoom = true;
				} else { // can't get to the room this turn, goes closer
					ArrayList<Tile> reachableTiles = new ArrayList<>();
					gameState.board.buildReachableTiles(currentTile.getNeighbours(), reachableTiles, diceResult-1);

					Coordinates destCoord = null;
					int minDistance = 9999;

					for(Tile tile: reachableTiles) {
						int dist = (int) Board.getDistance(targetCoord, tile.getCoordinates());
						if(dist < minDistance) {
							destCoord = tile.getCoordinates();
							minDistance = dist;
						}
					}

					// make the move to the tile closest to the target coord
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - getting closer to the targetCoord: "+targetCoord.getX()+"-"+targetCoord.getY()+" by going to "+destCoord.getX()+"-"+destCoord.getY());
					makeMove(destCoord.getX(), destCoord.getY());
				}
			}
			
			if(enterRoom) {
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - I can get into the room.");

				for(int i = targetCoord.getY() - 1; i <= targetCoord.getY() + 1; i++) {
					for(int j = targetCoord.getX() - 1; j <= targetCoord.getX() + 1; j++) {
						Tile tile = gameState.board.getTileAtPosition(new Coordinates(j, i));

						if(tile.isRoom() && tile.getRoom().equals(targetRoom)) {
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - entering room!");
							makeMove(tile.getCoordinates().getX(), tile.getCoordinates().getY());
							break;
						}
					}

					if(madeBoardMove) {
						break;
					}
				}
			}
			
		} else {
			// it shouldn't get here
			System.out.println("No target coord after requesting a dice roll");
		}
	}

	@Override
	public void handleCardFromPlayer(ACLMessage msg, CluedoCard card) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleHasCardToContradict(CluedoSuggestion playerSuggestion,
			String playerThatContradicted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleNoCardToContradict(CluedoSuggestion playerSuggestion,
			String playerThatContradicted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleContradictedSuggestion(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlePlayerSuggestion(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleValidMoveMsg(ACLMessage msg) {
		
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
		
		if(currentTile.isRoom()) {
			
			targetRoom = null;
			targetCoord = null;
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - i'm inside a room. I can make a suggestion right?");
			endMyTurn(); // TODO temporary
			
		} else {
			endMyTurn();
		}
	}

	@Override
	public void handleInvalidMoveMsg(ACLMessage msg) {
		System.out.println("I made an invalid move. Should not get here!");
	}
}
