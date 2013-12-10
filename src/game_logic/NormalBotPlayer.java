package game_logic;

import jade.util.Logger;

public class NormalBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 2702856073265411877L;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new NormalBotPlayer.");
	}

}
