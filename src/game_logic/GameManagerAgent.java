package game_logic;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class GameManagerAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private static String[] character_names = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};
	
	public void setup() {
		int numberPlayers = 0;
		
		// getting the number of players for this game
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			numberPlayers = Integer.parseInt((String)args[0]);
		}
		
		AgentContainer container = getContainerController();
		
		// creating the players and add them to the main container
		for(int i = 0; i < numberPlayers; i++) {
			try {
				AgentController a = container.createNewAgent(character_names[i], "Player", null);
				a.start();
				System.out.println("+++ Created player: " + character_names[i]);
			} catch(Exception e) {
				// do nothing
				e.printStackTrace();
			}
		}
		
		System.out.printf("Number of players: %d", numberPlayers);
	}
}