package game_logic;

import java.util.ArrayList;
import java.util.ListIterator;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public abstract class BotPlayerAgent extends PlayerAgent {

	private static final long serialVersionUID = -6042695269335080044L;
	
	protected CluedoNotebook playerNotebook = new CluedoNotebook();

	public void setup() {
		super.setup();
		addBehaviour(new BotPlayerBehaviour());
	}
	
	private class BotPlayerBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 1374362294259200211L;

		@Override
		public void action() {
			ACLMessage msg = receive();

			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					switch (message.getType()) {
					case GameMessage.DISTRIBUTE_CARDS: // receiving this players cards and initial game state
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							gameState = (Cluedo.GameState) message.getObject(1);
							posOnBoard = (Coordinates) message.getObject(2);

							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new NormalBotPlayer.");
							playerNotebook.addPlayerCards(myCards);
							
							// send ack
							GameMessage msg_ack = new GameMessage(GameMessage.ACK_DISTRIBUTE_CARDS);
							sendGameMessage(msg_ack, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
						} else {
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Receiving cards and initial position again.");
						}
					}
					break;
					case GameMessage.TURN_PLAYER: // receiving the name of the current turn's player
					{
						String turnPlayerName = (String) message.getObject(0);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received turn player name "+turnPlayerName);

						if(turnPlayerName.equals(myAgent.getLocalName())) {
							myTurn = true;
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - it's my turn");
							makePlay(msg);
						}
					}
					break;
					case GameMessage.RSLT_DICE_ROLL: // receiving the result of the dice roll
					{
						if(pickingBoardMove) {
							diceResult = ((Integer) message.getObject(0)).intValue();
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - rolled the dice and got "+diceResult);
							handleDiceRollResult(msg, diceResult);
						}
					}
					break;
					case GameMessage.VALID_MOVE:
					{
						if(madeBoardMove) { // our move has been done
							gameState = (Cluedo.GameState) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);
						}
						handleValidMoveMsg(msg);
					}
					break;
					case GameMessage.INVALID_MOVE:
					{
						madeBoardMove = false;
						pickingBoardMove = true;
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - my move is invalid");
						handleInvalidMoveMsg(msg);
					}
					break;
					case GameMessage.GAME_STATE_UPDATE:
					{
						gameState = (Cluedo.GameState) message.getObject(0);
					}
					break;
					case GameMessage.PLAYER_MADE_SUGGESTION:
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received suggestion warning");
						handlePlayerSuggestion(msg);
					}
					break;
					case GameMessage.CONTRADICT_SUGGESTION: 
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received request to contradict a suggestion");
						handleContradictedSuggestion(msg);
					}
					break;
					case GameMessage.NO_CONTRADICTION_CARD: // some player is contradicting another player
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received a NO contradiction to suggestion");
						String playerThatContradicted = (String)message.getObject(0); 
						CluedoSuggestion playerSuggestion = (CluedoSuggestion) message.getObject(1);
						handleNoCardToContradict(playerSuggestion, playerThatContradicted);
					}
					break;
					case GameMessage.HAVE_CONTRADICTION_CARD: // some player had a card to contradict another player's suggestion
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received contradiction to suggestion");
						String playerThatContradicted = (String)message.getObject(0); 
						CluedoSuggestion playerSuggestion = (CluedoSuggestion) message.getObject(1);
						handleHasCardToContradict(playerSuggestion, playerThatContradicted);
					}
					break;
					case GameMessage.CONTRADICT_CARD: // a card to contradict our suggestion
					{
						CluedoCard card = (CluedoCard) message.getObject(0);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received the card "+card.getName()+" to contradict my suggestion from "+msg.getSender().getLocalName());
						handleCardFromPlayer(msg, card);
					}
					break;
					default:
					{
						// should not get here!!!
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - unrecognized message.");
					}
					break;
					}
					
				} catch (UnreadableException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			} else { // if no message is arrived, block the behaviour
				block();
			}
		}
		
	}

	public abstract void makePlay(ACLMessage msg);

	public abstract void handleCardFromPlayer(ACLMessage msg, CluedoCard card);

	public abstract void handleHasCardToContradict(CluedoSuggestion playerSuggestion, String playerThatContradicted);
	
	public abstract void handleNoCardToContradict(CluedoSuggestion playerSuggestion, String playerThatContradicted);

	public abstract void handleContradictedSuggestion(ACLMessage msg);
	
	public abstract void handlePlayerSuggestion(ACLMessage msg);

	public abstract void handleInvalidMoveMsg(ACLMessage msg);

	public abstract void handleValidMoveMsg(ACLMessage msg);

	public abstract void handleDiceRollResult(ACLMessage msg, int diceResult);
}
