package game_logic;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class BotPlayerAgent extends PlayerAgent {

	private static final long serialVersionUID = -6042695269335080044L;

	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new human player.");
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
					case GameMessage.DISTRIBUTE_CARDS: // receiving this players cards and initial position
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							gameState = (Cluedo.GameState) message.getObject(1);

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
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - it's my turn!");

							makePlay();
						}
					}
					break;
					case GameMessage.RSLT_DICE_ROLL: // receiving the result of the dice roll
					{
						if(waitingForDiceResult) {
							int diceResult = ((Integer) message.getObject(0)).intValue();
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - rolled the dice and got "+diceResult);
						}
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

	@Override
	protected void makePlay() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - doing bot play");
	}
}
