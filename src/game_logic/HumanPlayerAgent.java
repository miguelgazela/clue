package game_logic;

import java.util.ArrayList;

import game_ui.UIHumanPlayer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class HumanPlayerAgent extends PlayerAgent {

	private static final long serialVersionUID = -314248632846121693L;
	protected UIHumanPlayer myGui;

	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new human player.");
		myGui = new UIHumanPlayer(this, getLocalName());
		addBehaviour(new HumanPlayerBehaviour());
	}
	
	private class HumanPlayerBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = -3630440372660298200L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();

			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					System.out.println("HUMANPLAYER: "+message.getType());

					switch (message.getType()) {
					case GameMessage.DISTRIBUTE_CARDS_AND_POS: // receiving this players cards and initial position
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);

							// send ack
							GameMessage msg_ack = new GameMessage(GameMessage.ACK_DISTRIBUTE_CARDS);
							sendGameMessage(msg_ack, new AID("host", AID.ISLOCALNAME));
							
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
							waitingForDiceResult = false;
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
		System.out.println("MakePlay do HumanPlayer");
		askDiceRoll();
	}
}
