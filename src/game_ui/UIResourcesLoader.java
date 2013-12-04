package game_ui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class UIResourcesLoader {
	
	static public final int RANDOM_GAME = 0;
	static public final int STRATEGIC_GAME = 1;
	
	static public final int BOARD_POS_DIF = 23;
	
	static public final String P1 = "P1";
	static public final String P2 = "P2";
	
	private static UIResourcesLoader instanceLoader;
	
	public BufferedImage mainmenu_bg;
	public BufferedImage about_bg;
	public BufferedImage devteam_bg;
	public BufferedImage newgame_bg;
	public BufferedImage game_bg;
	
	public UICoord[] new_game_btns_coords;
	
	public UICoord turn_coord;
	public UICoord game_status_coord;
	public UICoord nextPiece_coord;
	public UICoord board_source_coord;
	
	private GameImage[] v_unselectedNewGameBtn;
	private GameImage[] v_selectedNewGameBtn;
	
	private Image[] v_players_tokens;
	private Image[] v_selected_tokens;
	
	private GameImage[] v_turns;
	private Image[] v_gameStatus;
	
	public GameImage startGameBtn, returnToGameBtn;
	public GameImage confirmReset;
	
	private UIResourcesLoader() {
		initPieces();
//		initImages();
		initBtns();
//		initStrings();
		initCoords();
		
		// initialize backgrounds
		try {
			mainmenu_bg = ImageIO.read(new File("images/backgrounds/mainmenu.png"));
			//about_bg = ImageIO.read(new File("images/backgrounds/about.png"));
			devteam_bg = ImageIO.read(new File("images/backgrounds/dev_team.png"));
			newgame_bg = ImageIO.read(new File("images/backgrounds/newgame.png"));
			game_bg = ImageIO.read(new File("images/backgrounds/game.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Background Resources Missing");
			System.exit(-1);
		}
	}

	public static UIResourcesLoader getInstanceLoader() {
		if(instanceLoader == null) {
			instanceLoader = new UIResourcesLoader();
		}
		return instanceLoader;
	}
	
//	private void initStrings() {
//		try {
//			v_gameStatus = new Image[9];
//			v_gameStatus[0] = ImageIO.read(new File("images/strings/placePiece.png"));
//			v_gameStatus[1] = ImageIO.read(new File("images/strings/selectPiece.png"));
//			v_gameStatus[2] = ImageIO.read(new File("images/strings/movePiece.png"));
//			v_gameStatus[3] = ImageIO.read(new File("images/strings/p1Won.png"));
//			v_gameStatus[4] = ImageIO.read(new File("images/strings/p2Won.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.out.println("Strings Resources Missing");
//			System.exit(-1);
//		}
//	}
	
//	private void initImages() {
//		v_turns = new GameImage[2];
//		v_turns[0] = new GameImage("images/pieces/turnP1.png", 272, 671);
//		v_turns[1] = new GameImage("images/pieces/turnP2.png", 272, 671);
//		confirmReset = new GameImage("images/backgrounds/confirmReset.png", 0, 0);
//	}
	
	private void initBtns() {
//		v_unselectedNewGameBtn = new GameImage[2];
//		v_selectedNewGameBtn = new GameImage[2];
//		
//		v_unselectedNewGameBtn[RANDOM_GAME] = new GameImage("images/buttons/random-uns.png", 105, 260);
//		v_unselectedNewGameBtn[STRATEGIC_GAME] = new GameImage("images/buttons/strategic-uns.png", 105, 375);
//		v_selectedNewGameBtn[RANDOM_GAME] = new GameImage("images/buttons/random-sel.png", 105, 260);
//		v_selectedNewGameBtn[STRATEGIC_GAME] = new GameImage("images/buttons/strategic-sel.png", 105, 375);
		
		returnToGameBtn = new GameImage("images/buttons/return_game.png", 993, 663);
		startGameBtn = new GameImage("images/buttons/start_game.png", 272, 640);
	}
	
	private void initCoords() {
		new_game_btns_coords = new UICoord[2];
		board_source_coord = new UICoord(210, 21);
		game_status_coord = new UICoord(363, 668);
	}
	
	private void initPieces() {
		try {
			v_players_tokens = new Image[6];
//			v_selectedPieces = new Image[2];
//			v_hiddenPieces = new Image[2];
			
			for(int i = 0; i < 6; i++) {
				v_players_tokens[i] = ImageIO.read(new File("images/players/"+i+".png"));
			}			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Resources missing");
			System.exit(-1);
		}
	}
	
//	public Image getGameStatus(String str) {
//		switch (str) {
//		case "place":
//			return v_gameStatus[0];
//		case "select":
//			return v_gameStatus[1];
//		case "move":
//			return v_gameStatus[2];
//		case "P1":
//			return v_gameStatus[3];
//		case "P2":
//			return v_gameStatus[4];
//		default:
//			return null;
//		}
//	}
	public UICoord getSourceCoord() {
		return board_source_coord;
	}
	
	public Image getPlayerToken(int index) {
		return v_players_tokens[index];
	}
	
//	public GameImage getPlayerTurn(String player) throws GameException {
//		if(!player.equals(P1) && !player.equals(P2)) {
//			throw new GameException("Invalid Token to get turn player: "+player);
//		}
//		if(player.equals(P1)) {
//			return v_turns[0];
//		} else {
//			return v_turns[1];
//		}
//	}

//	public Image getUnselectedPiece(Piece piece) {
//		try {
//			if(piece.getColor().toString() == new quotes.Blue().toString()) {
//				return v_piecesP1[piece.getRank().intValue()];
//			} else {
//				return v_piecesP2[piece.getRank().intValue()];
//			}
//		} catch (CGException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	public Image getHiddenPiece(Piece piece) {
//		try {
//			if(piece.getColor().toString() == new quotes.Blue().toString()) {
//				return v_hiddenPieces[0];
//			} else {
//				return v_hiddenPieces[1];
//			}
//		} catch (CGException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
//	public Image getSelectedPiece(Piece piece) throws GameException {
//		try {
//			if(piece.getColor().toString() == new quotes.Blue().toString()) {
//				return v_selectedPieces[0];
//			} else {
//				return v_selectedPieces[1];
//			}
//		} catch (CGException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public GameImage getUnselectedNewGameBtn(int btn) {
		if(btn < RANDOM_GAME || btn > STRATEGIC_GAME) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return v_unselectedNewGameBtn[btn];
	}
	
	public GameImage getSelectedNewGameBtn(int btn) {
		if(btn < RANDOM_GAME || btn > STRATEGIC_GAME) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return v_selectedNewGameBtn[btn];
	}
}
