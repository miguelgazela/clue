package game_ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class UIResourcesLoader{
	
	private static final long serialVersionUID = -6253717236840388878L;
	
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
	public BufferedImage player_interface;
	public BufferedImage suggestion;
	
	public UICoord[] new_game_btns_coords;
	
	public HashMap<String, UICoord> notebook_card_coords;
	public HashMap<String, UICoord> suggestion_card_coords;
	public HashMap<String, Color> player_colors;
	
	public UICoord turn_coord;
	public UICoord game_status_coord;
	public UICoord board_source_coord_main_ui;
	public UICoord board_source_coord_player_interface;
	public UICoord sidebar_name_coord;
	public UICoord player_dashboard;
	
	private Image[] v_newGameStrings;
	public UICoord new_game_first_player_coord;
	
	private Image[] v_players_tokens;
	public Image[] v_players_sidebar_names;
	private Image[] v_players_dashboards;
	private Image[] v_cards_note_status;
	
	private GameImage[] v_turns;
	private Image[] v_gameStatus;
	
	public GameImage startGameBtn, returnToGameBtn;
	public GameImage confirmReset;
	
	private UIResourcesLoader() {
		initPieces();
		initImages();
		initBtns();
		initStrings();
		initCoords();
		
		// initialize backgrounds
		try {
			mainmenu_bg = ImageIO.read(new File("images/backgrounds/mainmenu.png"));
			//about_bg = ImageIO.read(new File("images/backgrounds/about.png"));
			devteam_bg = ImageIO.read(new File("images/backgrounds/dev_team.png"));
			newgame_bg = ImageIO.read(new File("images/backgrounds/newgame.png"));
			game_bg = ImageIO.read(new File("images/backgrounds/game.png"));
			player_interface = ImageIO.read(new File("images/backgrounds/player_interface.png"));
			suggestion = ImageIO.read(new File("images/backgrounds/suggestion.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Background Resources Missing");
			System.exit(-1);
		}
		
		
		// init players colors
		player_colors = new HashMap<>();
		player_colors.put("Miss Scarlett", new Color(240, 110, 170));
		player_colors.put("Colonel Mustard", new Color(52, 152, 219));
		player_colors.put("Mrs. White", new Color(142, 68, 173));
		player_colors.put("Reverend Green", new Color(231, 76, 60));
		player_colors.put("Professor Plum", new Color(46, 204, 113));
		player_colors.put("Mrs. Peacock", new Color(230, 126, 34));
	}

	public static UIResourcesLoader getInstanceLoader() {
		if(instanceLoader == null) {
			instanceLoader = new UIResourcesLoader();
		}
		return instanceLoader;
	}
	
	private void initStrings() {
		try {
//			v_gameStatus = new Image[9];
//			v_gameStatus[0] = ImageIO.read(new File("images/strings/placePiece.png"));
//			v_gameStatus[1] = ImageIO.read(new File("images/strings/selectPiece.png"));
//			v_gameStatus[2] = ImageIO.read(new File("images/strings/movePiece.png"));
//			v_gameStatus[3] = ImageIO.read(new File("images/strings/p1Won.png"));
//			v_gameStatus[4] = ImageIO.read(new File("images/strings/p2Won.png"));
			v_newGameStrings = new Image[5];
			v_newGameStrings[0] = ImageIO.read(new File("images/buttons/add_player.png"));
			v_newGameStrings[1] = ImageIO.read(new File("images/buttons/human.png"));
			v_newGameStrings[2] = ImageIO.read(new File("images/buttons/rookie.png"));
			v_newGameStrings[3] = ImageIO.read(new File("images/buttons/detective.png"));
			v_newGameStrings[4] = ImageIO.read(new File("images/buttons/inspector.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Strings Resources Missing");
			System.exit(-1);
		}
	}
	
	public Image getNewGameString(int type) {
		return v_newGameStrings[type];
	}
	
	private void initImages() {
		v_cards_note_status = new Image[3];
		try {
			v_cards_note_status[0] = ImageIO.read(new File("images/players/has_card.png"));
			v_cards_note_status[1] = ImageIO.read(new File("images/players/not_solution.png"));
			v_cards_note_status[2] = ImageIO.read(new File("images/players/possible_solution.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Image getCardNoteStatus(int status) {
		return v_cards_note_status[status];
	}
	
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
		board_source_coord_main_ui = new UICoord(437, 21);
		board_source_coord_player_interface = new UICoord(14, 12);
		game_status_coord = new UICoord(363, 668);
		sidebar_name_coord = new UICoord(0, 176);
		player_dashboard = new UICoord(574, 9);
		new_game_first_player_coord = new UICoord(185, 183);
		
		notebook_card_coords = new HashMap<>();
		notebook_card_coords.put("Miss Scarlett", new UICoord(616, 225));
		notebook_card_coords.put("Colonel Mustard", new UICoord(616, 246));
		notebook_card_coords.put("Mrs. White", new UICoord(616, 267));
		notebook_card_coords.put("Reverend Green", new UICoord(767, 225));
		notebook_card_coords.put("Mrs. Peacock", new UICoord(767, 246));
		notebook_card_coords.put("Professor Plum", new UICoord(767, 267));
		
		notebook_card_coords.put("Candlestick", new UICoord(616, 342));
		notebook_card_coords.put("Dagger", new UICoord(616, 363));
		notebook_card_coords.put("Lead pipe", new UICoord(616, 384));
		notebook_card_coords.put("Revolver", new UICoord(767, 342));
		notebook_card_coords.put("Rope", new UICoord(767, 363));
		notebook_card_coords.put("Wrench", new UICoord(767, 384));
		
		notebook_card_coords.put("Kitchen", new UICoord(616, 459));
		notebook_card_coords.put("Ballroom", new UICoord(616, 480));
		notebook_card_coords.put("Conservatory", new UICoord(616, 501));
		notebook_card_coords.put("Dining Room", new UICoord(616, 522));
		notebook_card_coords.put("Lounge", new UICoord(616, 543));
		notebook_card_coords.put("Hall", new UICoord(767, 459));
		notebook_card_coords.put("Study", new UICoord(767, 480));
		notebook_card_coords.put("Library", new UICoord(767, 501));
		notebook_card_coords.put("Billiard Room", new UICoord(767, 522));
		
		suggestion_card_coords = new HashMap<>();
		suggestion_card_coords.put("Miss Scarlett", new UICoord(34, 47));
		suggestion_card_coords.put("Colonel Mustard", new UICoord(34, 68));
		suggestion_card_coords.put("Mrs. White", new UICoord(34, 89));
		suggestion_card_coords.put("Reverend Green", new UICoord(185, 47));
		suggestion_card_coords.put("Mrs. Peacock", new UICoord(185, 68));
		suggestion_card_coords.put("Professor Plum", new UICoord(185, 89));
		
		suggestion_card_coords.put("Candlestick", new UICoord(61346, 164));
		suggestion_card_coords.put("Dagger", new UICoord(34, 185));
		suggestion_card_coords.put("Lead pipe", new UICoord(34, 206));
		suggestion_card_coords.put("Revolver", new UICoord(185, 164));
		suggestion_card_coords.put("Rope", new UICoord(185, 185));
		suggestion_card_coords.put("Wrench", new UICoord(185, 206));
		
		suggestion_card_coords.put("Kitchen", new UICoord(34, 281));
		suggestion_card_coords.put("Ballroom", new UICoord(34, 302));
		suggestion_card_coords.put("Conservatory", new UICoord(34, 323));
		suggestion_card_coords.put("Dining Room", new UICoord(34, 344));
		suggestion_card_coords.put("Lounge", new UICoord(34, 365));
		suggestion_card_coords.put("Hall", new UICoord(185, 281));
		suggestion_card_coords.put("Study", new UICoord(185, 302));
		suggestion_card_coords.put("Library", new UICoord(185, 323));
		suggestion_card_coords.put("Billiard Room", new UICoord(185, 344));
	}
	
	public UICoord getNotebookCardStateCoord(String card) {
		return notebook_card_coords.get(card);
	}
	
	private void initPieces() {
		try {
			v_players_tokens = new Image[6];
			v_players_sidebar_names = new Image[6];
			v_players_dashboards = new Image[6];
			
//			v_selectedPieces = new Image[2];
//			v_hiddenPieces = new Image[2];
			
			for(int i = 0; i < 6; i++) {
				v_players_tokens[i] = ImageIO.read(new File("images/players/"+i+".png"));
				v_players_sidebar_names[i] = ImageIO.read(new File("images/players/"+i+"_name.png"));
				v_players_dashboards[i] = ImageIO.read(new File("images/players/"+i+"_dash.png"));
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
	
	public Image getPlayerToken(int index) {
		return v_players_tokens[index];
	}
	
	public Image getPlayerSidebarName(int index) {
		return v_players_sidebar_names[index];
	}
	
	public Image getPlayerDashBoard(int index) {
		return v_players_dashboards[index];
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
}
