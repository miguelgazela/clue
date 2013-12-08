package game_ui;

import game_logic.Cluedo.GameState;
import game_logic.Board;
import game_logic.CluedoCard;
import game_logic.CluedoNotebook;
import game_logic.Coordinates;
import game_logic.GameManagerAgent;
import game_logic.HumanPlayerAgent;

import jade.gui.GuiEvent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class UIHumanPlayer extends JFrame implements ActionListener {

	private static final long serialVersionUID = -682639614717071016L;
	protected UIPlayerPanel uiPlayerPanel;
	protected HumanPlayerAgent agent;
	protected GameState gameState = null;
	protected CluedoNotebook notebook;

	public UIHumanPlayer(HumanPlayerAgent owner, String playerName) {
		super(playerName+" interface");
		agent = owner;
		notebook = new CluedoNotebook();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiPlayerPanel = new UIPlayerPanel();
		uiPlayerPanel.addMouseListener(uiPlayerPanel);
		getContentPane().add(uiPlayerPanel);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
//		setVisible(true);
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	public void updatePlayerCards(ArrayList<CluedoCard> cards) {
		notebook.addPlayerCards(cards);
	}
	
	private class UIPlayerPanel extends JPanel implements MouseListener {

		private static final long serialVersionUID = 1035949055614632990L;
		transient private BufferedImage background;
		
		public UIPlayerPanel() {
			background = UIResourcesLoader.getInstanceLoader().player_interface;
		}
		
		private void writeObject(ObjectOutputStream out) throws IOException {
	        out.defaultWriteObject();
	        ImageIO.write(background, "png", out);
	    }

	    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	        in.defaultReadObject();
	        background = ImageIO.read(in);
	    }
		
		@Override
		public Dimension getPreferredSize() {
			if (background != null) {
				int width = background.getWidth();
				int height = background.getHeight();
				return new Dimension(width, height);
			}
			return super.getPreferredSize();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g); // clear off-screen bitmap
			if (background != null) {
				g.drawImage(background, 0, 0, this);
			}
			
			if(gameState != null) {
				
				for(int i = 0; i < gameState.players.size(); i++) {
					UICoord c = UIResourcesLoader.getInstanceLoader().board_source_coord_player_interface;
					Coordinates pos = gameState.players.get(i).getPosOnBoard();
					
					g.drawImage(
							UIResourcesLoader.getInstanceLoader().getPlayerToken(i), 
							c.x + pos.getX()*UIResourcesLoader.BOARD_POS_DIF, 
							c.y + pos.getY()*UIResourcesLoader.BOARD_POS_DIF,
							this
					);
					
					// draw dashboard
					if(gameState.players.get(i).getName().equals(agent.getLocalName())) {
						c = UIResourcesLoader.getInstanceLoader().player_dashboard;
						g.drawImage(
								UIResourcesLoader.getInstanceLoader().getPlayerDashBoard(i), 
								c.x, 
								c.y,
								this
						);
					}
					
					// draw notebook cards state
					for (Map.Entry entry : notebook.getCardsState().entrySet()) { 
						c = UIResourcesLoader.getInstanceLoader().getNotebookCardStateCoord((String)entry.getKey());
						
						if(c != null) {

							if((int)entry.getValue() != -1) {
								g.drawImage(
										UIResourcesLoader.getInstanceLoader().getCardNoteStatus((int)entry.getValue()),
										c.x, 
										c.y,
										this
										);
							}
						}
					}
			}
				
			}
		}
		
		private void makeMove(int y, int x) {
			GuiEvent ge = new GuiEvent(this, HumanPlayerAgent.MAKE_MOVE);
			ge.addParameter(new Integer(x));
			ge.addParameter(new Integer(y));
			agent.postGuiEvent(ge);
		}
	
		private void rollDice() {
			GuiEvent ge = new GuiEvent(this, HumanPlayerAgent.ROLL_DICE);
			agent.postGuiEvent(ge);
		}
		
		private void makeSuggestion() {
			
		}
		
		private void endTurn() {
			
		}
		
		private void makeAccusation() {
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
						
			if(x >= 14 && y >= 12 && x <= (12 + 24*23) && y <= (14 + 25*23)) { //board click
				System.out.println("Board click");
				
				int i = (y - 12) / 23;
				int j = (x - 14) / 23;
				makeMove(i, j);
				
			} else if(x >=574 && y >= 9 && x <= 923 && y <= 587) { // dashboard click
				System.out.println("Dashboard click");
				
				if(x >= 614 && y >= 79 && x <= (614+115) && y <= (79+31)) { // roll dice
					System.out.println("Roll dice");
					rollDice();
				} else if(x >= 614 && y >= 131 && x <= (614+115) && y <= (131+31)) { // make suggestion
					System.out.println("Make suggestion");
					makeSuggestion();
				} else if(x >= 768 && y >= 79 && x <= (768+115) && y <= (79 +31)) { // end turn
					System.out.println("End turn");
					endTurn();
				} else if(x >= 768 && y >= 131 && x <= (768+115) && y <= (131+31)) { // make accusation
					System.out.println("Make accusation");
					makeAccusation();
				} else if(x >= 616 && x <= 632) { //first row notebook
					if(y >= 225 && y <= (225 + 17)) { // miss scarlett
						notebook.updateCardState("Miss Scarlett");
					} else if(y >= 246 && y <= (246 + 17)) { // colonel mustard
						notebook.updateCardState("Colonel Mustard");
					} else if(y >= 267 && y <= (267 + 17)) { // mrs.white
						notebook.updateCardState("Mrs. White");
					} else if(y >= 342 && y <= (342 + 17)) {
						notebook.updateCardState("Candlestick");
					} else if(y >= 363 && y <= (363 + 17)) {
						notebook.updateCardState("Dagger");
					} else if(y >= 384 && y <= (384 + 17)) {
						notebook.updateCardState("Lead pipe");
					} else if(y >= 459 && y <= (459 + 17)) {
						notebook.updateCardState("Kitchen");
					} else if(y >= 480 && y <= (480 + 17)) {
						notebook.updateCardState("Ballroom");
					} else if(y >= 501 && y <= (501 + 17)) {
						notebook.updateCardState("Conservatory");
					} else if(y >= 522 && y <= (522 + 17)) {
						notebook.updateCardState("Dining Room");
					} else if(y >= 543 && y <= (543 + 17)) {
						notebook.updateCardState("Lounge");
					}
					
					repaint();
					
				} else if(x >= 767 && x <= 783) { // second row notebook
					if(y >= 225 && y <= (225 + 17)) { 
						notebook.updateCardState("Reverend Green");
					} else if(y >= 246 && y <= (246 + 17)) { 
						notebook.updateCardState("Mrs. Peacock");
					} else if(y >= 267 && y <= (267 + 17)) { 
						notebook.updateCardState("Professor Plum");
					} else if(y >= 342 && y <= (342 + 17)) {
						notebook.updateCardState("Revolver");
					} else if(y >= 363 && y <= (363 + 17)) {
						notebook.updateCardState("Rope");
					} else if(y >= 384 && y <= (384 + 17)) {
						notebook.updateCardState("Wrench");
					} else if(y >= 459 && y <= (459 + 17)) {
						notebook.updateCardState("Hall");
					} else if(y >= 480 && y <= (480 + 17)) {
						notebook.updateCardState("Study");
					} else if(y >= 501 && y <= (501 + 17)) {
						notebook.updateCardState("Library");
					} else if(y >= 522 && y <= (522 + 17)) {
						notebook.updateCardState("Billiard Room");
					}
					
					repaint();
				}
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}