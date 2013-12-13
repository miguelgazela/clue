package game_logic;

import java.util.ArrayList;

import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SmartBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 7479738606280331848L;
	private String roomSolutionString = null;
	private String suspectSolutionString = null;
	private String weaponSolutionString = null;
	private ArrayList<Tile> minPath;
	private Coordinates targetCoord = null;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new SmartBotPlayer.");
	}

	@Override
	public void makePlay(ACLMessage msg) {
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
		
		// Room is known.
		if (roomSolutionString != null) {
			if (currentTile.isRoom()) {
				// In solution room.
				if (currentTile.getRoom().equals(roomSolutionString)) {
					// Make suggestion.
					if (suspectSolutionString == null || weaponSolutionString == null) {
						
					}
					// Make accusation.
					else {
						
					}
				}
				// In other room. Needs to get out and try to reach solution room.
				else {
					
					
					//TODO igual a corridar mas ver cada porta da sala onde está
				}
			}
			// In the corridor
			else {
				
			}
		}
		// Room not known
		else {
			askDiceRoll();
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
	public void handleInvalidMoveMsg(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleValidMoveMsg(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleDiceRollResult(ACLMessage msg, int diceResult) {
		Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
		
		// Room is known.
		if (roomSolutionString != null) {
			if (currentTile.isRoom()) {
				// In solution room.
				if (currentTile.getRoom().equals(roomSolutionString)) {
					// Make suggestion.
					if (suspectSolutionString == null || weaponSolutionString == null) {
						
					}
					// Make accusation.
					else {
						
					}
				}
				// In other room. Needs to get out and try to reach solution room.
				else {
					
					
					//TODO igual a corridar mas ver cada porta da sala onde está
				}
			}
			// In the corridor
			else {
				int minDist = 9999;
				for(Tile door : gameState.board.getRoomDoors(roomSolutionString)) {
					ArrayList<Tile> tempPath;
					if((tempPath = gameState.board.djs(posOnBoard, door.getCoordinates())).size() < minDist)
						minPath = tempPath;
				}
			}
		}
		// Room not known
		else {
			if (currentTile.isRoom()) {
				//TODO verificar se room is checked e mover ou fazer sugestao
			}
			// In the corridor
			else { //go to the closest unchecked room
				int minDist = 9999;
				ArrayList<Tile> tempPath;
				for(String room : playerNotebook.getNotCheckedRooms()) {
					if((tempPath = getMinDistToRoom(room,posOnBoard,diceResult)).size() < minDist)
						minPath = tempPath;
				}
			targetCoord = minPath.get(diceResult-1).getCoordinates();
			}
			
			
			
		}
		
	}
	private ArrayList<Tile> getMinDistToRoom(String room, Coordinates source, int diceResult) {
		int minDist = 9999;
		ArrayList<Tile> bestPath = null;
		for(Tile door : gameState.board.getRoomDoors(room)) {
			ArrayList<Tile> tempPath;
			if((tempPath = gameState.board.djs(source, door.getCoordinates())).size() < minDist)
				bestPath = tempPath;
		}
		return bestPath;
	}
}
