package game_ui;

import game_logic.CluedoPlayer;
import game_logic.Coordinates;
import game_logic.HumanPlayerAgent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class UIHumanPlayer extends JFrame implements ActionListener {

	private static final long serialVersionUID = -682639614717071016L;
	private UIPlayerPanel uiPlayerPanel;
	private HumanPlayerAgent agent;

	public UIHumanPlayer(HumanPlayerAgent owner, String playerName) {
		super(playerName+" interface");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		uiPlayerPanel = new UIPlayerPanel();
		uiPlayerPanel.addMouseListener(uiPlayerPanel);
		getContentPane().add(uiPlayerPanel);
		
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	private class UIPlayerPanel extends JPanel implements MouseListener {

		private static final long serialVersionUID = 1035949055614632990L;
		private UIResourcesLoader uiResourcesLoader;
		private BufferedImage background;
		private Graphics graphics;
		
		public UIPlayerPanel() {
			uiResourcesLoader = UIResourcesLoader.getInstanceLoader();
			background = uiResourcesLoader.player_bg;
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
		public void mouseClicked(MouseEvent e) {}
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
