package View;

import Model.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

class GamePanel extends JPanel {
	private static final BufferedImage whiteImage, blackImage, greenFrameImage;

	static {
		try {
			whiteImage = ImageIO.read(Objects.requireNonNull(OthelloGUI.class.getResource("/Assets/white.jpg")));
			blackImage = ImageIO.read(Objects.requireNonNull(OthelloGUI.class.getResource("/Assets/black.jpg")));
			greenFrameImage = ImageIO.read(Objects.requireNonNull(OthelloGUI.class.getResource("/Assets/greenFrame.jpg")));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load button images", e);
		}
	}

	private final int n;
	private final OthelloGUI gui;
	private final JButton[][] board;

	private int cellSize;
	private ImageIcon whiteCellIcon;
	private ImageIcon blackCellIcon;
	private ImageIcon greenCellIcon;
	private ComponentListener componentListener;

	public GamePanel(final OthelloGUI gui, final int n) {
		this.gui = gui;
		this.n = n;

		// 画面構成の設定
		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());
		GridBagConstraints gbc = new GridBagConstraints();

		// 各セルの初期化
		board = new JButton[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board[i][j] = new JButton();
				initButton(i, j, greenCellIcon);
				// 端のマスには余白を挿入
				int left = j == 0 ? 10 : 0;
				int right = j == n - 1 ? 10 : 0;
				gbc.insets = new Insets(0, left, 0, right);
				gbc.gridx = j;
				gbc.gridy = i;
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
		int half = n / 2;
		setPiece(half - 1, half - 1, Piece.BLACK);
		setPiece(half - 1, half, Piece.WHITE);
		setPiece(half, half - 1, Piece.WHITE);
		setPiece(half, half, Piece.BLACK);
	}

	public void removeNotify() {
		super.removeNotify();
		removeComponentListener(componentListener);
	}

	private void resizeBoard() {
		int width = getWidth(), height = getHeight();

		// パネルが表示されていない、またはサイズが小さすぎる場合は何もしない
		if (width <= 0 || height <= 0) return;

		// 新しいセルサイズを計算 (余白を考慮して計算式はそのまま利用)
		int newCellSize = Math.min(width / (n + 1), height / (n + 1));

		// サイズが変更された場合のみ更新
		if (newCellSize > 0 && newCellSize != cellSize) {
			cellSize = newCellSize;
			prepareImages(cellSize);
			Dimension newDim = new Dimension(cellSize, cellSize);

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
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

	public void setPiece(int i, int j, Piece piece) {
		board[i][j].putClientProperty(Piece.class, piece);
		if (piece.isBlack()) {
			board[i][j].setIcon(blackCellIcon);
		} else if (piece.isWhite()) {
			board[i][j].setIcon(whiteCellIcon);
		} else {
			board[i][j].setIcon(greenCellIcon);
		}
	}

	public void setPieces(Piece piece, List<Integer> pieces) {
		for (int ij : pieces) {
			int i = ij / n, j = ij % n;
			setPiece(i, j, piece);
		}
	}

	/**
	 * ボタン用の画像を事前生成してキャッシュ
	 * パフォーマンス最適化のため、クリックごとの画像生成を回避
	 *
	 * @param cellSize ボタンサイズ
	 */
	private void prepareImages(int cellSize) {
		whiteCellIcon = new ImageIcon(whiteImage.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		blackCellIcon = new ImageIcon(blackImage.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
		greenCellIcon = new ImageIcon(greenFrameImage.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH));
	}

	/**
	 * ボタンの初期化
	 *
	 * @param i 初期化するセルの i 座標
	 * @param j 初期化するセルの j 座標
	 * @param normalImage  通常時の画像
	 */
	private void initButton(int i, int j, ImageIcon normalImage) {
		JButton button = board[i][j];
		// 諸々の設定（押下時にサイズ画像サイズにつられないようにとか、枠線を消したりとか）
		button.putClientProperty(Piece.class, Piece.EMPTY);
		button.setIcon(normalImage);
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
				System.out.println("Cell(" + i + ", " + j + ", " + board[i][j].getClientProperty(Piece.class) + ")");
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				// todo: コントローラーに伝達
			}
		});
	}
}
