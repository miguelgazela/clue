package game_logic;

import game_ui.UIGame;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aurelienribon.slidinglayout.SLAnimator;

public class GameManagerAgent extends GuiAgent {

	private static final long serialVersionUID = 5548183532204390248L;
	private static final int NUM_CONTAINERS = 10;

	public static final int CREATE_GAME = 1;
	
	private UIGame myGui;
	
	private ArrayList<AID> agents = new ArrayList<AID>();
	private int numPlayers = 0;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	private Cluedo cluedo;
	private GameState gameState;
	
	public void setup() {
		
		// create and show the GUI
		SLAnimator.start();
		myGui = new UIGame(this);
		
		gameState = GameState.Waiting_for_players;

		try {
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Setting up GameManager");

			// create the agent description of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
			
			// add a Bahaviour to handle pre-game messages
			addBehaviour(new GameBehaviour());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class GameBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 6164848963172508841L;
		
		private int playersReady = 0;
		private int playersThatReceivedCards = 0;

		@Override
		public void action() {
			ACLMessage msg = receive();
			
			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();
					
					switch (message.getType()) {
					
					case GameMessage.READY_PLAY:
					{
						if(gameState == GameState.Waiting_for_players) { // READY_PLAY msgs received after game begins are ignored
							playersReady++;
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - "+msg.getSender().getLocalName()+" is ready to start the game.");

							if(playersReady == numPlayers) {
								myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - All players are ready, starting game.");
								gameState = GameState.Distribution_of_cards;
								((GameManagerAgent)myAgent).startGame();
							}
						}
					}
					break;
					case GameMessage.ACK_DISTRIBUTE_CARDS:
					{
						if(gameState == GameState.Distribution_of_cards) { // waiting for ack from all players
							playersThatReceivedCards++;
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - "+msg.getSender().getLocalName()+" has received his cards.");

							if(playersThatReceivedCards == numPlayers) {
								myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - All players received their cards.");
								gameState = GameState.Waiting_for_play;
								((GameManagerAgent)myAgent).notifyTurnPlayer();
							}
						}
					}
					break;
					case GameMessage.ASK_DICE_ROLL:
					{
						if(gameState == GameState.Waiting_for_play) { // waiting for a player to do something
							addBehaviour(new HandleDiceRollRequest(myAgent, msg));
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
			} else {
				block();
			}
		}	
	}
	
	private class HandleDiceRollRequest extends OneShotBehaviour {
		private static final long serialVersionUID = -5340646074128914622L;
		ACLMessage request;
		
		public HandleDiceRollRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - receiving request for dice roll.");
			
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
				
				// send dice result
				GameMessage diceResult = new GameMessage(GameMessage.RSLT_DICE_ROLL);
				diceResult.addObject(new Integer(cluedo.rollDice()));
				ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);

				try {
					aclmsg.setContentObject(diceResult);
					aclmsg.addReceiver(request.getSender());
					send(aclmsg);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
	
	/**
	 * starts the cluedo game
	 */
	public void startGame() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Starting the game.");
		
		try {
			cluedo = new Cluedo(agents.size());
			
			// send all players their cards and initial pos
			for(AID agent: agents) {
				GameMessage msg = new GameMessage(GameMessage.DISTRIBUTE_CARDS_AND_POS);
				msg.addObject(cluedo.getPlayerCards(agent.getLocalName()));
				msg.addObject(cluedo.getBoard().getPlayerStartingPos(agent.getLocalName()));
				
				// send message with cards to agent
				ACLMessage cards = new ACLMessage(ACLMessage.INFORM);
				try {
					cards.setContentObject(msg);
					cards.addReceiver(agent);
					send(cards);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
			myGui.hasGameRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	protected void endGame() {
		// TODO this will be called to end the game
	}

	/**
	 * initiates the agents and containers needed to play the game
	 * @param numPlayers
	 */
	public void createGame(int numHumanPlayers, int numBotPlayers) {
		this.numPlayers = numHumanPlayers + numBotPlayers;
		createGameContainers();
		createSuspectsAgents(numHumanPlayers, numBotPlayers);
	}
	
	public Cluedo getCluedo() {
		return cluedo;
	}
	
	/**
	 * sends a message to all agents with the name of the turn player
	 */
	private void notifyTurnPlayer() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Notifying players about this turn's player");
		
		for(AID agent: agents) {
			GameMessage msg = new GameMessage(GameMessage.TURN_PLAYER);
//			msg.addObject(cluedo.getTurnPlayerName());
			msg.addObject("Miss Scarlett");

			// send message with turn player name to agent
			ACLMessage turn = new ACLMessage(ACLMessage.INFORM);
			try {
				turn.setContentObject(msg);
				turn.addReceiver(agent);
				send(turn);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	/**
	 * creates the containers needed to the game
	 */
	private void createGameContainers() {

		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		

		// Create a default profile and set to be non-main container
		ProfileImpl p = new ProfileImpl();
		p.setParameter(Profile.MAIN, "false");

		for(int i = 0; i < GameManagerAgent.NUM_CONTAINERS; i++) {
			try {
				p.setParameter(Profile.CONTAINER_NAME, Cluedo.rooms[i]);
				// Create a new non-main container, connecting to the default
				// main container (i.e. on this host, port 1099)
				rt.createAgentContainer(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * creates all the agents that will be in the game
	 * @param numPlayers
	 */
	private void createSuspectsAgents(int numHumPlayers, int numBotPlayers) {
		PlatformController container = getContainerController();

		try {
			// create human players
			// create bot players
			for (int i = 0;  i < (numHumPlayers + numBotPlayers);  i++) {
				AgentController guest = null;
				
				if(i < numHumPlayers) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.HumanPlayerAgent", null);
				} else {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.BotPlayerAgent", null);
				}
				
				guest.start();
				AID aid = new AID(Cluedo.suspects[i], AID.ISLOCALNAME);
				agents.add(aid);
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
	}

	/**
	 * processes events given by the GUI
	 */
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		
		int command = ev.getType();
		
		switch (command) {
		case CREATE_GAME:
		{
			int numHumPlayers = ((Integer)ev.getParameter(0)).intValue();
			int numBotPlayers = ((Integer)ev.getParameter(1)).intValue();
			createGame(numHumPlayers, numBotPlayers);
		}
		break;

		default:
			// should not get here
			break;
		}
	}
}