import java.util.ArrayList;

import jade.core.*;
import jade.core.behaviours.*;

import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class GameControlAgent extends Agent {

	private static final long serialVersionUID = 5548183532204390248L;

	private ArrayList<AgentController> agents = new ArrayList<AgentController>();
	private static String[] character_names = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Reverend Green", "Mrs. Peacock", "Professor Plum"};
	
	public void setup() {
		int numberPlayers = 0;
		
		Object[] args = getArguments();
		if(args != null && args.length > 0) {
			numberPlayers = Integer.parseInt((String)args[0]);
		}
		
		AgentContainer c = getContainerController();
		
		for(int i = 0; i < numberPlayers; i++) {
			try {
				AgentController a = c.createNewAgent(character_names[i], "Detective", null);
				a.start();
				System.out.println("+++ Created: " + character_names[i]);
				agents.add(a);
			} catch(Exception e) {
				// do nothing
				e.printStackTrace();
			}
		}
		
		System.out.printf("Number of players: %d", numberPlayers);
	}
}