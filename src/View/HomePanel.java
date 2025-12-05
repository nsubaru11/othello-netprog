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
	private static final BufferedImage startImage, finishImage, backgroundImage;
	private static final String titleText = "Othello Game";
	private static final Font titleFont = new Font("Arial", Font.BOLD, 64);

	static {
		try {
			startImage = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/start2.png")));
			finishImage = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/finish2.png")));
			backgroundImage = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/background.png")));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load button images", e);
		}
	}

	// --------------- フィールド ---------------
	private final OthelloGUI gui;
	private final JButton startButton, finishButton;

	// 事前生成画像（インスタンス変数）
	private ImageIcon startIconNormal, startIconPressed;
	private ImageIcon finishIconNormal, finishIconPressed;

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

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int imageWidth = backgroundImage.getWidth();
		int imageHeight = backgroundImage.getHeight();
		double imageAspect = (double) imageWidth / imageHeight;
		double panelAspect = (double) panelWidth / panelHeight;

		// 背景画像を描画
		int drawWidth, drawHeight;
		int imgX, imgY;
		if (panelAspect > imageAspect) {
			// パネルの方が横長 ? 横幅を合わせて縦をトリミング
			drawWidth = panelWidth;
			drawHeight = (int) (panelWidth / imageAspect);
			imgX = 0;
			imgY = (panelHeight - drawHeight) / 2;
		} else {
			// パネルの方が縦長 ? 縦幅を合わせて横をトリミング
			drawHeight = panelHeight;
			drawWidth = (int) (panelHeight * imageAspect);
			imgY = 0;
			imgX = (panelWidth - drawWidth) / 2;
		}
		g.drawImage(backgroundImage, imgX, imgY, drawWidth, drawHeight, this); // アスペクト比を維持

		// 影付き文字を描画
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setFont(titleFont);
		FontMetrics fm = g2d.getFontMetrics();
		int textWidth = fm.stringWidth(titleText);
		int textX = (panelWidth - textWidth) / 2;
		int textY = panelHeight / 3;

		// 影の描画
		g2d.setColor(new Color(0, 0, 0, 120));
		g2d.drawString(titleText, textX + 3, textY + 3);

		// 文字の描画
		g2d.setColor(Color.WHITE);
		g2d.drawString(titleText, textX, textY);
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュ
	 * パフォーマンス最適化のため、クリックごとの画像生成を回避
	 *
	 * @param buttonSize ボタンサイズ
	 */
	private void prepareImages(int buttonSize) {
		int pressedSize = (int) (buttonSize * 0.95);

		// start画像の生成
		startIconNormal = new ImageIcon(startImage.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));
		startIconPressed = new ImageIcon(createPressedImage(startImage, pressedSize));

		// finish画像の生成
		finishIconNormal = new ImageIcon(finishImage.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH));
		finishIconPressed = new ImageIcon(createPressedImage(finishImage, pressedSize));
	}

	/**
	 * 押下時の視覚効果用画像を生成
	 * サイズ縮小と半透明化を適用
	 *
	 * @param source 元画像
	 * @param size   縮小後のサイズ
	 * @return 視覚効果が適用された画像
	 */
	private Image createPressedImage(BufferedImage source, int size) {
		// リサイズされたImageオブジェクトの作成
		Image scaled = source.getScaledInstance(size, size, Image.SCALE_SMOOTH);

		// 画像の半透明化を行うためにBufferedImageクラスでラップ
		BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

		// Graphics2Dで半透明化
		Graphics2D g2d = result.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		g2d.drawImage(scaled, 0, 0, null);
		g2d.dispose();

		return result;
	}

	/**
	 * ボタンの初期化
	 *
	 * @param button       初期化対象
	 * @param normalImage  通常時の画像
	 * @param pressedImage 押下時の画像
	 * @param buttonSize   ボタンサイズ
	 */
	private void initButton(JButton button, ImageIcon normalImage, ImageIcon pressedImage, int buttonSize) {
		Dimension size = new Dimension(buttonSize, buttonSize);

		// 諸々の設定（押下時にサイズ画像サイズにつられないようにとか、枠線を消したりとか）
		button.setIcon(normalImage);
		button.setPreferredSize(size);
		button.setMinimumSize(size);
		button.setMaximumSize(size);
		button.setBorderPainted(false); // 枠線 = false
		button.setContentAreaFilled(false); // ボタン領域を透明化
		button.setFocusPainted(false); // 押下時の枠線の有無

		// この設定は柔軟性が低い
		// button.setPressedIcon(pressedImage);
		// button.setHorizontalAlignment(SwingConstants.CENTER);
		// button.setVerticalAlignment(SwingConstants.CENTER);

		// 押下時のアクション（↑だと押下時に画像が動く）
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