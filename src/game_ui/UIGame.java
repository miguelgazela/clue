package game_ui;

import game_logic.CluedoPlayer;
import game_logic.Coordinates;
import game_logic.GameManagerAgent;
import jade.core.Agent;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentController;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;

public class UIGame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -5256114500541984237L;
	private SLPanel panel;
	private final UIMainMenuPanel uiMainMenuPanel;
	private UIAboutPanel uiAboutPanel;
	private UINewGamePanel uiNewGamePanel;
	private UIDevTeamPanel uiDevTeamPanel;
	private UIGamePanel uiGamePanel;
	
	// game related instance variables
	public boolean hasGameRunning = false;
	public boolean humanPlayer = false;
	public int playerIndex = -1;
	public String turnPlayer = null;
	
	private SLConfig mainCfg, AboutCfg, NewGameCfg, DevTeamCfg, GameCfg;
	protected MenuState currentMenuState;
	protected GameManagerAgent gameManagerAgent;
	
	public UIGame(GameManagerAgent owner) {
		super("Cluedo - by Miguel Oliveira, Afonso Caldas & Rui Monteiro");
		System.out.println("Creating GUI");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new SLPanel();
		getContentPane().add(panel);
		currentMenuState = MenuState.Main;
		gameManagerAgent = owner;
		
		// defining the game menu panel
		uiMainMenuPanel = new UIMainMenuPanel();
		uiMainMenuPanel.addMouseMotionListener(uiMainMenuPanel);
		uiMainMenuPanel.addMouseListener(uiMainMenuPanel);
		uiMainMenuPanel.addKeyListener(uiMainMenuPanel);
		
		// defining the game rules panel
		uiAboutPanel = new UIAboutPanel();
		uiAboutPanel.addMouseListener(uiAboutPanel);
		
		// defining the dev. team panel
		uiDevTeamPanel = new UIDevTeamPanel();
		uiDevTeamPanel.addMouseListener(uiDevTeamPanel);
		
		// defining the new game panel
		uiNewGamePanel = new UINewGamePanel();
		uiNewGamePanel.addMouseListener(uiNewGamePanel);
		
		// defining the game panel
		uiGamePanel = new UIGamePanel();
		uiGamePanel.addMouseListener(uiGamePanel);
		
		initiatePanelConfigurations();
		
		setSize(1280, 742);
//		setUndecorated(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void initiatePanelConfigurations() {
		mainCfg = new SLConfig(panel)
		.row(1f).col(1f)
		.place(0, 0, uiMainMenuPanel);

		AboutCfg = new SLConfig(panel)
		.row(1f).col(1f)
		.place(0, 0, uiAboutPanel);
		
		DevTeamCfg = new SLConfig(panel)
		.row(1f).col(1f)
		.place(0, 0, uiDevTeamPanel);

		NewGameCfg = new SLConfig(panel)
		.row(1f).col(2f).col(1f)
		.place(0, 0, uiMainMenuPanel)
		.place(0, 1, uiNewGamePanel);
		
		GameCfg = new SLConfig(panel)
		.row(1f).col(1f)
		.place(0, 0, uiGamePanel);

		panel.setTweenManager(SLAnimator.createTweenManager());
		panel.initialize(mainCfg);
	}
	
	private class UIAboutPanel extends JPanel implements MouseListener {

		private static final long serialVersionUID = -7973208111694509132L;
		private UIResourcesLoader uiResourcesLoader;
		private BufferedImage background;
		private Graphics graphics;
		
		public UIAboutPanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.about_bg;
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
			super.paintComponent(graphics = g); // clear off-screen bitmap
			if (background != null) {
				graphics.drawImage(background, 0, 0, this);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			if(x > 1200 && y > 34 && x < 1249 && y < 80) {
				new Runnable() {@Override public void run() {
					panel.createTransition()
					.push(new SLKeyframe(mainCfg, 2f)
					.setStartSide(SLSide.TOP, uiMainMenuPanel)
					.setEndSide(SLSide.BOTTOM, uiAboutPanel)
					.setCallback(new SLKeyframe.Callback() {@Override public void done() {
						currentMenuState = MenuState.Main;
					}}))
					.play();
				}}.run();
			}
		}
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class UIDevTeamPanel extends JPanel implements MouseListener {
		private static final long serialVersionUID = 5264009616917087268L;
		private UIResourcesLoader uiResourcesLoader;
		private BufferedImage background;
		private Graphics graphics;
		
		public UIDevTeamPanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.devteam_bg;
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
			super.paintComponent(graphics = g); // clear off-screen bitmap
			if (background != null) {
				graphics.drawImage(background, 0, 0, this);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			
			int sifeupInitialY = 458, sifeupFinalY = 492;

			if(x > 1200 && y > 642 && x < 1249 && y < 689) { // getting back to main menu
				new Runnable() {@Override public void run() {
					panel.createTransition()
					.push(new SLKeyframe(mainCfg, 2f)
					.setStartSide(SLSide.BOTTOM, uiMainMenuPanel)
					.setEndSide(SLSide.TOP, uiDevTeamPanel)
					.setCallback(new SLKeyframe.Callback() {@Override public void done() {
						currentMenuState = MenuState.Main;
					}}))
					.play();
				}}.run();
			} else if(x > 178 && y > sifeupInitialY && x < 317 && y < sifeupFinalY) { // sifeup miguel
				WebPage.open("http://sigarra.up.pt/feup/pt/fest_geral.cursos_list?pv_num_unico=200700604");
			} else if(x > 561 && y > sifeupInitialY && x < 700 && y < sifeupFinalY) { // sifeup afonso
				WebPage.open("http://sigarra.up.pt/feup/pt/fest_geral.cursos_list?pv_num_unico=201009023");
			} else if(x > 944 && y > sifeupInitialY && x < 1083 && y < sifeupFinalY) { // sifeup rui
				WebPage.open("http://sigarra.up.pt/feup/pt/fest_geral.cursos_list?pv_num_unico=201005450");
			}
		}
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class UINewGamePanel extends JPanel implements MouseListener, MouseMotionListener {

		private static final int ADD_PLAYER = 0;
		private static final int HUMAN = 1;
		public static final int ROOKIE = 2;
		public static final int DETECTIVE = 3;
		public static final int INSPECTOR = 4;
		
		private static final long serialVersionUID = 7380205004739956983L;
		private UIResourcesLoader uiResourcesLoader;
		private Graphics graphics;
		private BufferedImage background;
		private int[] typePlayers = {HUMAN, ROOKIE, ROOKIE, ADD_PLAYER, ADD_PLAYER, ADD_PLAYER};
		
		public UINewGamePanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.newgame_bg;
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
			super.paintComponent(graphics = g); // clear off-screen bitmap

			// draws the game elements
			if (background != null) {
				graphics.drawImage(background, 0, 0, this);
				drawStartGameBtn();
			}

			// draw the type of players
			for(int i = 0; i < typePlayers.length; i++) {
				UICoord c = UIResourcesLoader.getInstanceLoader().new_game_first_player_coord;
				Image image = UIResourcesLoader.getInstanceLoader().getNewGameString(typePlayers[i]);
				graphics.drawImage(image, c.x, c.y + i*75, this);
			}
		}
		
		private void drawStartGameBtn() {
			GameImage btn = uiResourcesLoader.startGameBtn;
			graphics.drawImage(btn.image, btn.coord.x, btn.coord.y, this);
		}
		
		private void updateTypePlayer(int playerIndex) {
			if(playerIndex <= 2) { // they always have a player
				typePlayers[playerIndex] = (typePlayers[playerIndex] == INSPECTOR) ? HUMAN : (typePlayers[playerIndex] + 1);
			} else {
				if(typePlayers[playerIndex-1] != ADD_PLAYER) { // the user must fill out the previous players
					if(typePlayers[playerIndex] == INSPECTOR) {
						for(int i = playerIndex; i < typePlayers.length; i++) {
							typePlayers[i] = ADD_PLAYER;
						}
					} else {
						typePlayers[playerIndex] += 1;
					}
				}
			}
			repaint();
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(currentMenuState == MenuState.NewGame) {
				int x = e.getX();
				int y = e.getY();
				
				if(x > 30 && y > 643 && x < 79 && y < 690) { // back to main menu
					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(mainCfg, 1f)
						//.setStartSide(SLSide.RIGHT, uiNewGamePanel)
						.setEndSide(SLSide.RIGHT, uiNewGamePanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.Main;
						}}))
						.play();
					}}.run();
				} else if(x >= 373 && y >= 646 && x <= 417 && y <= 691) { // start game

					uiGamePanel.clearPossibleGame();
					uiGamePanel.startGame(typePlayers);

					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(GameCfg, 1f)
						.setStartSide(SLSide.RIGHT, uiGamePanel)
						.setEndSide(SLSide.LEFT, uiMainMenuPanel)
						.setEndSide(SLSide.RIGHT, uiNewGamePanel)
						.setDelay(1.0f, uiNewGamePanel)
						.setDelay(0.3f, uiMainMenuPanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.Game;
						}}))
						.play();
					}}.run();
					
				} else if(x >= 185 && x <= (185+205) && y >= 183 && y <= (183 + 40)) { // P1
					updateTypePlayer(0);
				} else if(x >= 185 && x <= (185+205) && y >= 258 && y <= (258 + 40)) { // P2
					updateTypePlayer(1);
				} else if(x >= 185 && x <= (185+205) && y >= 333 && y <= (333 + 40)) { // P3
					updateTypePlayer(2);
				} else if(x >= 185 && x <= (185+205) && y >= 408 && y <= (408 + 40)) { // P4
					updateTypePlayer(3);
				} else if(x >= 185 && x <= (185+205) && y >= 483 && y <= (483 + 40)) { // P5
					updateTypePlayer(4);
				} else if(x >= 185 && x <= (185+205) && y >= 558 && y <= (558 + 40)) { // P6
					updateTypePlayer(5);
				}
			}
		}

		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void mouseDragged(MouseEvent e) {}
		@Override public void mouseMoved(MouseEvent e) {}
	}
	
	private class UIGamePanel extends JPanel implements MouseListener {

		private static final long serialVersionUID = -7973208111694509132L;
		
		private UIResourcesLoader uiResourcesLoader;
		private BufferedImage background;
		private Graphics graphics;
		private boolean gameIsOver = false;
		private boolean showingResetWarning = false;
		
		public UIGamePanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.game_bg;
		}
		
		public void startGame(int[] typePlayer) {
			GuiEvent ge = new GuiEvent(this, GameManagerAgent.CREATE_GAME);
			ge.addParameter(typePlayer);
			gameManagerAgent.postGuiEvent(ge);
		}
		
		public void clearPossibleGame() {
			hasGameRunning = false;
			gameIsOver = false;
			showingResetWarning = false;
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
			super.paintComponent(graphics = g); // clear off-screen bitmap
			if (background != null) {
				graphics.drawImage(background, 0, 0, this);
				
				if(hasGameRunning) {
					// draw players on board
					ArrayList<CluedoPlayer> players = gameManagerAgent.getCluedo().getPlayers();
					
					for(int i = 0; i < players.size(); i++) {
						UICoord c = uiResourcesLoader.board_source_coord_main_ui;
						Coordinates pos = players.get(i).getPosOnBoard();
						
						
						if(pos != null) {
							graphics.drawImage(
									uiResourcesLoader.getPlayerToken(i), 
									c.x + pos.getX()*uiResourcesLoader.BOARD_POS_DIF, 
									c.y + pos.getY()*uiResourcesLoader.BOARD_POS_DIF,
									this
							);
						}
						
						// draw sidebar names
						c = uiResourcesLoader.sidebar_name_coord;
						graphics.drawImage(
								uiResourcesLoader.getPlayerSidebarName(i), 
								c.x, 
								c.y + i*50,
								this
						);
					}
					
					if(showingResetWarning) {
						GameImage reset = uiResourcesLoader.confirmReset;
						graphics.drawImage(reset.image, reset.coord.x, reset.coord.y, this);
					}
					
					// draw game status
					UICoord c = uiResourcesLoader.game_status_coord;
					Image status = null;
					graphics.drawImage(status, c.x, c.y, this);
				}
			}
		}

		private void goBackToMenu() {
			new Runnable() {@Override public void run() {
				panel.createTransition()
				.push(new SLKeyframe(mainCfg, 2f)
				.setStartSide(SLSide.LEFT, uiMainMenuPanel)
				.setEndSide(SLSide.RIGHT, uiGamePanel)
				.setCallback(new SLKeyframe.Callback() {@Override public void done() {
					currentMenuState = MenuState.Main;
				}}))
				.play();
			}}.run();
		}
		
		private void resetGame() {
			clearPossibleGame();
			GuiEvent ge = new GuiEvent(this, GameManagerAgent.RESET_GAME);
			gameManagerAgent.postGuiEvent(ge);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			System.out.println("X: "+x+" Y: "+y);

			if(x >= 14 && y >= 668 && x <= 58 && y <= 709) { // return to main menu
				goBackToMenu();
			} else if(x >= 1225 && y >= 664 && x <= 1266 && y <= 708) { // reset game
				if(!gameIsOver) {
					showingResetWarning = true;
				} else {
					resetGame();
				}
				repaint();
			} else {
				if(!gameIsOver) {
					if(!showingResetWarning) {
							
					} else { // it's showing the reset warning
						if(x >= 499 && y >= 407 && x <= 593 && y <= 446) { // yes
							resetGame();
							repaint();
						} else if(x >= 686 && y >= 407 && x <= 780 && y <= 446) { // no
							showingResetWarning = false;
							repaint();
						}
					}
				}
			}
		}
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class UIMainMenuPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
		
		private static final long serialVersionUID = -1237601154927560866L;
		private UIResourcesLoader uiResourcesLoader;
		private Graphics graphics;
		private BufferedImage background;
		
		public UIMainMenuPanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.mainmenu_bg;
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
			super.paintComponent(graphics = g); // clear off-screen bitmap
						
			// draws the game elements
			if (background != null) {
				graphics.drawImage(background, 0, 0, this);
				
				if(hasGameRunning) {
					GameImage returnToGameBtn = uiResourcesLoader.returnToGameBtn;
					graphics.drawImage(returnToGameBtn.image, returnToGameBtn.coord.x, returnToGameBtn.coord.y, this);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(currentMenuState == MenuState.Main) {
				int x = e.getX();
				int y = e.getY();
				System.out.println("X: "+x+" Y: "+y);

				if (y > 189 && y < 327 && x > 397 && x < 716) { // new game
					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(NewGameCfg, 1f)
						.setStartSide(SLSide.RIGHT, uiNewGamePanel)
						//.setEndSide(SLSide.LEFT, uiMainMenuPanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.NewGame;
						}}))
						.play();
					}}.run();
					
				} else if(x > 249 && y > 485 && x < 386 && y < 622) { // source code
					WebPage.open("https://github/miguelgazela/cluedo");
				} else if (y > 485 && y < 622 && x > 397 && x < 716) { // exit game
					System.exit(0);
				} else if (x > 397 && x < 551 && y > 337 && y < 474) { // about info
					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(AboutCfg, 2f)
						.setStartSide(SLSide.BOTTOM, uiAboutPanel)
						.setEndSide(SLSide.TOP, uiMainMenuPanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.About;
						}}))
						.play();
					}}.run();
				}
				else if (y > 337 && y < 474 && x > 562 && x < 716) { // dev. team
					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(DevTeamCfg, 2f)
						.setStartSide(SLSide.TOP, uiDevTeamPanel)
						.setEndSide(SLSide.BOTTOM, uiMainMenuPanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.DevTeam;
						}}))
						.play();
					}}.run();
				} else if(x >= 1225 && y >= 668 && x <= 1267 && y <= 711) { // return to game
					new Runnable() {@Override public void run() {
						panel.createTransition()
						.push(new SLKeyframe(GameCfg, 2f)
						.setStartSide(SLSide.RIGHT, uiGamePanel)
						.setEndSide(SLSide.LEFT, uiMainMenuPanel)
						.setCallback(new SLKeyframe.Callback() {@Override public void done() {
							currentMenuState = MenuState.Game;
						}}))
						.play();
					}}.run();
				}
			}
		}

		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
		@Override public void keyTyped(KeyEvent e) {}
		@Override public void keyPressed(KeyEvent e) {}
		@Override public void keyReleased(KeyEvent e) {}
		@Override public void mouseDragged(MouseEvent e) {}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
	}
}
