package game_logic;

import game_ui.UIHumanPlayer;

import java.awt.Color;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

public class HumanPlayerAgent extends PlayerAgent {

	private static final long serialVersionUID = -314248632846121693L;
	
	public static final int ROLL_DICE = 1;
	public static final int MAKE_MOVE = 2;
	public static final int END_TURN = 3;
	
	protected UIHumanPlayer myGui;

	public void setup() {
		super.setup();
		myGui = new UIHumanPlayer(this, getLocalName());
		myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - new human player.");
		addBehaviour(new HumanPlayerBehaviour());
	}
	
	private class HumanPlayerBehaviour extends CyclicBehaviour {
		private static final long serialVersionUID = -3630440372660298200L;

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive();

			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					System.out.println("HUMANPLAYER: "+message.getType());

					switch (message.getType()) {
					case GameMessage.DISTRIBUTE_CARDS: // receiving this players cards and initial game state
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
							gameState = (Cluedo.GameState) message.getObject(1);
							posOnBoard = (Coordinates) message.getObject(2);
							
							// send ack
							GameMessage msg_ack = new GameMessage(GameMessage.ACK_DISTRIBUTE_CARDS);
							sendGameMessage(msg_ack, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
							
							myGui.setGameState(gameState);
							myGui.updatePlayerCards(myCards);
							myGui.repaint();
							
						} else {
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Receiving cards and initial position again.");
						}
					}
					break;
					case GameMessage.TURN_PLAYER: // receiving the name of the current turn's player
					{
						String turnPlayerName = (String) message.getObject(0);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received turn player name "+turnPlayerName);

						if(turnPlayerName.equals(myAgent.getLocalName())) {
							myTurn = true;
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - it's my turn!");
							myGui.setVisible(true);
						}
					}
					break;
					case GameMessage.RSLT_DICE_ROLL: // receiving the result of the dice roll
					{
						if(waitingForDiceResult) {
							diceResult = ((Integer) message.getObject(0)).intValue();
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - rolled the dice and got "+diceResult);
							waitingForDiceResult = false;
							pickingBoardMove = true;
							myGui.updateDiceResult(diceResult);
							ArrayList<Tile> reachablePos = new ArrayList<>();
							buildReachableTiles(gameState.board.getTiles().get(posOnBoard.getY()).get(posOnBoard.getX()).getNeighbours(), reachablePos, diceResult-1);
							myGui.updateReachablePos(reachablePos);
						}
					}
					break;
					case GameMessage.VALID_MOVE:
					{
						if(waitingForMoveConfirmation) {
							// our move has been done
							gameState = (Cluedo.GameState) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);
							myGui.setGameState(gameState);
							myGui.updateReachablePos(null);
							myGui.repaint();
							waitingForMoveConfirmation = false;
							madeBoardMove = true;
						}
					}
					break;
					case GameMessage.INVALID_MOVE:
					{
						if(waitingForMoveConfirmation) {
							waitingForMoveConfirmation = false;
							pickingBoardMove = true;
						}
					}
					break;
					case GameMessage.GAME_STATE_UPDATE:
					{
						gameState = (Cluedo.GameState) message.getObject(0);
						myGui.setGameState(gameState);
						myGui.repaint();
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
			} else { // if no message is arrived, block the behaviour
				block();
			}
		}
	}

	@Override
	protected void makePlay() {
		System.out.println("MakePlay do HumanPlayer");
	}

	public void postGuiEvent(GuiEvent ge) {
		int command = ge.getType();

		switch (command) {
		case ROLL_DICE:
		{
			if(!waitingForDiceResult && !pickingBoardMove && !madeBoardMove) {
				askDiceRoll();
			}
		}
		break;
		case MAKE_MOVE:
		{
			if(pickingBoardMove) {
				int x = ((Integer)ge.getParameter(0)).intValue();
				int y = ((Integer)ge.getParameter(1)).intValue();
				makeMove(x, y);
			}
		}
		break;
		case END_TURN:
		{
			if(madeBoardMove) { // TODO needs to check other stuff
				madeBoardMove = false;
				endMyTurn();
				myGui.setVisible(false);
			}
		}
		break;
		default:
			// should not get here
			break;
		}
	}
}
