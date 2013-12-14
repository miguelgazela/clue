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
import java.util.List;

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
	protected boolean pickingBoardMove = false;
	protected boolean madeBoardMove = false;
	protected boolean madeSuggestion = false;
	protected boolean madeAccusation = false;
	protected int diceResult = -1;
	
	protected GameState gameState = null; 
	protected Coordinates posOnBoard;

	protected void setup() 
	{ 
		stillInGame = true;
		myTurn = false;
		myLogger.setLevel(Logger.SEVERE);
		
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
	}
	
	public boolean isStillInGame() {
		return stillInGame;
	}
	
	/**
	 * sends the game manager a msg asking for a dice roll result
	 */
	protected void askDiceRoll() {
		pickingBoardMove = true;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - SENDING REQUEST FOR DICE ROLL.");
		GameMessage msg = new GameMessage(GameMessage.ASK_DICE_ROLL);
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	/**
	 * sends the game maneger a suggestion for the game solution
	 * @param room
	 * @param suspect
	 * @param weapon
	 */
	protected void makeSuggestion(CluedoSuggestion playerSuggestion) {
		madeSuggestion = true;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - SENDING SUGGESTION.");
		GameMessage msg = new GameMessage(GameMessage.MAKE_SUGGESTION);
		msg.addObject(playerSuggestion);
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	/**
	 * sends the game maneger an accusation for the game solution
	 * @param room
	 * @param suspect
	 * @param weapon
	 */
	protected void makeAccusation(CluedoSuggestion playerAccusation) {
		madeAccusation = true;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - SENDING ACCUSATION.");
		GameMessage msg = new GameMessage(GameMessage.MAKE_ACCUSATION);
		msg.addObject(playerAccusation);
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	/**
	 * sends the game manager a msg asking to move to this board location
	 * @param x
	 * @param y
	 */
	protected void makeMove(int x, int y) {
		pickingBoardMove = false;
		madeBoardMove = true;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - SENDING REQUEST TO MAKE MOVE.");
		
		GameMessage msg = new GameMessage(GameMessage.MAKE_MOVE);
		msg.addObject(new Integer(x));
		msg.addObject(new Integer(y));
		
		sendGameMessage(msg, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
	}
	
	/**
	 * ends this players turn
	 */
	protected void endMyTurn() {
		pickingBoardMove = false;
		madeBoardMove = false;
		madeSuggestion = false;
		myTurn = false;
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - ENDING MY TURN.");
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
 }