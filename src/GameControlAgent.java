import java.util.Vector;

import jade.core.*;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

public class GameControlAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private Vector<AID> agents = new Vector<AID>();
	private static String[] character_names = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};

	public void setup() {
		//TODO
	}

	public void startGame(int numPlayers) {
		PlatformController container = getContainerController(); // get a container controller for creating new agents
		
		// create N player agents
		try {
			for (int i = 0;  i < numPlayers;  i++) {
				// create a new agent
				AgentController guest = container.createNewAgent(character_names[i], "examples.party.GuestAgent", null);
				guest.start();

				agents.add( new AID(character_names[i], AID.ISLOCALNAME) );
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
	}
}