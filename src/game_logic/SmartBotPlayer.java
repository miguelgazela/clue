package game_logic;

import jade.util.Logger;

public class SmartBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 7479738606280331848L;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new SmartBotPlayer.");
	}
	
}
