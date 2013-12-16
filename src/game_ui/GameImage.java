package game_ui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameImage {
	public Image image;
	public UICoord coord;
	
	public GameImage(String pathname) {
		try {
			image = ImageIO.read(new File(pathname));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public GameImage(String pathname, int x, int y) {
		this(pathname);
		coord = new UICoord(x, y);
	}
}
