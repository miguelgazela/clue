package game_logic;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class BotPlayerAgent extends PlayerAgent {

	private static final long serialVersionUID = -6042695269335080044L;

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
