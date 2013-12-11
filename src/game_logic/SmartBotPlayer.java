package game_logic;

import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SmartBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 7479738606280331848L;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new SmartBotPlayer.");
	}

	@Override
	public void makePlay(ACLMessage msg) {
		// TODO Auto-generated method stub
		
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
