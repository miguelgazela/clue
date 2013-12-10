package game_logic;

import jade.util.Logger;

public class RandomBotPlayer extends BotPlayerAgent {

	private static final long serialVersionUID = 3588603829094518352L;
	
	public void setup() {
		super.setup();
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new RandomBotPlayer.");
	}
	
}
