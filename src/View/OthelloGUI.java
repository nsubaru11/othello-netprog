package View;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class OthelloGUI extends JFrame {
	private static final Image whiteImage, blackImage, greenFrameImage, iconImage;
	private static final Color backgroundColor = new Color(34, 139, 34);

	static {
		whiteImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("/assets/White.jpg"))).getImage();
		blackImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("/assets/Black.jpg"))).getImage();
		greenFrameImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("/assets/GreenFrame.jpg"))).getImage();
		iconImage = new ImageIcon(Objects.requireNonNull(OthelloGUI.class.getResource("/assets/Icon.png"))).getImage();
	}

	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private final LoadPanel loadPanel;
	private final HomePanel homePanel;
	private final GamePanel gamePanel;

	public OthelloGUI() {
		// 画面サイズの設定（デフォルト）
		// 短い辺の60%を幅、80%を高さにする
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double size = Math.min(screenSize.getWidth(), screenSize.getHeight());
		screenSize.setSize(size * 0.6, size * 0.8);
		setSize(screenSize);

		setTitle("Othello");
		setIconImage(iconImage);
		setBackground(backgroundColor);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(true);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		loadPanel = new LoadPanel(this);
		homePanel = new HomePanel(this);
		gamePanel = new GamePanel(this);

		cardPanel.add(loadPanel, "load");
		cardPanel.add(homePanel, "home");
		cardPanel.add(gamePanel, "game");

		add(cardPanel);
		setVisible(true);
		showLoad();
	}

	public void showLoad() {
		cardLayout.show(cardPanel, "load");
		loadPanel.startProgress();
	}

	public void showHome() {
		cardLayout.show(cardPanel, "home");
	}

	public void showGame() {
		cardLayout.show(cardPanel, "game");
	}
}
