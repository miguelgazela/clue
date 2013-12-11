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

import javax.swing.event.HyperlinkEvent;

import aurelienribon.slidinglayout.SLAnimator;

public class GameManagerAgent extends GuiAgent {

	private static final long serialVersionUID = 5548183532204390248L;
	private static final int NUM_CONTAINERS = 10;

	public static final int CREATE_GAME = 1;
	public static final int HUMAN = 2;
	public static final int BOT = 3;
	
	private UIGame myGui;
	
	private ArrayList<AID> agents = new ArrayList<AID>();
	private HashMap<String, Integer> agentType = new HashMap<>();
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
					case GameMessage.MAKE_MOVE:
					{
						if(gameState == GameState.Waiting_for_play) {
							addBehaviour(new HandleMakeMoveRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.MAKE_SUGGESTION:
					{
						if(gameState == GameState.Waiting_for_play) {
							addBehaviour(new HandleMakeSuggestionRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.END_TURN:
					{
						// check if it's this player's turn
						if(msg.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
							myAgent.addBehaviour(new UpdateGameStateOfAllAgents());
							cluedo.updateTurnPlayer();
							notifyTurnPlayer();
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
	
	private class HandleMakeSuggestionRequest extends OneShotBehaviour {
		private static final long serialVersionUID = -6137878476840405835L;
		ACLMessage request;

		public HandleMakeSuggestionRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
				
			}
		}
	}
	
	private class HandleMakeMoveRequest extends OneShotBehaviour {

		private static final long serialVersionUID = -750399125759169861L;
		ACLMessage request;
		
		public HandleMakeMoveRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {

				GameMessage message;
				try {
					message = (GameMessage) request.getContentObject();
					int x = ((Integer)message.getObject(0)).intValue();
					int y = ((Integer)message.getObject(1)).intValue();
					
					GameMessage msg = null;
					
					Coordinates move = cluedo.makeMove(new Coordinates(x, y));
					
					if(move != null) {
						msg = new GameMessage(GameMessage.VALID_MOVE);
						msg.addObject(cluedo.getGameState());
						msg.addObject(move);
					} else {
						msg = new GameMessage(GameMessage.INVALID_MOVE);
						// TODO maybe add the reason to why the move is invalid?
					}
					
					sendGameMessage(msg, request.getSender(), ACLMessage.INFORM);
					
				} catch (UnreadableException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
	
	private class UpdateGameStateOfAllAgents extends OneShotBehaviour {

		private static final long serialVersionUID = 5056291833901886321L;

		@Override
		public void action() {
			GameMessage update = new GameMessage(GameMessage.GAME_STATE_UPDATE);
			update.addObject(cluedo.getGameState());
			
			for(AID agent: agents) {
				sendGameMessage(update, agent, ACLMessage.INFORM);
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
				diceResult.addObject(new Integer(cluedo.rollDices()));
				sendGameMessage(diceResult, request.getSender(), ACLMessage.INFORM);
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
			
			// send all players their cards and initial game state
			for(AID agent: agents) {
				GameMessage msg = new GameMessage(GameMessage.DISTRIBUTE_CARDS);
				msg.addObject(cluedo.getPlayerCards(agent.getLocalName()));
				msg.addObject(cluedo.getGameState());
				msg.addObject(cluedo.getBoard().getPlayerStartingPos(agent.getLocalName()));
				sendGameMessage(msg, agent, ACLMessage.INFORM);
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
	 * @throws Exception 
	 */
	public void createGame(int[] typePlayers) throws Exception {
		for(int type: typePlayers) {
			if(type != 0) {
				numPlayers++;
			}
		}
		createGameContainers();
		createSuspectsAgents(typePlayers);
	}
	
	public Cluedo getCluedo() {
		return cluedo;
	}
	
	/**
	 * sends a message to all agents with the name of the turn player
	 */
	private void notifyTurnPlayer() {
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Notifying players about this turn's player");
		String currentTurnPlayer = cluedo.getTurnPlayerName();
		
		for(int i = 0; i < agents.size(); i++) {
			GameMessage msg = new GameMessage(GameMessage.TURN_PLAYER);
			msg.addObject(currentTurnPlayer);
			sendGameMessage(msg, agents.get(i), ACLMessage.INFORM);
		}
		
		// update gui TODO should not be here
		int type = agentType.get(currentTurnPlayer).intValue();
		myGui.humanPlayer = (type == HUMAN);
		myGui.turnPlayer = currentTurnPlayer;
		myGui.playerIndex = cluedo.getTurnPlayerIndex();
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
	private void createSuspectsAgents(int[] typePlayers) {
		PlatformController container = getContainerController();
		
		int HUMAN = 1, ROOKIE = 2, DETECTIVE = 3, INSPECTOR = 4;
		
		try {
			// create human players
			// create bot players
			for(int i = 0; i < typePlayers.length; i++) {
				AgentController guest = null;
				
				if(typePlayers[i] == HUMAN) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.HumanPlayerAgent", null);
					agentType.put(Cluedo.suspects[i], new Integer(HUMAN));
				} else if(typePlayers[i] == ROOKIE) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.RandomBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
				} else if(typePlayers[i] == DETECTIVE) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.NormalBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
				} else if(typePlayers[i] == INSPECTOR) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.SmartBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
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
			int[] typePlayers = (int[])ev.getParameter(0);
			
			try {
				createGame(typePlayers);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		break;

		default:
			// should not get here
			break;
		}
	}
}