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
 * TODO: リセットボタンの作成
 */
class GamePanel extends JPanel {
	// --------------- クラス定数 ---------------
	/** 白駒（配置可能マス用ヒント）の画像のパス */
	private static final String WHITE_MOVE_HINT_IMAGE_PATH = "../assets/move_hint_white.png";
	/** 黒駒（配置可能マス用ヒント）の画像のパス */
	private static final String BLACK_MOVE_HINT_IMAGE_PATH = "../assets/move_hint_black.png";
	/** 白駒の画像のパス */
	private static final String WHITE_STONE_IMAGE_PATH = "../assets/white_stone.jpg";
	/** 黒駒の画像のパス */
	private static final String BLACK_STONE_IMAGE_PATH = "../assets/black_stone.jpg";
	/** 空きマスの画像のパス */
	private static final String EMPTY_CELL_IMAGE_PATH = "../assets/move_hint_frame.jpg";
	/** 背景画像のパス */
	private static final String BACKGROUND_IMAGE_PATH = "../assets/background.png";
	/** 白駒（配置可能マス用ヒント）の画像 */
	private static final BufferedImage WHITE_MOVE_HINT_IMAGE;
	/** 黒駒（配置可能マス用ヒント）の画像 */
	private static final BufferedImage BLACK_MOVE_HINT_IMAGE;
	/** 白駒の画像 */
	private static final BufferedImage WHITE_STONE_IMAGE;
	/** 黒駒の画像 */
	private static final BufferedImage BLACK_STONE_IMAGE;
	/** 空きマスの画像 */
	private static final BufferedImage EMPTY_CELL_IMAGE;
	/** 背景画像 */
	private static final BufferedImage BACKGROUND_IMAGE;
	/** 白駒（配置可能マス用ヒント）の文字列 */
	private static final String WHITE_MOVE_HINT = "WHITE_MOVE_HINT";
	/** 黒駒（配置可能マス用ヒント）の文字列 */
	private static final String BLACK_MOVE_HINT = "BLACK_MOVE_HINT";
	/** 白駒の文字列 */
	private static final String WHITE_STONE = "WHITE_STONE";
	/** 黒駒の文字列 */
	private static final String BLACK_STONE = "BLACK_STONE";
	/** 空きマスの文字列 */
	private static final String EMPTY_CELL = "EMPTY_CELL";

	static {
		try {
			WHITE_MOVE_HINT_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(WHITE_MOVE_HINT_IMAGE_PATH)));
			BLACK_MOVE_HINT_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(BLACK_MOVE_HINT_IMAGE_PATH)));
			WHITE_STONE_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(WHITE_STONE_IMAGE_PATH)));
			BLACK_STONE_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(BLACK_STONE_IMAGE_PATH)));
			EMPTY_CELL_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(EMPTY_CELL_IMAGE_PATH)));
			BACKGROUND_IMAGE = ImageIO.read(Objects.requireNonNull(GamePanel.class.getResource(BACKGROUND_IMAGE_PATH)));
		} catch (final IOException | NullPointerException e) {
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
	private ImageIcon whiteStoneIcon;
	/** 黒駒のアイコン */
	private ImageIcon blackStoneIcon;
	/** 空きマスのアイコン */
	private ImageIcon emptyCellIcon;
	/** 白駒のアイコン（配置可能マス表示用ヒント） */
	private ImageIcon whiteMoveHintIcon;
	/** 黒駒のアイコン（配置可能マス表示用ヒント） */
	private ImageIcon blackMoveHintIcon;

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
				initButton(i, j, emptyCellIcon);
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
		setPiece(Piece.WHITE, half - 1, half - 1);
		setPiece(Piece.BLACK, half - 1, half);
		setPiece(Piece.BLACK, half, half - 1);
		setPiece(Piece.WHITE, half, half);
	}

	/**
	 * 画面上部にメッセージを表示します。
	 * @param message 表示するメッセージ
	 */
	public void showMessage(final String message) {
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
	 * 指定位置に駒を配置します。
	 *
	 * @param piece 配置する駒
	 * @param i     行インデックス
	 * @param j     列インデックス
	 */
	public void setPiece(final Piece piece, final int i, final int j) {
		JButton button = board[i][j];
		if (piece.isWhite()) {
			button.setIcon(whiteStoneIcon);
			button.putClientProperty(Piece.class, WHITE_STONE);
		} else if (piece.isBlack()) {
			button.setIcon(blackStoneIcon);
			button.putClientProperty(Piece.class, BLACK_STONE);
		} else {
			button.setIcon(emptyCellIcon);
			button.putClientProperty(Piece.class, EMPTY_CELL);
		}
	}

	/**
	 * 指定位置に配置可能マスのヒントを設定します。
	 *
	 * @param piece 配置した際に裏返る駒の色
	 * @param i     行インデックス
	 * @param j     列インデックス
	 */
	public void setValidPiece(final Piece piece, final int i, final int j) {
		JButton button = board[i][j];
		if (piece.isWhite()) {
			button.setIcon(whiteMoveHintIcon);
			button.putClientProperty(Piece.class, WHITE_MOVE_HINT);
		} else if (piece.isBlack()) {
			button.setIcon(blackMoveHintIcon);
			button.putClientProperty(Piece.class, BLACK_MOVE_HINT);
		} else {
			button.setIcon(emptyCellIcon);
			button.putClientProperty(Piece.class, EMPTY_CELL);
		}
	}

	/**
	 * 背景画像を描画します。
	 */
	@Override
	protected void paintComponent(final Graphics g) {
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
					String property = (String) button.getClientProperty(Piece.class);
					switch (property) {
						case WHITE_MOVE_HINT:
							button.setIcon(whiteMoveHintIcon);
							break;
						case BLACK_MOVE_HINT:
							button.setIcon(blackMoveHintIcon);
							break;
						case WHITE_STONE:
							button.setIcon(whiteStoneIcon);
							break;
						case BLACK_STONE:
							button.setIcon(blackStoneIcon);
							break;
						case EMPTY_CELL:
							button.setIcon(emptyCellIcon);
							break;
						default:
							System.err.println("Invalid piece property: " + property);
							break;
					}
				}
			}
			revalidate();
			repaint();
		}
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュします。
	 * パフォーマンス最適化のため、クリックごとの画像生成を回避します。
	 *
	 * @param cellSize ボタンサイズ
	 */
	private void prepareImages(final int cellSize) {
		whiteStoneIcon = new ImageIcon(WHITE_STONE_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		whiteMoveHintIcon = new ImageIcon(WHITE_MOVE_HINT_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		blackStoneIcon = new ImageIcon(BLACK_STONE_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		blackMoveHintIcon = new ImageIcon(BLACK_MOVE_HINT_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		emptyCellIcon = new ImageIcon(EMPTY_CELL_IMAGE.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
	}

	/**
	 * ボタンの初期化を行います。
	 *
	 * @param i           初期化するセルの行インデックス
	 * @param j           初期化するセルの列インデックス
	 * @param normalImage 通常時の画像
	 */
	private void initButton(final int i, final int j, final ImageIcon normalImage) {
		JButton button = board[i][j];
		// ボタンの基本設定（枠線を消し、透明化）
		button.putClientProperty(Piece.class, EMPTY_CELL);
		button.setIcon(normalImage);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		// 押下時のアクション
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				System.out.println("Cell(" + i + ", " + j + ", " + button.getClientProperty(Piece.class) + ")");
				controller.setPiece(i, j);
			}
		});
	}
}
