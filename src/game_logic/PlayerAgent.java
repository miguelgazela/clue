package game_logic;
// ------------------------------------------------------------
//   ParamAgent:   An Agent receiving parameters             
//
//   Usage:    % javac ParamAgent.java
//             % java jade.Boot  fred:ParamAgent(3 "Allo there")
//
// ... on UNIX, the agent specifier and arguments must be quoted:
//
//             % java jade.Boot 'fred:ParamAgent(3 "Allo there")'
// ------------------------------------------------------------

import java.io.IOException;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

 public class PlayerAgent extends Agent 
 { 
	private static final long serialVersionUID = -4614773070990660799L;
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private ArrayList<CluedoCard> myCards = null;
	private boolean stillInGame;
	private boolean myTurn;
	private Coordinates posOnBoard;

	protected void setup() 
	{ 
		stillInGame = true;
		myTurn = false;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - ready to play, sending READY msg.");

		try {
			// create the agent description of itself and register it
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
		} catch (FIPAException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// notify the game manager agent that this player is ready to play
		GameMessage msg = new GameMessage(GameMessage.READY_PLAY);
		ACLMessage ready = new ACLMessage(ACLMessage.INFORM);
		
		try {
			ready.setContentObject(msg);
			ready.addReceiver(new AID("host", AID.ISLOCALNAME));
			send(ready);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		addBehaviour(new PlayerBehaviour());
	}
	
	public boolean isStillInGame() {
		return stillInGame;
	}
	
	/**
	 * sends the game manager a msg asking for a dice roll result
	 */
	public void askDiceRoll() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - sending request for a dice roll.");
		GameMessage msg = new GameMessage(GameMessage.ASK_DICE_ROLL);
		ACLMessage diceRollRequest = new ACLMessage(ACLMessage.INFORM);
		
		try {
			diceRollRequest.setContentObject(msg);
			diceRollRequest.addReceiver(new AID("host", AID.ISLOCALNAME));
			send(diceRollRequest);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private class PlayerBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 6875374470516568688L;

		@Override
		public void action() {
			ACLMessage msg = receive();

			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					switch (message.getType()) {

					case GameMessage.DISTRIBUTE_CARDS_AND_POS: // receiving this players cards and initial position
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);

							// send ack
							GameMessage msg_ack = new GameMessage(GameMessage.ACK_DISTRIBUTE_CARDS);
							ACLMessage ack = new ACLMessage(ACLMessage.INFORM);

							try {
								ack.setContentObject(msg_ack);
								ack.addReceiver(msg.getSender());
								send(ack);
							} catch (Exception e) {
								e.printStackTrace();
								System.exit(-1);
							}

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

							// can do different things here!
							((PlayerAgent)myAgent).askDiceRoll();
						}

					}
					break;
					case GameMessage.RSLT_DICE_ROLL: // receiving the name of the current turn's player
					{
						int diceResult = ((Integer) message.getObject(0)).intValue();
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - rolled the dice and got "+diceResult);
					}
						break;
					default:
					{
						// should not get here!!!
						System.exit(-1);
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
 }