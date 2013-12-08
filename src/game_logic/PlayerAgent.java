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

import game_logic.Cluedo.GameState;
import game_ui.UIGame;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

 public abstract class PlayerAgent extends Agent 
 { 
	private static final long serialVersionUID = -4614773070990660799L;
	
	protected Logger myLogger = Logger.getMyLogger(getClass().getName());
	protected ArrayList<CluedoCard> myCards = null;
	
	protected boolean stillInGame;
	protected boolean myTurn;
	protected boolean waitingForDiceResult = false;
	protected boolean pickingBoardMove = false;
	protected boolean waitingForMoveConfirmation = false;
	protected boolean madeBoardMove = false;
	protected int diceResult = -1;
	
	protected GameState gameState = null; 
	protected Coordinates posOnBoard;

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
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
		
		// move to the corridor container
		ContainerID cid = new ContainerID("Corridor", null);
		doMove(cid);
		
		//addBehaviour(new PlayerBehaviour()); TODO it doesn't work, why??
	}
	
	public boolean isStillInGame() {
		return stillInGame;
	}
	
	/**
	 * sends the game manager a msg asking for a dice roll result
	 */
	protected void askDiceRoll() {
		waitingForDiceResult = true;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - sending request for a dice roll.");
		GameMessage msg = new GameMessage(GameMessage.ASK_DICE_ROLL);
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	/**
	 * sends the game manager a msg asking to move to this board location
	 * @param x
	 * @param y
	 */
	protected void makeMove(int x, int y) {
		pickingBoardMove = false;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - sending request for making a move.");
		
		GameMessage msg = new GameMessage(GameMessage.MAKE_MOVE);
		msg.addObject(new Integer(x));
		msg.addObject(new Integer(y));
		
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
		waitingForMoveConfirmation = true;
	}
	
	/**
	 * ends this players turn
	 */
	protected void endMyTurn() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - ending this agents turn.");
		GameMessage msg = new GameMessage(GameMessage.END_TURN);
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	protected void sendGameMessage(GameMessage gameMsg, AID receiver, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		
		try {
			msg.setContentObject(gameMsg);
			msg.addReceiver(receiver);
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	protected abstract void makePlay();
	
	private class PlayerBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 6875374470516568688L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();
			
			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					System.out.println("PLAYERAGENT: "+message.getType());
					
					switch (message.getType()) {

					case GameMessage.DISTRIBUTE_CARDS: // receiving this players cards and initial position
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);

							// send ack
							GameMessage msg_ack = new GameMessage(GameMessage.ACK_DISTRIBUTE_CARDS);
							sendGameMessage(msg_ack, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
							
						} else {
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Receiving cards and initial position again.");
						}
					}
					break;
//					case GameMessage.TURN_PLAYER: // receiving the name of the current turn's player
//					{
//						String turnPlayerName = (String) message.getObject(0);
//						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received turn player name "+turnPlayerName);
//
//						if(turnPlayerName.equals(myAgent.getLocalName())) {
//							myTurn = true;
//							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - it's my turn!");
//
//							makePlay();
//						}
//					}
//					break;
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
 }