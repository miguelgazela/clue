package game_logic;

import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class NormalBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 2702856073265411877L;
	private CluedoNotebook playerNotebook = new CluedoNotebook();
	private String roomSolutionString = null;
	private String suspectSolutionString = null;
	private String weaponSolutionString = null;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new NormalBotPlayer.");
		playerNotebook.addPlayerCards(myCards);
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
					
				}
			}
			// In the corridor
			else {
				
			}
		}
		// Room not known
		else {
			
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
		// TODO Auto-generated method stub
		
	}

}
