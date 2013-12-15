package game_logic;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class SmartBotPlayer extends BotPlayerAgent {

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
					if (targetCoord != null)
						askDiceRoll();
					else
						makeBotSuggestionWithNotebook(currentTile);
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

		// Gets closest unchecked room
		for(Tile door: roomDoors) {
			if(!door.isOccupied()) {

				ArrayList<String> rooms = playerNotebook.getNotCheckedRooms(); 
				for (String room : rooms) {

					if(!room.equals(currentTile.getRoom())) {
						ArrayList<Tile> roomDoors_2 = gameState.board.getRoomDoors(room);

						for(Tile door_2: roomDoors_2) {
							if(!door_2.isOccupied()) {
								ArrayList<Tile> path = gameState.board.djs(door.getCoordinates(), door_2.getCoordinates());
								int dist = path.size();

								if(dist < minDistance) {
									targetRoom = room;
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

	private void calculateNewPathFromCorridor() {
		int minDistance = 9999;
		targetCoord = null;
		minimumPath = null;
		targetRoom = null;

		ArrayList<String> rooms = playerNotebook.getNotCheckedRooms();

		// Gets closest unchecked room
		for (String room : rooms) {

			if(!room.equals("Corridor")) {
				ArrayList<Tile> roomDoors = gameState.board.getRoomDoors(room);

				for(Tile door: roomDoors) {
					if(!door.isOccupied()) {

						ArrayList<Tile> path = gameState.board.djs(posOnBoard, door.getCoordinates());
						int dist = path.size();

						if(dist < minDistance) {
							targetRoom = room;
							minDistance = dist;
							minimumPath = path;
							targetCoord = door.getCoordinates();
						}
					}
				}
			}
		}
	}

	public void getToUncheckedRoom() {
		if(targetRoom == null) { // 

			Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

			if(currentTile.isRoom()) {
				calculateNewPathFromRoom(currentTile);

				if(targetRoom != null && targetCoord != null) {
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
					askDiceRoll();
				} else { // the player is blocked in the room
					//					System.out.println("BLOCKED IN ROOM!!!");
					makeBotSuggestionWithNotebook(currentTile);
				}

			} else {
				calculateNewPathFromCorridor();
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING TO RANDOM ROOM: "+targetRoom);
				if (targetCoord != null && targetRoom != null)
					askDiceRoll();
				else
					endMyTurn();
			}
		} else 
			if (targetCoord != null)
				askDiceRoll();
			else
				endMyTurn();
	}

	private void makeBotSuggestionWithNotebook(Tile current) {

		String suspect = null, weapon = null;

		suspect = playerNotebook.getMostProbableSolutionSuspect();
		weapon = playerNotebook.getMostProbableSolutionWeapon();

		makeSuggestion(new CluedoSuggestion(current.getRoom(), suspect, weapon, getLocalName()));
	}

	@Override
	public void handleCardFromPlayer(ACLMessage msg, CluedoCard card) {
		playerNotebook.updateCardState(card.getName(), CluedoNotebook.NOT_SOLUTION);		
		playerNotebook.saveOtherPlayerCard(card.getName(), msg.getSender().getLocalName());

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
		tryToDeductContradictedCard(playerSuggestion, playerThatContradicted);
		
		playerNotebook.addSuspectSuggestedByOtherPlayer(playerSuggestion.getSuspect());
		playerNotebook.addWeaponSuggestedByOtherPlayer(playerSuggestion.getWeapon());
	}

	/**
	 * Receives a suggestion and the name of the player that has made the contradiction
	 * and try to eliminate 2 of the 3 cards by checking the info this player keeps about
	 * who keeps which cards
	 * 
	 * @param playerSuggestion
	 * @param playerThatContradicted
	 */
	private void tryToDeductContradictedCard(CluedoSuggestion playerSuggestion,
			String playerThatContradicted) {
		int checkedCardsCounter = 0;
		String cardDeducted = null;

		ArrayList<String> suggestionCards = new ArrayList<>();
		suggestionCards.add(playerSuggestion.getSuspect());
		suggestionCards.add(playerSuggestion.getRoom());
		suggestionCards.add(playerSuggestion.getWeapon());

		ArrayList<String> myCardsString = new ArrayList<>();
		for (CluedoCard card : myCards)
			myCardsString.add(card.getName());

		for (String card : suggestionCards) {			
			String playerWhoHasTheCard = playerNotebook.getPlayerWhoHasCard(card);

			if (!(myCardsString.contains(card) || 
					(playerWhoHasTheCard != null &&
					!playerWhoHasTheCard.equals(playerThatContradicted)))) {

				cardDeducted = card;
				checkedCardsCounter++;
			}
		}

		// Made a deduction
		if (checkedCardsCounter == 1) {
			playerNotebook.updateCardState(cardDeducted, CluedoNotebook.NOT_SOLUTION);
		}
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

				CluedoCard card = getCardToContradict(playerSuggestion.getPlayer(), cardsToContradict);

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

	/**
	 * If the player has shown one card to the player who made the suggestion,
	 * it will show the same card again, so that the other player learn the less
	 * about this player's hand
	 * 
	 * @param player
	 * @param cardsToContradict
	 * @return
	 */
	private CluedoCard getCardToContradict(
			String player, ArrayList<CluedoCard> cardsToContradict) {

		for (CluedoCard card : cardsToContradict)
			if (playerNotebook.hasShownCardToPlayer(player, card))
				return card;

		CluedoCard card = cardsToContradict.get(r.nextInt(cardsToContradict.size()));
		playerNotebook.addCardShownToPlayer(player, card);
		return card;
	}

	@Override
	public void handlePlayerSuggestion(ACLMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInvalidMoveMsg(ACLMessage msg) {
		// 
		//		System.out.println("I MADE AN INVALID MOVE. SOME TILE IN THE PATH MUST BE OCCUPIED NOW");
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);

		if(currentTile.isRoom()) {
			calculateNewPathFromRoom(currentTile);

			if(targetRoom != null && targetCoord != null) {
				myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NEXT RANDOM ROOM IS: "+targetRoom);
				askDiceRoll();
			} else { // the player is blocked in the room
				makeBotSuggestionWithNotebook(currentTile);
			}

		} else {
			calculateNewPathFromCorridor();
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOING INSTEAD TO RANDOM ROOM: "+targetRoom);
			if (targetRoom != null && targetCoord != null)
				askDiceRoll();
			else
				endMyTurn();
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
					if (playerNotebook.getNotCheckedRooms().contains(currentTile.getRoom())) {
						makeBotSuggestionWithNotebook(currentTile);
						return;
					}

					Coordinates destTileCoords = minimumPath.get(diceResult - 1).getCoordinates(); // this time is - 1 because he needs to move to the first coord of the path
					makeMove(destTileCoords.getX(), destTileCoords.getY());
				}
			} else {

				if(minimumPath.size() <= diceResult) { // he can get to the room
					// TODO needs to check if the room door is still free
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
			//			System.out.println("No target coord after requesting a dice roll");
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
