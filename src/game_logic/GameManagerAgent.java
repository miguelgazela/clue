package game_logic;
import java.util.Vector;

import game_ui.CluedoGameGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

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
		int numberPlayers = 0;

		// getting the number of players for this game
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			numberPlayers = Integer.parseInt((String)args[0]);
		}

		// create and show the GUI
		myGui = new CluedoGameGUI(this);
		myGui.show();


		//		AgentContainer container = getContainerController();
		//		
		//		// creating the players and add them to the main container
		//		for(int i = 0; i < numberPlayers; i++) {
		//			try {
		//				AgentController a = container.createNewAgent(Cluedo.suspects[i], "game_logic.Player", null);
		//				a.start();
		//				System.out.println("+++ Created player: " + Cluedo.suspects[i]);
		//			} catch(Exception e) {
		//				// do nothing
		//				e.printStackTrace();
		//			}
		//		}

		System.out.printf("Number of players: %d", numberPlayers);
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
	}

	protected void endGame() {
		// TODO this will be called to end the game
	}
}