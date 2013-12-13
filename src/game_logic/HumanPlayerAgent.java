package game_logic;

import game_ui.UIHumanPlayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import jade.core.AID;
import jade.core.ContainerID;
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
	public static final int SHOW_SUGGESTION = 4;
	public static final int MAKE_SUGGESTION = 5;
	
	protected UIHumanPlayer myGui;

	public void setup() {
		super.setup();
		myGui = new UIHumanPlayer(this, getLocalName());
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
							
						}
					}
					break;
					case GameMessage.TURN_PLAYER: // receiving the name of the current turn's player
					{
						String turnPlayerName = (String) message.getObject(0);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - RECEIVED TURN PLAYER "+turnPlayerName);

						if(turnPlayerName.equals(myAgent.getLocalName())) {
							myTurn = true;
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - MY TURN");
							myGui.setVisible(true);
						}
					}
					break;
					case GameMessage.RSLT_DICE_ROLL: // receiving the result of the dice roll
					{
						if(pickingBoardMove) {
							diceResult = ((Integer) message.getObject(0)).intValue();
							myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - GOT DICE RESULT: "+diceResult);
							myGui.updateDiceResult(diceResult);
							
							ArrayList<Tile> reachablePos = new ArrayList<>();
							Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
							
							if(!currentTile.isRoom()) {
								gameState.board.buildReachableTiles(currentTile.getNeighbours(), reachablePos, diceResult-1);
							} else {
								ArrayList<Tile> room_doors = gameState.board.getRoomDoors(currentTile.getRoom());

								ListIterator<Tile> it = room_doors.listIterator();
								while(true) {
									if(it.hasNext()) {
										Tile door = it.next();
										if(door.isOccupied()) {
											it.remove();
										}
									} else {
										break;
									}
								}

								gameState.board.buildReachableTiles(room_doors, reachablePos, diceResult-1);
							}
							myGui.updateReachablePos(reachablePos);
						}
					}
					break;
					case GameMessage.VALID_MOVE:
					{
						if(madeBoardMove) { // our move has been done
							gameState = (Cluedo.GameState) message.getObject(0);
							posOnBoard = (Coordinates) message.getObject(1);
							myGui.setGameState(gameState);
							myGui.updateReachablePos(null);
							myGui.repaint();
							
							if(gameState.board.getTileAtPosition(posOnBoard).isRoom()) {
								// move the player to that room here?
//								ContainerID cid = new ContainerID(gameState.board.getTileAtPosition(posOnBoard).getRoom(), null);
//								doMove(cid);
							}
						}
					}
					break;
					case GameMessage.INVALID_MOVE:
					{
						madeBoardMove = false;
						pickingBoardMove = true;
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - MY MOVE IS INVALID");
					}
					break;
					case GameMessage.GAME_STATE_UPDATE:
					{
						gameState = (Cluedo.GameState) message.getObject(0);
						myGui.setGameState(gameState);
						myGui.repaint();
					}
					break;
					case GameMessage.PLAYER_MADE_SUGGESTION:
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received suggestion warning");
					}
					break;
					case GameMessage.CONTRADICT_SUGGESTION: 
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - RECEIVED REQUEST TO CONTRADICT SUGGESTION");
						contradictSuggestion(msg);
					}
					break;
					case GameMessage.NO_CONTRADICTION_CARD: // some player is contradicting another player
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received a NO contradiction to suggestion");
						String playerThatContradicted = (String)message.getObject(0); 
						CluedoSuggestion playerSuggestion = (CluedoSuggestion) message.getObject(1);
					}
					break;
					case GameMessage.HAVE_CONTRADICTION_CARD: // some player had a card to contradict another player's suggestion
					{
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received contradiction to suggestion");
						String playerThatContradicted = (String)message.getObject(0); 
						CluedoSuggestion playerSuggestion = (CluedoSuggestion) message.getObject(1);
					}
					break;
					case GameMessage.CONTRADICT_CARD: // a card to contradict our suggestion
					{
						CluedoCard card = (CluedoCard) message.getObject(0);
						myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - received the card "+card.getName()+" to contradict my suggestion from "+msg.getSender().getLocalName());
						if(myTurn) {
							myGui.showCardContradict(card.getName(), msg.getSender().getLocalName());
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
			} else { // if no message is arrived, block the behaviour
				block();
			}
		}
	}
	
	/**
	 * sends a message to the game manager saying if he has a card that can contradict a suggestion and sends a message with that
	 * card to the player that made the suggestion or sends only a message to the game manager saying he doesn't
	 * have a card to contradict that suggestion.
	 * @param msg
	 */
	private void contradictSuggestion(ACLMessage msg) {
		try {
			GameMessage gameMsg = (GameMessage) msg.getContentObject();
			CluedoSuggestion playerSuggestion = (CluedoSuggestion) gameMsg.getObject(0);
			
			// see if I have any card that has been suggested
			for(CluedoCard card: myCards) {
				String cardName = card.getName();
				
				// i have one card to contradict, say yes to gamemanager and send card to the requester
				if(cardName.equals(playerSuggestion.getRoom()) 
						|| cardName.equals(playerSuggestion.getSuspect()) 
						|| cardName.equals(playerSuggestion.getWeapon())) {
					
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - I have at least one card to contradict this suggestion.");
					GameMessage haveContrCard = new GameMessage(GameMessage.HAVE_CONTRADICTION_CARD);
					haveContrCard.addObject(playerSuggestion);
					sendGameMessage(haveContrCard, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);
					
					// send the card to the player that asked it
					myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - sending card "+cardName+" to "+playerSuggestion.getPlayer());
					GameMessage contradictionCard = new GameMessage(GameMessage.CONTRADICT_CARD);
					contradictionCard.addObject(card);
					sendGameMessage(contradictionCard, new AID(playerSuggestion.getPlayer(), AID.ISLOCALNAME), ACLMessage.INFORM);
					return;
				}
			}
			
			// send msg to game manager saying you don't have a card
			myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - I have no card to contradict this suggestion.");
			GameMessage noContrCard = new GameMessage(GameMessage.NO_CONTRADICTION_CARD);
			noContrCard.addObject(playerSuggestion);
			sendGameMessage(noContrCard, new AID("host", AID.ISLOCALNAME), ACLMessage.INFORM);

		} catch (UnreadableException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void postGuiEvent(GuiEvent ge) {
		int command = ge.getType();

		switch (command) {
		case ROLL_DICE:
		{
			// needs to check if he is in a room and all the doors are blocked!
			if(!madeBoardMove && !pickingBoardMove && !madeSuggestion) {
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
			if(madeBoardMove || madeSuggestion) {
				madeBoardMove = false;
				endMyTurn();
				myGui.setVisible(false);
			}
		}
		break;
		case SHOW_SUGGESTION:
		{
			if(!madeSuggestion) {
				Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
				if(currentTile.isRoom()) {
					myGui.showSuggestionPanel(currentTile.getRoom());
				}
			}
		}
		break;
		case MAKE_SUGGESTION:
		{
			if(!madeSuggestion) {
				Tile currentTile = gameState.board.getTileAtPosition(posOnBoard);
				String room = currentTile.getRoom();
				String suspect = (String)ge.getParameter(0);
				String weapon = (String)ge.getParameter(1);
				makeSuggestion(new CluedoSuggestion(room, suspect, weapon, getLocalName()));
			}
		}
		break;
		default:
			// should not get here
			break;
		}
	}
}
