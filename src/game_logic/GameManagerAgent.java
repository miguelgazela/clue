package game_logic;

import game_ui.CluedoGameGUI;
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
import jade.wrapper.*;

import java.util.Vector;

public class GameManagerAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private static final int NUM_CONTAINERS = 10;

	
	private CluedoGameGUI myGui;
	private Vector<AID> agents = new Vector<AID>();
	private int playersReady = 0;
	private int numPlayers = 0;
	private Cluedo cluedo;
	
	public String READY = "READY";

	public void setup() {
		// create and show the GUI
		myGui = new CluedoGameGUI(this);

		try {
			System.out.println( getLocalName() + " setting up");

			// create the agent descrption of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
			
			// add a Behaviour to handle messages from guests
			addBehaviour( new CyclicBehaviour(this) {
				private static final long serialVersionUID = 1347573303070882850L;

				public void action() {
					ACLMessage msg = receive();

					if (msg != null) {

						Message message;
						try {
							message = (Message) msg.getContentObject();
							
							switch (message.getType()) {
							case "READY":
								System.out.println(msg.getSender() + " sended Ready message!");
								break;
							default:
								break;
							}
							
							if (playersReady == agents.size()) {
								System.out.println( "All players are ready, starting game" );
								startGame();
							}
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
//						
//						
//						try {
//							System.out.println((Cluedo) msg.getContentObject());
//						} catch (UnreadableException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						
//						
//						if (READY.equals( msg.getContent() )) {
//							// a player is ready to go
//							playersReady++;
//							System.out.println(msg.getSender().getLocalName()+" is ready");
//							
//							try {
//								String c = (String) msg.getContentObject();
//								System.out.println(c.toString());
//							} catch (UnreadableException e) {
//								e.printStackTrace();
//							}
//							
////							setPartyState( "Inviting guests (" + m_guestCount + " have arrived)" );
////
//							if (playersReady == agents.size()) {
//								System.out.println( "All players are ready, starting game" );
//								startGame();
//							}
//						}
					}
					else { // if no message is arrived, block the behaviour
						block();
					}
				}
			} );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void startGame() {
		System.out.println("Starting the game");
		cluedo = new Cluedo(agents.size());
	}

	/**
	 * 
	 * @param numPlayers
	 */
	public void createGame(int numPlayers) {
		this.numPlayers = numPlayers;
		createGameContainers();
		createSuspectsAgents(numPlayers);
	}

	/**
	 * 
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
				AgentContainer ac = rt.createAgentContainer(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void createSuspectsAgents(int numPlayers) {
		PlatformController container = getContainerController();

		// create N player agents
		try {
			for (int i = 0;  i < numPlayers;  i++) {
				// create a new agent
				AgentController guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.BotPlayerAgent", null);
				guest.start();

				agents.add(new AID(Cluedo.suspects[i], AID.ISLOCALNAME));
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
	}

	protected void endGame() {
		// TODO this will be called to end the game
	}
}