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
	private final Dimension cellSizeDim;

	private ImageIcon whiteCellIcon;
	private ImageIcon blackCellIcon;
	private ImageIcon greenCellIcon;

	public GamePanel(final OthelloGUI gui, final int n) {
		this.gui = gui;
		this.n = n;

		Dimension dimension = gui.getSize();
		int width = dimension.width;
		int height = dimension.height;
		setSize(dimension);

		// 画面構成の設定
		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());
		GridBagConstraints gbc = new GridBagConstraints();

		int cellSize = Math.min(width / (n + 1), height / (n + 1));
		cellSizeDim = new Dimension(cellSize, cellSize);
		prepareImages(cellSize);

		board = new JButton[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board[i][j] = new JButton();
				initButton(i, j, greenCellIcon);
				int left = j == 0 ? 10 : 0; // 端のマスには余白を挿入
				int right = j == n - 1 ? 10 : 0;
				gbc.insets = new Insets(0, left, 0, right);
				gbc.gridx = j;
				gbc.gridy = i;
				add(board[i][j], gbc);
			}
		}
		int half = n / 2;
		setPiece(half - 1, half - 1, Piece.BLACK);
		setPiece(half - 1, half, Piece.WHITE);
		setPiece(half, half - 1, Piece.WHITE);
		setPiece(half, half, Piece.BLACK);
	}

	public void setPiece(int i, int j, Piece piece) {
		if (piece.isBlack()) {
			board[i][j].setIcon(blackCellIcon);
		} else if (piece.isWhite()) {
			board[i][j].setIcon(whiteCellIcon);
		} else {
			System.err.println("Invalid piece");
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
	 * @param i 初期化するセルの i 座標
	 * @param j 初期化するセルの j 座標
	 * @param normalImage  通常時の画像
	 */
	private void initButton(int i, int j, ImageIcon normalImage) {
		JButton button = board[i][j];
		// 諸々の設定（押下時にサイズ画像サイズにつられないようにとか、枠線を消したりとか）
		button.setIcon(normalImage);
		button.setPreferredSize(cellSizeDim);
		button.setMinimumSize(cellSizeDim);
		button.setMaximumSize(cellSizeDim);
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
				System.out.println("Cell(" + i + ", " + j + ")");
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				// todo: コントローラーに伝達
			}
		});
	}
}
