package game_logic;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class NormalBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 2702856073265411877L;
	private String roomSolutionString = null;
	private String suspectSolutionString = null;
	private String weaponSolutionString = null;

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
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

		// Room is known.
		if (roomSolutionString != null) {
			if (suspectSolutionString != null && weaponSolutionString != null)
				makeAccusation(new CluedoSuggestion(roomSolutionString, suspectSolutionString, weaponSolutionString, getLocalName()));

			else if (currentTile.isRoom()) {
				// In solution room.
				if (currentTile.getRoom().equals(roomSolutionString)) {
					// Make suggestion.
					makeBotSuggestionWithNotebook(currentTile);
				}
				// In other room. Needs to get out and try to reach solution room.
				else {
					buildPathFromRoomToRoom(roomSolutionString, currentTile);
					if(targetCoord != null)
						askDiceRoll();
					else
						makeBotSuggestionWithNotebook(currentTile); //if room is blocked make another sugestion
				}
			}
			// In the corridor
			else {
				buildPathFromCorridorToRoom(roomSolutionString, currentTile);
				if (targetCoord != null)
					askDiceRoll();
				else
					endMyTurn();
			}
		}
		// Room not known
		else 
			getToUncheckedRoom();
	}

	private void buildPathFromCorridorToRoom(String dest, Tile currentTile) {
		int minDistance = 9999;
		targetCoord = null;
		minimumPath = null;
		targetRoom = null;

		ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(dest);

		for(Tile door: roomDoors) {
			if(!door.isOccupied()) {

				ArrayList<Tile> path = gameState.board.djs(posOnBoard, door.getCoordinates());
				int dist = path.size();

				if(dist < minDistance) {
					targetRoom = dest;
					minDistance = dist;
					minimumPath = path;
					targetCoord = door.getCoordinates();
				}
			}
		}
		if(targetCoord == null){ //if solution room is blocked, go to a near neighboor
			for(Tile door: roomDoors) {
				for(Tile neighboor: door.getNeighbours()) {
					if(!neighboor.isOccupied()) {

						ArrayList<Tile> path = gameState.board.djs(posOnBoard, neighboor.getCoordinates());
						int dist = path.size();

						if(dist < minDistance) {
							targetRoom = dest;
							minDistance = dist;
							minimumPath = path;
							targetCoord = neighboor.getCoordinates();
						}
					}
				}
			}
		}

	}

	private void buildPathFromRoomToRoom(String dest, Tile currentTile) {
		ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(currentTile.getRoom());
		int minDistance = 9999;
		targetRoom = null;
		targetCoord = null;
		doorToExit = null;
		minimumPath = null;

		ArrayList<Tile> roomDoors_2 = gameState.board.getRoomDoors(dest);
		
		for(Tile door: roomDoors) {
			if(!door.isOccupied()) {
				for(Tile door_2: roomDoors_2) {
					if(!door_2.isOccupied()) {
						ArrayList<Tile> path = gameState.board.djs(door.getCoordinates(), door_2.getCoordinates());
						int dist = path.size();

						if(dist < minDistance) {
							targetRoom = dest;
							minDistance = dist;
							minimumPath = path;
							targetCoord = door_2.getCoordinates();
							doorToExit = door;
						}
					}
				}
			}
		}
		if(targetCoord == null) {
			for(Tile door: roomDoors) {
				if(!door.isOccupied()) {
					for(Tile door_2: roomDoors_2) {
						for(Tile neighboor: door.getNeighbours()) {
							if(!door_2.isOccupied()) {
								ArrayList<Tile> path = gameState.board.djs(door.getCoordinates(), neighboor.getCoordinates());
								int dist = path.size();

								if(dist < minDistance) {
									targetRoom = dest;
									minDistance = dist;
									minimumPath = path;
									targetCoord = door_2.getCoordinates();
									doorToExit = door;
								}
							}
						}
					}
				}
			}
		}
	}

	private void calculateNewPathFromRoom(Tile currentTile) {

		ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(currentTile.getRoom());
		int minDistance = 9999;
		targetRoom = null;
		targetCoord = null;
		doorToExit = null;
		minimumPath = null;
		
		ArrayList<String> uncheckedRooms = new ArrayList<String> (playerNotebook.getNotCheckedRooms()); 
		
		for(Tile door: roomDoors) {
			if(!door.isOccupied()) {
				while (!uncheckedRooms.isEmpty()) {
					String randomRoom = uncheckedRooms.get(r.nextInt(uncheckedRooms.size()));
					uncheckedRooms.remove(randomRoom);
					
					if(!randomRoom.equals(currentTile.getRoom())) {
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
						
						if(targetRoom != null && targetCoord != null)
							break;
					}
				}
			}
		}
	}

	private void calculateNewPathFromCorridor() {
		int minDistance = 9999;
		targetCoord = null;
		minimumPath = null;
		targetRoom = null;

		ArrayList<String> uncheckedRooms = new ArrayList<String> (playerNotebook.getNotCheckedRooms()); 
		
		while(!uncheckedRooms.isEmpty()) {
			String randomRoom = uncheckedRooms.get(r.nextInt(uncheckedRooms.size()));
			uncheckedRooms.remove(randomRoom);
			
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

				if(targetRoom != null && targetCoord != null)
					break;
			}
		}
	}

	public void getToUncheckedRoom() {
		if(targetRoom == null) { // 

			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

			if(currentTile.isRoom()) {
				calculateNewPathFromRoom(currentTile);

				if(targetRoom != null) {
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
					askDiceRoll();
				} else { // the player is blocked in the room
					makeBotSuggestionWithNotebook(currentTile);
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

	private void makeBotSuggestionWithNotebook(Tile current) {

		String suspect = null, weapon = null;
		ArrayList<String> uncheckedSuspects = playerNotebook.getNotCheckedSuspects();
		ArrayList<String> uncheckedWeapons = playerNotebook.getNotCheckedWeapons();

		// pick a random unchecked suspect
		suspect = uncheckedSuspects.get(r.nextInt(uncheckedSuspects.size()));
		// pick a random unchecked weapon
		weapon = uncheckedWeapons.get(r.nextInt(uncheckedWeapons.size()));
		
		makeSuggestion(new CluedoSuggestion(current.getRoom(), suspect, weapon, getLocalName()));
	}

	@Override
	public void handleCardFromPlayer(ACLMessage msg, CluedoCard card) {
		playerNotebook.updateCardState(card.getName(), CluedoNotebook.NOT_SOLUTION);

		ArrayList<String> uncheckedSuspects = playerNotebook.getNotCheckedSuspects();
		ArrayList<String> uncheckedWeapons = playerNotebook.getNotCheckedWeapons();
		ArrayList<String> uncheckedRooms = playerNotebook.getNotCheckedRooms();

		if (uncheckedSuspects.size() == 1)
			suspectSolutionString = uncheckedSuspects.get(0);
		if (uncheckedWeapons.size() == 1)
			weaponSolutionString = uncheckedWeapons.get(0);
		if (uncheckedRooms.size() == 1)
			roomSolutionString = uncheckedRooms.get(0);

		endMyTurn();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInvalidMoveMsg(ACLMessage msg) {
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

		if(currentTile.isRoom()) {
			calculateNewPathFromRoom(currentTile);

			if(targetRoom != null) {
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
				askDiceRoll();
			} else { // the player is blocked in the room
				makeBotSuggestionWithNotebook(currentTile);
			}
		} else {
			calculateNewPathFromCorridor();
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING INSTEAD TO RANDOM ROOM: "+targetRoom);
			askDiceRoll();
		}
	}

	@Override
	public void handleValidMoveMsg(ACLMessage msg) {
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

		if (roomSolutionString != null && suspectSolutionString != null && weaponSolutionString != null)
			makeAccusation(new CluedoSuggestion(roomSolutionString, suspectSolutionString, weaponSolutionString, getLocalName()));

		else if(currentTile.isRoom()) { // if he moved inside a room
			targetRoom = null;
			targetCoord = null;
			minimumPath = null;

			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - I'M INSIDE A ROOM. MAKING A SUGGESTION");

			makeBotSuggestionWithNotebook(currentTile);

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
			// System.out.println("Normal Bot: No target coord after requesting a dice roll " + roomSolutionString);
			endMyTurn();
		}
	}

	@Override
	public void resetState() {
		playerNotebook = new CluedoNotebook();
		targetCoord = null;
		minimumPath = null;
		targetRoom = null;

		roomSolutionString = null;
		suspectSolutionString = null;
		weaponSolutionString = null;
	}

	public void handlePlayerAccusation(ACLMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleWrongAccusation(ACLMessage msg) {
		// TODO Auto-generated method stub		
	}

}
