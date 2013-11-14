package game_logic;
import game_ui.CluedoGameGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.util.Vector;

public class GameManagerAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private CluedoGameGUI myGui;
	private Vector<AID> agents = new Vector<AID>();
	private static String[] character_names = {
		"Miss Scarlett",
		"Colonel Mustard",
		"Mrs. White",
		"Reverend Green",
		"Mrs. Peacock",
		"Professor Plum"
	};

	public GameManagerAgent() {
		// TODO constructor
	}

	public void setup() {
		// create and show the GUI
		myGui = new CluedoGameGUI(this);

		try {
			System.out.println( getLocalName() + " setting up");
			
			// create the agent descrption of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );

			// add a Behaviour to handle messages from players
			addBehaviour( new CyclicBehaviour( this ) {
				private static final long serialVersionUID = 1L;

				public void action() {
					ACLMessage msg = receive();
					if (msg != null) {
						System.out.println(msg.getContent());
					} else {
						block();
					}
				}
			} );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startGame(int numPlayers) {
		PlatformController container = getContainerController();

		// create N player agents
		try {
			for (int i = 0;  i < numPlayers;  i++) {
				// create a new agent
				AgentController guest = container.createNewAgent(character_names[i], "game_logic.BotPlayerAgent", null);
				guest.start();

				agents.add( new AID(character_names[i], AID.ISLOCALNAME) );
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
		System.out.println("Starting the game");
	}

	protected void endGame() {
		// TODO this will be called to end the game
	}
}