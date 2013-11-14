package game_logic;
import game_ui.CluedoGameGUI;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class GameManagerAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;
	private CluedoGameGUI myGui;
	
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
		
		System.out.println("Here!");
		
		
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
		// TODO 
		System.out.println("HERE AGAIN");
	}

	protected void endGame() {
		// TODO this will be called to end the game
	}
}