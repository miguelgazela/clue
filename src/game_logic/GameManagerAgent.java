package game_logic;

import game_ui.UIGame;
import jade.Boot;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import aurelienribon.slidinglayout.SLAnimator;

public class GameManagerAgent extends GuiAgent {

	private static final long serialVersionUID = 5548183532204390248L;
	private static final int NUM_CONTAINERS = 10;

	public static final int CREATE_GAME = 1;
	public static final int HUMAN = 2;
	public static final int BOT = 3;
	public static final int RESET_GAME = 4;
	
	private UIGame myGui;
	
	private ArrayList<AID> agents = new ArrayList<AID>();
	private HashMap<String, Integer> agentType = new HashMap<>();
	private int numPlayers = 0;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	private Cluedo cluedo;
	private GameState gameState;
	public boolean gameIsOver = false;
	
	// for statistics purposes
	private String[] winners = null;
	private ArrayList<String> suggestionsMade = new ArrayList<>();
	private ArrayList<Integer> numberTurnsList = new ArrayList<>();
	private ArrayList<Integer> numberSuggestionsList = new ArrayList<>();
	private ArrayList<Integer> numberUniqueSuggestions = new ArrayList<>(); 
	
	private int numberTurns = 0;
	private int numberSuggestions = 0;
	private int numberOfGamesToMake = 0;
	private final int limitTurns = 3500;
	
	public static void main(String args[]) throws StaleProxyException {
		Boot.main(new String[]{"-gui"});
		 
        Profile perfil = new ProfileImpl(); 
        perfil.setParameter(Profile.CONTAINER_NAME, "Container");
 
        Runtime run = Runtime.instance();
        ContainerController control = run.createAgentContainer(perfil);
 
        GameManagerAgent manager = new GameManagerAgent();
        AgentController controlAgent1 = control.acceptNewAgent("host", manager);
 
        controlAgent1.start();
	}
	
