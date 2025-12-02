package View;

import javax.swing.*;
import java.awt.*;
import java.util.*;

class GamePanel extends JPanel {
	private static final Image whiteImage, blackImage, greenFrameImage;
	private static final Color backgroundColor = new Color(34, 139, 34);

	static {
		whiteImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("../Assets/white.jpg"))).getImage();
		blackImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("../Assets/black.jpg"))).getImage();
		greenFrameImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("../Assets/greenFrame.jpg"))).getImage();
	}

	public GamePanel(OthelloGUI gui, int n) {

	}

}
