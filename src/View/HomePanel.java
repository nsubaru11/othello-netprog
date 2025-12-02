package View;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;


class HomePanel extends JPanel {
	private static final BufferedImage startImage, finishImage;
	static {
		try {
			startImage = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/start.png")));
			finishImage = ImageIO.read(Objects.requireNonNull(HomePanel.class.getResourceAsStream("/Assets/finish.png")));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load button images", e);
		}
	}

	private final JButton startButton, finishButton;

	public HomePanel(OthelloGUI gui) {
		// 画面サイズの設定（デフォルト）
		Dimension dimension = gui.getSize();
		int width = dimension.width;
		int height = dimension.height;
		setSize(dimension);

		// 画面構成の設定
		setLayout(new GridBagLayout());
		setBackground(gui.getBackground());

		// 各ボタンの配置
		GridBagConstraints gbc = new GridBagConstraints();

		// タイトルの配置
		JLabel titleLabel = new JLabel("Othello Game");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		titleLabel.setForeground(Color.WHITE);
		gbc.insets = new Insets(10, 10, 200, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 6;
		gbc.gridheight = 1;
		add(titleLabel, gbc);

		// 各ボタンのサイズ
		int buttonSize = Math.min(width / 6, height / 6);
		Dimension buttonSizeDim = new Dimension(buttonSize, buttonSize);

		// finishボタンの配置
		initButton(finishButton = new JButton(), finishImage, buttonSize);
		gbc.insets = new Insets(10, 200, 10, 10);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		add(finishButton, gbc);

		// startボタンの配置
		initButton(startButton = new JButton(), startImage, buttonSize);
		gbc.insets = new Insets(10, 10, 10, 200);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		add(startButton, gbc);

	}

	/**
	 * ボタンの初期化
	 *
	 * @param button     初期化対象
	 * @param image      画像
	 * @param buttonSize ボタンサイズ
	 */
	private void initButton(final JButton button, BufferedImage image, int buttonSize) {
		Dimension buttonSizeDim = new Dimension(buttonSize, buttonSize);
		Image scaledImg = image.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
		button.setIcon(new ImageIcon(scaledImg));
		button.setPreferredSize(buttonSizeDim);
		button.setMinimumSize(buttonSizeDim);
		button.setMaximumSize(buttonSizeDim);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setRolloverEnabled(false);
		button.setFocusPainted(false);
		button.addMouseListener(new MouseAdapter() {
			private Graphics2D graphics2D;

			@Override
			public void mousePressed(MouseEvent e) {
				int buttonSizePressed = (int) (buttonSize * 0.95);
				Image imagePressed = image.getScaledInstance(buttonSizePressed, buttonSizePressed, Image.SCALE_SMOOTH);
				BufferedImage newImage = new BufferedImage(buttonSizePressed, buttonSizePressed, BufferedImage.TYPE_INT_ARGB);
				graphics2D = newImage.createGraphics();
				graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
				graphics2D.drawImage(imagePressed, 0, 0, null);
				button.setIcon(new ImageIcon(newImage));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				Image imageReleased = image.getScaledInstance(buttonSize, buttonSize, Image.SCALE_SMOOTH);
				button.setIcon(new ImageIcon(imageReleased));
				graphics2D.dispose();
			}
		});
	}

}
