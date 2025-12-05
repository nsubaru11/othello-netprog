package View;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * ゲーム起動時のロード画面を表示するパネルです。
 * ロード完了後にホーム画面へ遷移します。
 */
class LoadPanel extends JPanel {
	// --------------- クラス定数定義 ---------------
	/** 背景画像のパス */
	private static final String BACKGROUND_IMAGE_PATH = "/Assets/background.png";
	/** 背景画像 */
	private static final BufferedImage BACKGROUND_IMAGE;
	/** タイトルテキスト */
	private static final String TITLE_TEXT = "Othello Game";
	/** タイトルフォント */
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 64);

	static {
		try {
			BACKGROUND_IMAGE = ImageIO.read(Objects.requireNonNull(LoadPanel.class.getResourceAsStream(BACKGROUND_IMAGE_PATH)));
		} catch (final IOException | NullPointerException e) {
			throw new RuntimeException("Failed to load background image", e);
		}
	}

	// --------------- フィールド ---------------
	/** 親GUIへの参照 */
	private final OthelloGUI gui;
	/** プログレスバー */
	private final JProgressBar progressBar;
	/** アニメーション用タイマー */
	private final Timer timer;
	/** 現在の進捗値 */
	private int progress = 0;

	/**
	 * LoadPanelを構築します。
	 *
	 * @param gui 親となるOthelloGUIインスタンス
	 */
	public LoadPanel(final OthelloGUI gui) {
		this.gui = gui;
		setLayout(new GridBagLayout());
		setBackground(new Color(34, 139, 34));

		// プログレスバーの設定
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(gui.getWidth() / 2, gui.getWidth() / 20));
		progressBar.setFont(new Font("Monospaced", Font.PLAIN, 14));
		progressBar.setBackground(Color.WHITE);
		progressBar.setForeground(new Color(0, 200, 0));

		// レイアウト設定
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(300, 0, 0, 0);
		add(progressBar, gbc);

		// ロードアニメーション用のタイマー
		timer = new Timer(30, e -> updateProgress());
	}

	/**
	 * ロード処理を開始します。
	 */
	public void startProgress() {
		progress = 0;
		progressBar.setValue(0);
		timer.start();
	}

	/**
	 * 背景画像と影付きタイトルを描画します。
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int imageWidth = BACKGROUND_IMAGE.getWidth();
		int imageHeight = BACKGROUND_IMAGE.getHeight();
		double imageAspect = (double) imageWidth / imageHeight;
		double panelAspect = (double) panelWidth / panelHeight;

		// 背景画像を描画
		int drawWidth, drawHeight;
		int imgX, imgY;
		if (panelAspect > imageAspect) {
			// パネルの方が横長 → 横幅を合わせて縦をトリミング
			drawWidth = panelWidth;
			drawHeight = (int) (panelWidth / imageAspect);
			imgX = 0;
			imgY = (panelHeight - drawHeight) / 2;
		} else {
			// パネルの方が縦長 → 縦幅を合わせて横をトリミング
			drawHeight = panelHeight;
			drawWidth = (int) (panelHeight * imageAspect);
			imgY = 0;
			imgX = (panelWidth - drawWidth) / 2;
		}
		g.drawImage(BACKGROUND_IMAGE, imgX, imgY, drawWidth, drawHeight, this);

		// 影付き文字を描画
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(TITLE_FONT);
		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = fm.stringWidth(TITLE_TEXT);
		int textX = (panelWidth - textWidth) / 2;
		int textY = panelHeight / 2 - fm.getHeight() / 2 + fm.getAscent() - 50;

		// 影の描画
		g2d.setColor(new Color(0, 0, 0, 120));
		g2d.drawString(TITLE_TEXT, textX + 3, textY + 3);

		// 文字の描画
		g2d.setColor(Color.WHITE);
		g2d.drawString(TITLE_TEXT, textX, textY);
	}

	/**
	 * プログレスバーを更新します。
	 */
	private void updateProgress() {
		progress++;
		progressBar.setValue(progress);

		int dots = progress % 4;
		StringBuilder loadStr = new StringBuilder("Loading");
		for (int i = 0; i < dots; i++) loadStr.append('.');
		String loadString = String.format("%-10s%3d%%", loadStr, progress);
		progressBar.setString(loadString);

		// 100%で画面遷移
		if (progress >= 100) {
			timer.stop();
			Timer transitionTimer = new Timer(500, e -> {
				gui.showHome();
				((Timer) e.getSource()).stop();
			});
			transitionTimer.setRepeats(false);
			transitionTimer.start();
		}
	}
}
