package client.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * マッチング設定画面を表示するパネルです。
 * ユーザー名の入力、ボードサイズの選択、開始/キャンセルボタンを提供します。
 */
class MatchingPanel extends JPanel {
	// --------------- クラス定数定義 ---------------
	/** ボードサイズの選択肢 */
	private static final Integer[] BOARD_SIZES = {6, 8, 10, 12};

	// --------------- フィールド ---------------
	/** 親GUIへの参照 */
	private final OthelloGUI gui;
	/** ユーザー名入力フィールド */
	private final JTextField userNameField;
	/** ボードサイズ選択ドロップダウン */
	private final JComboBox<Integer> boardSizeComboBox;
	/** 開始ボタン */
	private final JButton startButton;
	/** キャンセルボタン */
	private final JButton cancelButton;

	/**
	 * MatchingPanelを構築します。
	 *
	 * @param gui 親となるOthelloGUIインスタンス
	 */
	public MatchingPanel(final OthelloGUI gui) {
		this.gui = gui;

		// 半透明オーバーレイ背景
		setOpaque(false);
		setLayout(new GridBagLayout());

		// ダイアログパネルの作成
		JPanel dialogPanel = createDialogPanel();

		// ダイアログ内のレイアウト設定
		dialogPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 20, 10, 20);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// タイトルラベル
		JLabel titleLabel = new JLabel("Game Settings", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		titleLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(20, 20, 20, 20);
		dialogPanel.add(titleLabel, gbc);

		// ユーザー名ラベル
		JLabel userNameLabel = new JLabel("User Name:");
		userNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		userNameLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 20, 5, 10);
		gbc.anchor = GridBagConstraints.WEST;
		dialogPanel.add(userNameLabel, gbc);

		// ユーザー名入力フィールド
		userNameField = new JTextField(15);
		userNameField.setFont(new Font("Arial", Font.PLAIN, 14));
		userNameField.setPreferredSize(new Dimension(200, 30));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 5, 20);
		dialogPanel.add(userNameField, gbc);

		// ボードサイズラベル
		JLabel boardSizeLabel = new JLabel("Board Size:");
		boardSizeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		boardSizeLabel.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 20, 10, 10);
		dialogPanel.add(boardSizeLabel, gbc);

		// ボードサイズドロップダウン
		boardSizeComboBox = new JComboBox<>(BOARD_SIZES);
		boardSizeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
		boardSizeComboBox.setSelectedItem(8); // デフォルトは8
		boardSizeComboBox.setPreferredSize(new Dimension(200, 30));
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = new Insets(5, 10, 10, 20);
		dialogPanel.add(boardSizeComboBox, gbc);

		// ボタンパネル
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonPanel.setOpaque(false);

		// 開始ボタン
		startButton = createStyledButton("Start", new Color(34, 139, 34));
		startButton.addActionListener(e -> onStartClicked());
		buttonPanel.add(startButton);

		// キャンセルボタン
		cancelButton = createStyledButton("Cancel", new Color(139, 69, 69));
		cancelButton.addActionListener(e -> onCancelClicked());
		buttonPanel.add(cancelButton);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(20, 20, 20, 20);
		dialogPanel.add(buttonPanel, gbc);

		// ダイアログをパネル中央に配置
		add(dialogPanel);
	}

	/**
	 * ダイアログパネルを作成します。
	 *
	 * @return ダイアログパネル
	 */
	private JPanel createDialogPanel() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// 角丸の背景
				g2d.setColor(new Color(45, 45, 45));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

				// 枠線
				g2d.setColor(new Color(100, 100, 100));
				g2d.setStroke(new BasicStroke(2));
				g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
			}
		};
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(400, 280));
		return panel;
	}

	/**
	 * スタイル付きボタンを作成します。
	 *
	 * @param text  ボタンテキスト
	 * @param color ボタンの背景色
	 * @return スタイル付きボタン
	 */
	private JButton createStyledButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setBackground(color);
		button.setPreferredSize(new Dimension(100, 35));
		button.setFocusPainted(false);
		button.setBorder(new CompoundBorder(
				new LineBorder(color.darker(), 1),
				new EmptyBorder(5, 15, 5, 15)
		));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// ホバーエフェクト
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				button.setBackground(color.brighter());
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				button.setBackground(color);
			}
		});

		return button;
	}

	/**
	 * 半透明のオーバーレイ背景を描画します。
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	/**
	 * 開始ボタンクリック時の処理です。
	 */
	private void onStartClicked() {
		String userName = userNameField.getText().trim();
		if (userName.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"ユーザー名を入力してください。",
					"入力エラー",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		Integer selectedSize = (Integer) boardSizeComboBox.getSelectedItem();
		int boardSize = selectedSize != null ? selectedSize : 8;
		gui.startGame(userName, boardSize);
	}

	/**
	 * キャンセルボタンクリック時の処理です。
	 */
	private void onCancelClicked() {
		gui.hideMatchingPanel();
	}

	/**
	 * パネル表示時に入力フィールドをリセットします。
	 */
	public void reset() {
		userNameField.setText("");
		boardSizeComboBox.setSelectedItem(8);
		userNameField.requestFocusInWindow();
	}

}
