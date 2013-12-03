package game_logic;

import game_ui.UIGame;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.io.IOException;
import java.util.ArrayList;

import aurelienribon.slidinglayout.SLAnimator;

public class GameManagerAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private static final int NUM_CONTAINERS = 10;

	
	private UIGame myGui;
	
	private ArrayList<AID> agents = new ArrayList<AID>();
	private int numPlayers = 0;
	private CluedoLogger logger;
	
	private Cluedo cluedo;
	private GameState gameState;
	
	public void setup() {
		
		// create and show the GUI
		SLAnimator.start();
		myGui = new UIGame(this);
		
		logger = CluedoLogger.getInstance();
		gameState = GameState.Waiting_for_players;
		

		try {
			logger.log("Setting up GameManager");

			// create the agent description of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
			
			// add a Bahaviour to handle pre-game messages
			addBehaviour(new PreGameBehaviour());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * responsive for handling the pre-game messages to and from players
	 * @author migueloliveira
	 *
	 */
	private class PreGameBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = -4883662942187754544L;
		private int playersReady = 0;

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
							logger.log(msg.getSender().getLocalName()+" is ready to start the game.");

							if(playersReady == agents.size()) {
								logger.log("All players are ready, starting game.");
								gameState = GameState.Initiating_game;
								((GameManagerAgent)myAgent).startGame();
							}
						}
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
	
	/**
	 * starts the cluedo game
	 */
	public void startGame() {
		logger.log("Starting the game");
		
		try {
			cluedo = new Cluedo(agents.size());
			
			// send all players their cards
			for(AID agent: agents) {
				GameMessage msg = new GameMessage(GameMessage.DISTRIBUTE_CARDS);
				msg.addObject(cluedo.getPlayerCards(agent.getLocalName()));
				
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
	public void createGame(int numPlayers) {
		this.numPlayers = numPlayers;
		createGameContainers();
		createSuspectsAgents(numPlayers);
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

	private void createSuspectsAgents(int numPlayers) {
		PlatformController container = getContainerController();

		try {
			for (int i = 0;  i < numPlayers;  i++) {
				// create a new agent
				AgentController guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.PlayerAgent", null);
				guest.start();

				agents.add(new AID(Cluedo.suspects[i], AID.ISLOCALNAME));
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
	}
}