package View;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * ゲームのホーム画面を表示するパネルです。
 * 背景画像、タイトル、およびゲーム開始・終了ボタンを提供します。
 */
class HomePanel extends JPanel {
	// --------------- クラス定数定義 ---------------
	/** スタートボタンの画像 */
	private static final BufferedImage START_IMAGE;
	/** 終了ボタンの画像 */
	private static final BufferedImage FINISH_IMAGE;
	/** 背景画像 */
	private static final BufferedImage BACKGROUND_IMAGE;
	/** タイトルテキスト */
	private static final String TITLE_TEXT = "Othello Game";
	/** タイトルフォント */
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 64);

	static {
		try {
			START_IMAGE = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/start2.png")));
			FINISH_IMAGE = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/finish2.png")));
			BACKGROUND_IMAGE = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/background.png")));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load button images", e);
		}
	}

	// --------------- フィールド ---------------
	/** 親GUIへの参照 */
	private final OthelloGUI gui;
	/** スタートボタン */
	private final JButton startButton;
	/** 終了ボタン */
	private final JButton finishButton;

	/** スタートボタンの通常アイコン */
	private ImageIcon startIconNormal;
	/** スタートボタンの押下時アイコン */
	private ImageIcon startIconPressed;
	/** 終了ボタンの通常アイコン */
	private ImageIcon finishIconNormal;
	/** 終了ボタンの押下時アイコン */
	private ImageIcon finishIconPressed;

	/**
	 * HomePanelを構築します。
	 *
	 * @param gui 親となるOthelloGUIインスタンス
	 */
	public HomePanel(final OthelloGUI gui) {
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

		// finishボタンの配置
		finishButton = new JButton();
		initButton(finishButton, finishIconNormal, finishIconPressed, buttonSize);
		finishButton.addActionListener(e -> System.exit(0));
		gbc.insets = new Insets(200, 200, 10, 10);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		add(finishButton, gbc);

		// startボタンの配置
		startButton = new JButton();
		initButton(startButton, startIconNormal, startIconPressed, buttonSize);
		startButton.addActionListener(e -> gui.showGame());
		gbc.insets = new Insets(200, 10, 10, 200);
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		add(startButton, gbc);
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
		int textY = panelHeight / 3;

		// 影の描画
		g2d.setColor(new Color(0, 0, 0, 120));
		g2d.drawString(TITLE_TEXT, textX + 3, textY + 3);

		// 文字の描画
		g2d.setColor(Color.WHITE);
		g2d.drawString(TITLE_TEXT, textX, textY);
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュします。
	 * パフォーマンス最適化のため、クリックごとの画像生成を回避します。
	 *
	 * @param buttonSize ボタンサイズ
	 */
	private void prepareImages(int buttonSize) {
		int pressedSize = (int) (buttonSize * 0.95);

		// スタート画像の生成
		startIconNormal = new ImageIcon(scaleImage(START_IMAGE, buttonSize, buttonSize));
		startIconPressed = new ImageIcon(createPressedImage(START_IMAGE, pressedSize));

		// 終了画像の生成
		finishIconNormal = new ImageIcon(scaleImage(FINISH_IMAGE, buttonSize, buttonSize));
		finishIconPressed = new ImageIcon(createPressedImage(FINISH_IMAGE, pressedSize));
	}

	/**
	 * 画像を指定サイズにスケーリングします。
	 * Graphics2Dを使用してgetScaledInstanceより高速に処理します。
	 *
	 * @param source 元画像
	 * @param width  スケーリング後の幅
	 * @param height スケーリング後の高さ
	 * @return スケーリングされた画像
	 */
	private BufferedImage scaleImage(BufferedImage source, int width, int height) {
		BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = scaled.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(source, 0, 0, width, height, null);
		g2d.dispose();
		return scaled;
	}

	/**
	 * 押下時の視覚効果用画像を生成します。
	 * サイズ縮小と半透明化を適用します。
	 *
	 * @param source 元画像
	 * @param size   縮小後のサイズ
	 * @return 視覚効果が適用された画像
	 */
	private BufferedImage createPressedImage(BufferedImage source, int size) {
		BufferedImage scaled = scaleImage(source, size, size);
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

		// ボタンの基本設定（枠線を消し、透明化）
		button.setIcon(normalImage);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);

		// 押下時のアクション
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				button.setIcon(pressedImage);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				button.setIcon(normalImage);
			}
		});
	}
}