	public void setup() {
		
		// create and show the GUI
		SLAnimator.start();
		myGui = new UIGame(this);

		myLogger.setLevel(Logger.INFO);
		numberOfGamesToMake = 1000;
//		winners = new String[numberOfGamesToMake];
		
		gameState = GameState.Waiting_for_players;

		try {
			// create the agent description of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
			
			// add a Bahaviour to handle pre-game messages
			addBehaviour(new GameBehaviour());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void sendGameMessage(GameMessage gameMsg, AID receiver, int performative) {
		ACLMessage msg = new ACLMessage(performative);
		
		try {
			msg.setContentObject(gameMsg);
			msg.addReceiver(receiver);
			send(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private class GameBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = 6164848963172508841L;
		
		private int playersReady = 0;
		private int playersThatReceivedCards = 0;

		@Override
		public void action() {
			ACLMessage msg = receive();
			
			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();
					
					switch (message.getType()) {
					
					case GameMessage.READY_PLAY:
					{
						if(gameState == GameState.Waiting_for_players) { // READY_PLAY msgs received after game begins are ignored
							playersReady++;

							if(playersReady == numPlayers) {
								myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - All players are ready, starting game.");
								gameState = GameState.Distribution_of_cards;
								playersReady = 0;
								((GameManagerAgent)myAgent).startGame();
							}
						}
					}
					break;
					case GameMessage.ACK_DISTRIBUTE_CARDS:
					{
						if(gameState == GameState.Distribution_of_cards) { // waiting for ack from all players
							playersThatReceivedCards++;

							if(playersThatReceivedCards == numPlayers) {
								myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - All players received their cards.");
								gameState = GameState.Waiting_for_play;
								playersThatReceivedCards = 0;
								((GameManagerAgent)myAgent).notifyTurnPlayer();
							}
						}
					}
					break;
					case GameMessage.ACK_RESET:
					{
						playersReady++;
						
						if(playersReady == numPlayers) {
							gameState = GameState.Distribution_of_cards;
							playersReady = 0;
							((GameManagerAgent)myAgent).startGame();
						}
					}
					break;
					case GameMessage.ASK_DICE_ROLL:
					{
						if(gameState == GameState.Waiting_for_play) { // waiting for a player to do something
							myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVED ASK DICE ROLL REQUEST");
							addBehaviour(new HandleDiceRollRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.MAKE_MOVE:
					{
						if(gameState == GameState.Waiting_for_play) {
							myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVED MAKE MOVE REQUEST");
							addBehaviour(new HandleMakeMoveRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.MAKE_SUGGESTION:
					{
						if(gameState == GameState.Waiting_for_play) {
							myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVED MAKE SUGGESTION");
							addBehaviour(new HandleMakeSuggestionRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.MAKE_ACCUSATION:
					{
						if(gameState == GameState.Waiting_for_play) {
							addBehaviour(new HandleMakeAccusationRequest(myAgent, msg));
						}
					}
					break;
					case GameMessage.NO_CONTRADICTION_CARD:
					case GameMessage.HAVE_CONTRADICTION_CARD:
					{
						if(gameState == GameState.Waiting_for_play) {
							myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVED SUGGESTION CONTRADICTION");
							addBehaviour(new HandleSuggestionContradiction(myAgent, msg));
						}
					}
					break;
					case GameMessage.END_TURN:
					{
						// check if it's this player's turn
						if(msg.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
							myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVED END TURN REQUEST");
							myAgent.addBehaviour(new UpdateGameStateOfAllAgents());
							cluedo.updateTurnPlayer();
							numberTurns++;
							myGui.repaint();
							
							if(numberTurns > limitTurns) {
								myLogger.log(Logger.WARNING, "Agent "+getLocalName()+" - IT'S OVER "+limitTurns+" turns!!!");
								myLogger.log(Logger.WARNING, "GAME_MANAGER - #UNIQUE SUGGESTIONS: "+suggestionsMade.size());
								resetGame();
							} else {
								notifyTurnPlayer();
							}
						}
					}
					break;
					default:
					{
						// should not get here!!!
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - unrecognized message.");
					}
						break;
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			} else {
				block();
			}
		}	
	}
	
	private class HandleSuggestionContradiction extends OneShotBehaviour {

		private static final long serialVersionUID = -7212843736966184461L;
		ACLMessage request;

		public HandleSuggestionContradiction(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
			if(request.getSender().getLocalName().equals(cluedo.getSuggestionContradictorName())) {
				try {
					GameMessage gameMsg = (GameMessage)request.getContentObject();
					GameMessage update = null;
					
					if(gameMsg.getType().equals(GameMessage.NO_CONTRADICTION_CARD)) { // the player didn't have a card of the suggestion
						update = new GameMessage(GameMessage.NO_CONTRADICTION_CARD);
					} else if(gameMsg.getType().equals(GameMessage.HAVE_CONTRADICTION_CARD)){
						update = new GameMessage(GameMessage.HAVE_CONTRADICTION_CARD);
					}
					
					update.addObject(request.getSender().getLocalName());
					update.addObject(gameMsg.getObject(0)); // the CluedoSuggestion
					
					for(AID agent: agents) {
						if(!agent.getLocalName().equals(request.getSender().getLocalName())) {
							sendGameMessage(update, agent, ACLMessage.INFORM);
						}
					}
					
					if(gameMsg.getType().equals(GameMessage.NO_CONTRADICTION_CARD)) {
						// send message to the next player to the left
						cluedo.updateSuggestionContradictor();
						
						if(cluedo.getSuggestionContradictorName().equals(cluedo.getTurnPlayerName())) { // no one had a card to contradict, the player made a suggestion with his cards
							cluedo.updateTurnPlayer();
							notifyTurnPlayer();
							return;
						}
						
						GameMessage requestContradiction = new GameMessage(GameMessage.CONTRADICT_SUGGESTION);
						requestContradiction.addObject(gameMsg.getObject(0));
						sendGameMessage(requestContradiction, new AID(cluedo.getSuggestionContradictorName(), AID.ISLOCALNAME), ACLMessage.INFORM);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
	}
	
	private class HandleMakeAccusationRequest extends OneShotBehaviour {

		private static final long serialVersionUID = 5567671553350648269L;

		ACLMessage request;
		
		public HandleMakeAccusationRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
			myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVING ACCUSATION");
		}
		
		@Override
		public void action() {
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
				try {
					GameMessage message = (GameMessage) request.getContentObject();
					CluedoSuggestion playerAccusation = (CluedoSuggestion) message.getObject(0);
					
					if(cluedo.isGameSolution(playerAccusation.getRoom(), playerAccusation.getSuspect(), playerAccusation.getWeapon())) {
						myLogger.log(Logger.WARNING, "GAME_MANAGER - WINNER: "+playerAccusation.getPlayer());
						myLogger.log(Logger.WARNING, "GAME_MANAGER - SOLUTION WAS: "+cluedo.getGameSolution());
						gameOver();
					} else {
						// the player looses the game
						GameMessage accusationWarning = new GameMessage(GameMessage.PLAYER_MADE_ACCUSATION);
						accusationWarning.addObject(playerAccusation);
						
						for(AID agent: agents) {
							if(!agent.getLocalName().equals(request.getSender().getLocalName())) {
								sendGameMessage(accusationWarning, agent, ACLMessage.INFORM);
							}
						}
						
						// warns the agent that he has lost
						cluedo.playerHasLost(request.getSender().getLocalName());
						GameMessage wrongAccusation = new GameMessage(GameMessage.WRONG_ACCUSATION);
						wrongAccusation.addObject(cluedo.getGameSolution());
						sendGameMessage(wrongAccusation, request.getSender(), ACLMessage.INFORM);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
	}
	
	private class HandleMakeSuggestionRequest extends OneShotBehaviour {
		private static final long serialVersionUID = -6137878476840405835L;
		ACLMessage request;

		public HandleMakeSuggestionRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
			myLogger.log(Logger.INFO, "GAME_MANAGER - RECEIVING SUGGESTION");
		}
		
		@Override
		public void action() {
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
				try {
					
					GameMessage message = (GameMessage) request.getContentObject();
					CluedoSuggestion playerSuggestion = (CluedoSuggestion) message.getObject(0);
					numberSuggestions++;
					
					String sgst = playerSuggestion.getSuspect()+"-"+playerSuggestion.getWeapon()+"-"+playerSuggestion.getRoom();
					if(!suggestionsMade.contains(sgst)) {
						suggestionsMade.add(sgst);
					}
					if(cluedo.isGameSolution(playerSuggestion.getRoom(), playerSuggestion.getSuspect(), playerSuggestion.getWeapon())) {
						myLogger.log(Logger.WARNING, "GAME_MANAGER - WINNER: "+playerSuggestion.getPlayer());
						myLogger.log(Logger.WARNING, "GAME_MANAGER - SUGGESTION: "+sgst);
						myLogger.log(Logger.WARNING, "GAME_MANAGER - SOLUTION WAS: "+cluedo.getGameSolution());
						gameOver();
					} else { // somebody must have a card to contradict this suggestion
						
						// warn other agents about the suggestion
						GameMessage suggestionWarning = new GameMessage(GameMessage.PLAYER_MADE_SUGGESTION);
						suggestionWarning.addObject(playerSuggestion);
						
						for(AID agent: agents) {
							if(!agent.getLocalName().equals(request.getSender().getLocalName())) {
								sendGameMessage(suggestionWarning, agent, ACLMessage.INFORM);
							}
						}
						
						// send request to the next player to the left
						GameMessage requestContradiction = new GameMessage(GameMessage.CONTRADICT_SUGGESTION);
						requestContradiction.addObject(playerSuggestion);
						sendGameMessage(requestContradiction, new AID(cluedo.getSuggestionContradictorName(), AID.ISLOCALNAME), ACLMessage.INFORM);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
	
	private class HandleMakeMoveRequest extends OneShotBehaviour {

		private static final long serialVersionUID = -750399125759169861L;
		ACLMessage request;
		
		public HandleMakeMoveRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {

				GameMessage message;
				try {
					message = (GameMessage) request.getContentObject();
					int x = ((Integer)message.getObject(0)).intValue();
					int y = ((Integer)message.getObject(1)).intValue();
					
					GameMessage msg = null;
					Coordinates move = cluedo.makeMove(new Coordinates(x, y));
					
					if(move != null) {
						msg = new GameMessage(GameMessage.VALID_MOVE);
						msg.addObject(cluedo.getGameState());
						msg.addObject(move);
					} else {
						msg = new GameMessage(GameMessage.INVALID_MOVE);
						// TODO maybe add the reason to why the move is invalid?
					}
					
					sendGameMessage(msg, request.getSender(), ACLMessage.INFORM);
					myGui.repaint();
					
				} catch (UnreadableException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
	
	private class UpdateGameStateOfAllAgents extends OneShotBehaviour {

		private static final long serialVersionUID = 5056291833901886321L;

		@Override
		public void action() {
			GameMessage update = new GameMessage(GameMessage.GAME_STATE_UPDATE);
			update.addObject(cluedo.getGameState());
			
			for(AID agent: agents) {
				sendGameMessage(update, agent, ACLMessage.INFORM);
			}
		}
	}
	
	private class HandleDiceRollRequest extends OneShotBehaviour {
		private static final long serialVersionUID = -5340646074128914622L;
		ACLMessage request;
		
		public HandleDiceRollRequest(Agent a, ACLMessage request) {
			super(a);
			this.request = request;
		}
		
		@Override
		public void action() {
//			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - RECEIVED REQUEST FOR DICE ROLL FROM " + request.getSender().getLocalName());
			
			// check if it's this player's turn
			if(request.getSender().getLocalName().equals(cluedo.getTurnPlayerName())) {
				
				// send dice result
				GameMessage diceResult = new GameMessage(GameMessage.RSLT_DICE_ROLL);
				diceResult.addObject(new Integer(cluedo.rollDices()));
				sendGameMessage(diceResult, request.getSender(), ACLMessage.INFORM);
			}
		}
	}
	
	/**
	 * starts the cluedo game
	 */
	public void startGame() {
		numberOfGamesToMake--;
		gameIsOver = false;
		try {
			cluedo = new Cluedo(agents.size());
			
			// send all players their cards and initial game state
			for(AID agent: agents) {
				GameMessage msg = new GameMessage(GameMessage.DISTRIBUTE_CARDS);
				msg.addObject(cluedo.getPlayerCards(agent.getLocalName()));
				msg.addObject(cluedo.getGameState());
				msg.addObject(cluedo.getBoard().getPlayerStartingPos(agent.getLocalName()));
				sendGameMessage(msg, agent, ACLMessage.INFORM);
			}
			myGui.hasGameRunning = true;
			myGui.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void gameOver() {
		myLogger.log(Logger.WARNING, "GAME_MANAGER - GAMEOVER");
		gameIsOver = true;
		myGui.repaint();

		myLogger.log(Logger.WARNING, "GAME_MANAGER - #TURNS: "+numberTurns);
		myLogger.log(Logger.WARNING, "GAME_MANAGER - #SUGGESTIONS: "+numberSuggestions);
		myLogger.log(Logger.WARNING, "GAME_MANAGER - #UNIQUE SUGGESTIONS: "+suggestionsMade.size());
		
		numberTurnsList.add(new Integer(numberTurns));
		numberSuggestionsList.add(new Integer(numberSuggestions));
		numberUniqueSuggestions.add(new Integer(suggestionsMade.size()));
		
//		winners[2 - (numberOfGamesToMake + 1)] = cluedo.getTurnPlayerName();
		
		if(numberOfGamesToMake > 0) { // reset and start another game
			resetGame();
		} else {
			// send all players that the game is over
			for(AID agent: agents) {
				GameMessage msg = new GameMessage(GameMessage.GAME_OVER);
				msg.addObject(cluedo.getTurnPlayerName());
				msg.addObject(cluedo.getGameSolution());
				sendGameMessage(msg, agent, ACLMessage.INFORM);
			}
			int avgNumberTurns = 0;
			int avgNumberSuggestions = 0;
			int avgNumberUniqueSugestions = 0;
			int gameSize = numberTurnsList.size();
			
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("gameStats.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			
			for(int i = 0; i < gameSize; i++) {
				writer.println(numberTurnsList.get(i) + " " + numberSuggestionsList.get(i) + " " + numberUniqueSuggestions.get(i));
//				writer.println(numberTurnsList.get(i) + " " + numberSuggestionsList.get(i) + " " + numberUniqueSuggestions.get(i) + " " + winners[i]);
				avgNumberTurns += numberTurnsList.get(i);
				avgNumberSuggestions += numberSuggestionsList.get(i);
				avgNumberUniqueSugestions += numberUniqueSuggestions.get(i);
				
			}
			
			// write number of wins
			
			writer.close();
			System.out.println("Game Over");
			myLogger.log(Logger.WARNING, "GAME_MANAGER - #AVERAGE TURNS: "+avgNumberTurns/gameSize);
			myLogger.log(Logger.WARNING, "GAME_MANAGER - #AVERAGE SUGGESTIONS: "+avgNumberSuggestions/gameSize);
			myLogger.log(Logger.WARNING, "GAME_MANAGER - #AVERAGE UNIQUE SUGESTIONS: "+avgNumberUniqueSugestions/gameSize);
		}
	}
	
	private void resetGame() {
		numberTurns = 0;
		numberSuggestions = 0;
		suggestionsMade.clear();
		gameIsOver = false;
		
		// send reset msg to all agents
		for(AID agent: agents) {
			GameMessage msg = new GameMessage(GameMessage.RESET);
			sendGameMessage(msg, agent, ACLMessage.INFORM);
		}
	}

	/**
	 * initiates the agents and containers needed to play the game
	 * @param numPlayers
	 * @throws Exception 
	 */
	public void createGame(int[] typePlayers) throws Exception {
		for(int type: typePlayers) {
			if(type != 0) {
				numPlayers++;
			}
		}
		createGameContainers();
		createSuspectsAgents(typePlayers);
	}
	
	public Cluedo getCluedo() {
		return cluedo;
	}
	
	/**
	 * sends a message to all agents with the name of the turn player
	 */
	private void notifyTurnPlayer() {
//		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - NOTIFY PLAYERS ABOUT TURN PLAYER");
		String currentTurnPlayer = cluedo.getTurnPlayerName();
		
		for(int i = 0; i < agents.size(); i++) {
			GameMessage msg = new GameMessage(GameMessage.TURN_PLAYER);
			msg.addObject(currentTurnPlayer);
			sendGameMessage(msg, agents.get(i), ACLMessage.INFORM);
		}
		
		int type = agentType.get(currentTurnPlayer).intValue();
		myGui.humanPlayer = (type == HUMAN);
		myGui.turnPlayer = currentTurnPlayer;
		myGui.playerIndex = cluedo.getTurnPlayerIndex();
	}

	/**
	 * creates the containers needed to the game
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
				rt.createAgentContainer(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * creates all the agents that will be in the game
	 * @param numPlayers
	 */
	private void createSuspectsAgents(int[] typePlayers) {
		PlatformController container = getContainerController();
		
		int HUMAN = 1, ROOKIE = 2, DETECTIVE = 3, INSPECTOR = 4;
		
		try {
			// create human players
			// create bot players
			for(int i = 0; i < typePlayers.length; i++) {
				AgentController guest = null;
				
				if(typePlayers[i] == HUMAN) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.HumanPlayerAgent", null);
					agentType.put(Cluedo.suspects[i], new Integer(HUMAN));
				} else if(typePlayers[i] == ROOKIE) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.RandomBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
				} else if(typePlayers[i] == DETECTIVE) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.NormalBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
				} else if(typePlayers[i] == INSPECTOR) {
					guest = container.createNewAgent(Cluedo.suspects[i], "game_logic.SmartBotPlayer", null);
					agentType.put(Cluedo.suspects[i], new Integer(BOT));
				}
				
				if(guest != null) {
					guest.start();
					AID aid = new AID(Cluedo.suspects[i], AID.ISLOCALNAME);
					agents.add(aid);
				}
			}
		}
		catch (Exception e) {
			System.err.println( "Exception while adding guests: " + e );
			e.printStackTrace();
		}
	}

	/**
	 * processes events given by the GUI
	 */
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		
		int command = ev.getType();
		
		switch (command) {
		case CREATE_GAME:
		{
			int[] typePlayers = (int[])ev.getParameter(0);
			
			try {
				createGame(typePlayers);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		break;
		case RESET_GAME:
		{
			resetGame();
		}
		break;
		default:
			// should not get here
			break;
		}
	}
}