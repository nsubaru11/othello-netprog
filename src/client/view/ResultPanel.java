package client.view;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * ゲーム終了後の結果画面を表示するパネルです。
 * 勝敗結果、スコア、およびホームに戻るボタンを提供します。
 */
class ResultPanel extends JPanel {
	// --------------- クラス定数定義 ---------------
	/** 背景画像 */
	private static final BufferedImage BACKGROUND_IMAGE;
	/** ホームボタンの画像 */
	private static final BufferedImage HOME_IMAGE;
	/** 結果タイトルフォント */
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 72);
	/** スコア表示フォント */
	private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 48);

	static {
		try {
			BACKGROUND_IMAGE = ImageIO.read(Objects.requireNonNull(ResultPanel.class.getResourceAsStream("../assets/background.png")));
			HOME_IMAGE = ImageIO.read(Objects.requireNonNull(ResultPanel.class.getResourceAsStream("../assets/home.png")));
		} catch (final IOException e) {
			throw new RuntimeException("結果画面の画像読み込みに失敗しました", e);
		}
	}

	// --------------- フィールド ---------------
	/** 親GUIへの参照 */
	private final OthelloGUI gui;
	/** ホームに戻るボタン */
	private final JButton homeButton;

	/** ホームボタンの通常アイコン */
	private ImageIcon homeIconNormal;
	/** ホームボタンの押下時アイコン */
	private ImageIcon homeIconPressed;
	/** 黒の駒数 */
	private int blackCount = 0;
	/** 白の駒数 */
	private int whiteCount = 0;
	/**
	 * 結果（"WIN", "LOSE", "DRAW"）
	 */
	private String result = "";

	/**
	 * ResultPanelを構築します。
	 *
	 * @param gui 親となるOthelloGUIインスタンス
	 */
	public ResultPanel(final OthelloGUI gui) {
		this.gui = gui;

		// 画面サイズの取得
		Dimension dimension = gui.getSize();
		int width = dimension.width;
		int height = dimension.height;
		setSize(dimension);

		// 画面構成の設定
		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());
		GridBagConstraints gbc = new GridBagConstraints();

		// ボタンサイズの計算
		int buttonSize = Math.min(width / 6, height / 6);
		prepareImages(buttonSize);

		// ホームボタンの配置
		homeButton = new JButton();
		initButton(homeButton, homeIconNormal, homeIconPressed, buttonSize);
		homeButton.addActionListener(e -> gui.showHome());
		gbc.insets = new Insets(250, 0, 10, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		add(homeButton, gbc);
	}

	/**
	 * ゲーム結果を設定します。
	 *
	 * @param blackCount 黒の駒数
	 * @param whiteCount 白の駒数
	 */
	public void setResult(String result, int blackCount, int whiteCount) {
		this.result = result;
		this.blackCount = blackCount;
		this.whiteCount = whiteCount;
		repaint();
	}

	/**
	 * 背景画像と結果表示を描画します。
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
			drawWidth = panelWidth;
			drawHeight = (int) (panelWidth / imageAspect);
			imgX = 0;
			imgY = (panelHeight - drawHeight) / 2;
		} else {
			drawHeight = panelHeight;
			drawWidth = (int) (panelHeight * imageAspect);
			imgY = 0;
			imgX = (panelWidth - drawWidth) / 2;
		}
		g.drawImage(BACKGROUND_IMAGE, imgX, imgY, drawWidth, drawHeight, this);

		// Graphics2Dの設定
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// 勝者タイトルの描画
		String titleText;
		switch (result) {
			case "WIN":
				titleText = "You win!";
				break;
			case "LOSE":
				titleText = "You Lose...";
				break;
			case "DRAW":
				titleText = "Draw！";
				break;
			default:
				titleText = "Game Over!";
				break;
		}
		g2d.setFont(TITLE_FONT);
		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = fm.stringWidth(titleText);
		int textX = (panelWidth - textWidth) / 2;
		int textY = panelHeight / 4;

		// 影の描画
		g2d.setColor(new Color(0, 0, 0, 120));
		g2d.drawString(titleText, textX + 4, textY + 4);

		// タイトル文字の描画（勝者に応じた色）
		Color titleColor;
		switch (result) {
			case "WIN":
				titleColor = Color.RED;
				break;
			case "LOSE":
				titleColor = Color.BLUE;
				break;
			default:
				titleColor = new Color(255, 215, 0);
				break;
		}
		g2d.setColor(titleColor);
		g2d.drawString(titleText, textX, textY);

		// スコアの描画
		String scoreText = "Black: " + blackCount + " - White: " + whiteCount;
		g2d.setFont(SCORE_FONT);
		fm = g2d.getFontMetrics();
		textWidth = fm.stringWidth(scoreText);
		textX = (panelWidth - textWidth) / 2;
		textY = panelHeight / 4 + 80;

		// 影の描画
		g2d.setColor(new Color(0, 0, 0, 120));
		g2d.drawString(scoreText, textX + 3, textY + 3);

		// スコア文字の描画
		g2d.setColor(Color.WHITE);
		g2d.drawString(scoreText, textX, textY);
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュします。
	 *
	 * @param buttonSize ボタンサイズ
	 */
	private void prepareImages(int buttonSize) {
		int pressedSize = (int) (buttonSize * 0.95);

		// ホーム画像の生成
		homeIconNormal = new ImageIcon(HOME_IMAGE.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));
		homeIconPressed = new ImageIcon(createPressedImage(HOME_IMAGE, pressedSize));
	}

	/**
	 * 押下時の視覚効果用画像を生成します。
	 *
	 * @param source 元画像
	 * @param size   縮小後のサイズ
	 * @return 視覚効果が適用された画像
	 */
	private Image createPressedImage(BufferedImage source, int size) {
		Image scaled = source.getScaledInstance(size, size, Image.SCALE_SMOOTH);
		BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = result.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		g2d.drawImage(scaled, 0, 0, null);
		g2d.dispose();
		return result;
	}

	/**
	 * ボタンの初期化を行います。
	 *
	 * @param button       初期化対象
	 * @param normalImage  通常時の画像
	 * @param pressedImage 押下時の画像
	 * @param buttonSize   ボタンサイズ
	 */
	private void initButton(JButton button, ImageIcon normalImage, ImageIcon pressedImage, int buttonSize) {
		Dimension size = new Dimension(buttonSize, buttonSize);

		// ボタンの基本設定
		button.setIcon(normalImage);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);

		// 押下時のアイコン変更
		button.setPressedIcon(pressedImage);
	}
}
