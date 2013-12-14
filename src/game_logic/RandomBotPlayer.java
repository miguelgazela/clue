package game_logic;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class RandomBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 3588603829094518352L;
	
	private String targetRoom = null;
	private Coordinates targetCoord = null;
	private Tile doorToExit = null;
	private ArrayList<Tile> minimumPath;
	
	private Random r = new Random(System.currentTimeMillis());
	
	public void setup() {
		super.setup();
	}

	@Override
	public void makePlay(ACLMessage msg) {
		if(targetRoom == null) { // 
			
			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
			
			if(currentTile.isRoom()) {
				calculateNewPathFromRoom(currentTile);
				
				if(targetRoom != null) {
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
					askDiceRoll();
				} else { // the player is blocked in the room
//					System.out.println("BLOCKED IN ROOM!!!");
					makeRandomSuggestion(currentTile);
				}
				
			} else {
				calculateNewPathFromCorridor();
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING TO RANDOM ROOM: "+targetRoom);
				askDiceRoll();
			}
		} else {
			// TODO has to do different things?
			askDiceRoll();
		}
	}
	
	private void calculateNewPathFromRoom(Tile currentTile) {

		ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(currentTile.getRoom());
		int minDistance = 9999;
		targetRoom = null;
		targetCoord = null;
		doorToExit = null;
		minimumPath = null;
	
		for(Tile door: roomDoors) {
			if(!door.isOccupied()) {
				
				while(true) {
					String randomRoom = Cluedo.rooms[r.nextInt(Cluedo.rooms.length)];
					
					if(!randomRoom.equals("Corridor") && !randomRoom.equals(currentTile.getRoom())) {
						ArrayList<Tile> roomDoors_2 = gameState.board.getRoomDoors(randomRoom);

						for(Tile door_2: roomDoors_2) {
							if(!door_2.isOccupied()) {
								ArrayList<Tile> path = gameState.board.djs(door.getCoordinates(), door_2.getCoordinates());
								int dist = path.size();
								
								if(dist < minDistance) {
									targetRoom = randomRoom;
									minDistance = dist;
									minimumPath = path;
									targetCoord = door_2.getCoordinates();
									doorToExit = door;
								}
							}
						}
					}
					
					if(targetRoom != null) {
//						System.out.println("Trying to go to room"+targetRoom);
//						System.out.println("Current posOnBoard: "+posOnBoard.toString());
//						System.out.println("Trying to reach coord: "+targetCoord.toString());
//						System.out.println("Leaving through door at: "+doorToExit.getCoordinates().toString());
						break;
					}
				}
			}
		}
	}
	
	private void calculateNewPathFromCorridor() {
		int minDistance = 9999;
		targetCoord = null;
		
		while(true) {
			String randomRoom = Cluedo.rooms[r.nextInt(Cluedo.rooms.length)];
			if(!randomRoom.equals("Corridor")) {
				ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(randomRoom);

				for(Tile door: roomDoors) {
					if(!door.isOccupied()) {
						
						ArrayList<Tile> path = gameState.board.djs(posOnBoard, door.getCoordinates());
						int dist = path.size();
						
						if(dist < minDistance) {
							targetRoom = randomRoom;
							minDistance = dist;
							minimumPath = path;
							targetCoord = door.getCoordinates();
						}
					}
				}
				
				if(targetRoom != null && targetCoord != null) {
					break;
				}
			}
		}
	}
	
	private void makeRandomSuggestion(Tile current) {
		
		String suspect = null, weapon = null;
		
		while(true) {
			int cardsOwnedByPlayer = 0;
			
			// pick a random suspect
			suspect = Cluedo.suspects[r.nextInt(Cluedo.suspects.length)];
			
			// pick a random weapon
			weapon = Cluedo.weapons[r.nextInt(Cluedo.weapons.length)];
			
			for(CluedoCard card: myCards) {
				if(card.getName().equals(suspect) || card.getName().equals(weapon)) {
					cardsOwnedByPlayer++;
				}
			}
			
			if(cardsOwnedByPlayer < 2) {
				break;
			}
		}
		
		makeSuggestion(new CluedoSuggestion(current.getRoom(), suspect, weapon, getLocalName()));
	}
	
	@Override
	public void handleDiceRollResult(ACLMessage msg, int diceResult) {
		if(targetCoord != null) {
			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
			boolean enterRoom = false;

			if(currentTile.isRoom()) {
				
				if(minimumPath.size() < diceResult) { // he can get to the room
					enterRoom = true;
				} else { // can't get to the room this turn, goes closer
					Coordinates destTileCoords = minimumPath.get(diceResult - 1).getCoordinates(); // this time is - 1 because he needs to move to the first coord of the path
					makeMove(destTileCoords.getX(), destTileCoords.getY());
				}
			} else {
				
				if(minimumPath.size() <= diceResult) { // he can get to the room
					enterRoom = true;
				} else {
					
					// goes to the minimumPath and moves to the diceResulth position
					Coordinates destTileCoords = minimumPath.get(diceResult).getCoordinates();
					myLogger.log(Logger.INFO, "Agent "+getLocalName()
							+" - getting closer to the targetCoord: "
							+targetCoord.getX()+"-"
							+targetCoord.getY()
							+" by going to "+destTileCoords.getX()
							+"-"+destTileCoords.getY()
					);
					makeMove(destTileCoords.getX(), destTileCoords.getY());
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
//			System.out.println("No target coord after requesting a dice roll");
		}
	}

	@Override
	public void handleCardFromPlayer(ACLMessage msg, CluedoCard card) {
		// ignore card and endTurn because I just made a suggestion
		endMyTurn();
	}

	@Override
	public void handleHasCardToContradict(CluedoSuggestion playerSuggestion,
			String playerThatContradicted) {
		// ignores this as well
	}

	@Override
	public void handleNoCardToContradict(CluedoSuggestion playerSuggestion,
			String playerThatContradicted) {
		// and this
	}

	@Override
	public void handleContradictedSuggestion(ACLMessage msg) {
		try {
			GameMessage gameMsg = (GameMessage) msg.getContentObject();
			CluedoSuggestion playerSuggestion = (CluedoSuggestion) gameMsg.getObject(0);
			
			ArrayList<CluedoCard> cardsToContradict = new ArrayList<>();
			
			// see if I have any card that has been suggested
			for(CluedoCard card: myCards) {
				String cardName = card.getName();
				
				// i have one card to contradict, say yes to gamemanager and send card to the requester
				if(cardName.equals(playerSuggestion.getRoom()) 
						|| cardName.equals(playerSuggestion.getSuspect()) 
						|| cardName.equals(playerSuggestion.getWeapon())) {
					cardsToContradict.add(card);
				}
			}
			
			if(cardsToContradict.size() != 0) {
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - HAVE AT LEAST 1 CARD TO CONTRADICT");
				GameMessage haveContrCard = new GameMessage(GameMessage.HAVE_CONTRADICTION_CARD);
				haveContrCard.addObject(playerSuggestion);
				sendGameMessage(haveContrCard, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
				
				CluedoCard card = cardsToContradict.get(r.nextInt(cardsToContradict.size()));
				
				// send the card to the player that asked it
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - SEND CARD "+card.getName()+" TO "+playerSuggestion.getPlayer());
				GameMessage contradictionCard = new GameMessage(GameMessage.CONTRADICT_CARD);
				contradictionCard.addObject(card);
				sendGameMessage(contradictionCard, new AID(playerSuggestion.getPlayer(), AID.ISLOCALNAME), ACLMessage.INFORM);
				return;
			}
			
			// send msg to game manager saying you don't have a card
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NO CARD TO CONTRADICT.");
			GameMessage noContrCard = new GameMessage(GameMessage.NO_CONTRADICTION_CARD);
			noContrCard.addObject(playerSuggestion);
			sendGameMessage(noContrCard, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);

		} catch (UnreadableException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void handlePlayerSuggestion(ACLMessage msg) {
		// this is ignored as well
	}
	
	@Override
	public void handleValidMoveMsg(ACLMessage msg) {
		
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
		
		if(currentTile.isRoom()) { // if he moved inside a room
			targetRoom = null;
			targetCoord = null;
			minimumPath = null;
			
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - I'M INSIDE A ROOM. MAKING A SUGGESTION");
			makeRandomSuggestion(currentTile);
			
		} else {
			// delete the part of the path that he moved
			ListIterator<Tile> it = minimumPath.listIterator();
			while(it.hasNext()) {
				Tile current = it.next();
				
				if(current.getCoordinates().equals(posOnBoard)) { // doesn't remove the current title
					break;
				}
				
				it.remove();
			}
			endMyTurn();
		}
	}

	@Override
	public void handleInvalidMoveMsg(ACLMessage msg) {
		// 
//		System.out.println("I MADE AN INVALID MOVE. SOME TILE IN THE PATH MUST BE OCCUPIED NOW");
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
		
		if(currentTile.isRoom()) {
			calculateNewPathFromRoom(currentTile);
			
			if(targetRoom != null) {
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
				askDiceRoll();
			} else { // the player is blocked in the room
				makeRandomSuggestion(currentTile);
			}
			
		} else {
			calculateNewPathFromCorridor();
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING INSTEAD TO RANDOM ROOM: "+targetRoom);
			askDiceRoll();
		}
	}
	
	@Override
	public void resetState() {
	}
}

