package client.view;

import client.controller.*;
import model.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * オセロゲームのゲーム画面を表示するパネルです。
 * ゲームボードの描画と駒の配置を管理します。
 */
class GamePanel extends JPanel {
	// --------------- クラス定数定義 ---------------
	/** 白駒の画像 */
	private static final BufferedImage WHITE_IMAGE;
	/** 黒駒の画像 */
	private static final BufferedImage BLACK_IMAGE;
	/** 空きマスの画像 */
	private static final BufferedImage GREEN_FRAME_IMAGE;
	/** 背景画像 */
	private static final BufferedImage BACKGROUND_IMAGE;

	static {
		try {
			WHITE_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource("../assets/white.jpg")));
			BLACK_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource("../assets/black.jpg")));
			GREEN_FRAME_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource("../assets/greenFrame.jpg")));
			BACKGROUND_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResourceAsStream("../assets/background.png")));
		} catch (final IOException e) {
			throw new RuntimeException("セル画像の読み込みに失敗しました", e);
		}
	}

	// --------------- フィールド ---------------
	/** ボードサイズ（片辺のマス数） */
	private final int boardSize;
	/** 親GUIへの参照 */
	private final OthelloGUI gui;
	/** コントローラーへの参照*/
	private final GameController controller;
	/** ボード上のボタン配列 */
	private final JButton[][] board;
	/** リサイズ用のリスナー */
	private final ComponentListener componentListener;
	/** タイトルラベル */
	private final JLabel titleLabel;

	/** 現在のセルサイズ */
	private int cellSize;
	/** 白駒のアイコン */
	private ImageIcon whiteCellIcon;
	/** 黒駒のアイコン */
	private ImageIcon blackCellIcon;
	/** 空きマスのアイコン */
	private ImageIcon greenCellIcon;

	/**
	 * GamePanelを構築します。
	 *
	 * @param gui       親となるOthelloGUIインスタンス
	 * @param boardSize ボードのサイズ（片辺のマス数）
	 */
	public GamePanel(final OthelloGUI gui, final GameController controller, final int boardSize) {
		this.gui = gui;
		this.controller = controller;
		this.boardSize = boardSize;

		// 画面構成の設定
		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());
		GridBagConstraints gbc = new GridBagConstraints();

		// テキストを表示するためのラベル
		titleLabel = new JLabel("Waiting...", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(Color.BLACK);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = boardSize;
		gbc.insets = new Insets(20, 20, 20, 20);
		add(titleLabel, gbc);

		// 各セルの初期化
		board = new JButton[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = new JButton();
				initButton(i, j, greenCellIcon);
				// 端のマスには余白を挿入
				int left = j == 0 ? 10 : 0;
				int right = j == boardSize - 1 ? 10 : 0;
				gbc.insets = new Insets(0, left, 0, right);
				gbc.gridx = j;
				gbc.gridy = i + 1;
				gbc.gridwidth = 1;
				add(board[i][j], gbc);
			}
		}

		// リスナーの作成
		componentListener = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeBoard();
			}
		};
		addComponentListener(componentListener);

		resizeBoard();
		int half = boardSize / 2;
		setPiece(Piece.BLACK, half - 1, half - 1);
		setPiece(Piece.WHITE, half - 1, half);
		setPiece(Piece.WHITE, half, half - 1);
		setPiece(Piece.BLACK, half, half);
	}

	public void showMessage(String message) {
		titleLabel.setText(message);
		repaint();
	}

	/**
	 * コンポーネントが親から削除される際にリスナーをクリーンアップします。
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();
		removeComponentListener(componentListener);
	}

	/**
	 * ボードのサイズを現在のパネルサイズに合わせて調整します。
	 */
	private void resizeBoard() {
		int width = getWidth(), height = getHeight();

		// パネルが表示されていない、またはサイズが小さすぎる場合は何もしない
		if (width <= 0 || height <= 0) return;

		// 新しいセルサイズを計算（余白を考慮）
		int newCellSize = Math.min(width / (boardSize + 1), height / (boardSize + 1));

		// サイズが変更された場合のみ更新
		if (newCellSize > 0 && newCellSize != cellSize) {
			cellSize = newCellSize;
			prepareImages(cellSize);
			Dimension newDim = new Dimension(cellSize, cellSize);

			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					JButton button = board[i][j];
					button.setPreferredSize(newDim);
					Piece piece = (Piece) button.getClientProperty(Piece.class);
					if (piece == null) {
						button.setIcon(greenCellIcon);
					} else if (piece.isBlack()) {
						button.setIcon(blackCellIcon);
					} else if (piece.isWhite()) {
						button.setIcon(whiteCellIcon);
					} else {
						button.setIcon(greenCellIcon);
					}
				}
			}
			revalidate();
			repaint();
		}
	}

	/**
	 * 指定位置に駒を配置します。
	 *
	 * @param piece 配置する駒
	 * @param i     行インデックス
	 * @param j     列インデックス
	 */
	public void setPiece(Piece piece, int i, int j) {
		board[i][j].putClientProperty(Piece.class, piece);
		if (piece.isBlack()) {
			board[i][j].setIcon(blackCellIcon);
		} else if (piece.isWhite()) {
			board[i][j].setIcon(whiteCellIcon);
		} else {
			board[i][j].setIcon(greenCellIcon);
		}
	}

	/**
	 * 背景画像を描画します。
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
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュします。
	 * パフォーマンス最適化のため、クリックごとの画像生成を回避します。
	 *
	 * @param cellSize ボタンサイズ
	 */
	private void prepareImages(int cellSize) {
		whiteCellIcon = new ImageIcon(WHITE_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		blackCellIcon = new ImageIcon(BLACK_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		greenCellIcon = new ImageIcon(GREEN_FRAME_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
	}

	/**
	 * ボタンの初期化を行います。
	 *
	 * @param i           初期化するセルの行インデックス
	 * @param j           初期化するセルの列インデックス
	 * @param normalImage 通常時の画像
	 */
	private void initButton(int i, int j, ImageIcon normalImage) {
		JButton button = board[i][j];
		// ボタンの基本設定（枠線を消し、透明化）
		button.putClientProperty(Piece.class, Piece.EMPTY);
		button.setIcon(normalImage);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		// 押下時のアクション
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				System.out.println("Cell(" + i + ", " + j + ", " + board[i][j].getClientProperty(Piece.class) + ")");
				controller.setPiece(i, j);
			}
		});
	}
}
