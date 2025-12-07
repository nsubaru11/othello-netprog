package client.view;

import client.controller.*;
import model.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * オセロゲームのメインウィンドウを管理するクラスです。
 * CardLayoutを使用して画面の切り替えを行います。
 */
public class OthelloGUI extends JFrame {
	// --------------- クラス定数定義 ---------------
	/** アプリケーションアイコンの画像 */
	private static final Image ICON_IMAGE;
	/** アプリケーションアイコンの画像パス */
	private static final String ICON_IMAGE_PATH = "../assets/icon.png";

	static {
		// iconは読み込み失敗してもアプリ動作には影響しないため、ログ出力のみで続行
		Image image = null;
		try {
			URL imageUrl = OthelloGUI.class.getResource(ICON_IMAGE_PATH);
			image = ImageIO.read(Objects.requireNonNull(imageUrl, "Icon image not found: " + ICON_IMAGE_PATH));
		} catch (final IOException e) {
			System.err.println("Failed to load icon image file due to I/O error.");
		} catch (final NullPointerException e) {
			System.err.println(e.getMessage());
		}
		ICON_IMAGE = image;
	}

	// CardLayout用のパネル識別文字列
	/** ロード画面の識別子 */
	private static final String CARD_LOAD = "load";
	/** ホーム画面の識別子 */
	private static final String CARD_HOME = "home";
	/** ゲーム画面の識別子 */
	private static final String CARD_GAME = "game";
	/** 結果画面の識別子 */
	private static final String CARD_RESULT = "result";

	// --------------- フィールド ---------------
	/** 画面切り替えレイアウトマネージャ */
	private final CardLayout cardLayout;
	/** 各画面パネルを保持する親パネル */
	private final JPanel cardPanel;
	/** ロード画面パネル */
	private final LoadPanel loadPanel;
	/** ホーム画面パネル */
	private final HomePanel homePanel;
	/** 結果画面パネル */
	private final ResultPanel resultPanel;
	/** マッチング設定パネル */
	private final MatchingPanel matchingPanel;
	/** ゲーム画面パネル */
	private GamePanel gamePanel;
	/** ゲームコントローラー */
	private GameController controller;

	/**
	 * OthelloGUIを構築し、メインウィンドウを初期化します。
	 * 画面サイズの設定、レイアウトの構築、各パネルの生成と配置を行います。
	 */
	public OthelloGUI() {
		// フレームの基本設定
		setTitle("Othello");
		if (ICON_IMAGE != null) setIconImage(ICON_IMAGE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 画面サイズの設定（デフォルト）
		// 短い辺の60%を幅、80%を高さにする
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double size = Math.min(screenSize.getWidth(), screenSize.getHeight());
		screenSize.setSize(size * 0.6, size * 0.8);
		setSize(screenSize);
		setLocationRelativeTo(null);

		// レイアウトとパネルの初期化
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		loadPanel = new LoadPanel(this);
		homePanel = new HomePanel(this);
		resultPanel = new ResultPanel(this);
		matchingPanel = new MatchingPanel(this);

		// CardLayoutにパネルを追加
		cardPanel.add(loadPanel, CARD_LOAD);
		cardPanel.add(homePanel, CARD_HOME);
		cardPanel.add(resultPanel, CARD_RESULT);
		add(cardPanel);

		// ウィンドウを表示し、ロード画面を開始する
		setVisible(true);
		showLoad();
	}

	public GameController getController() {
		return controller;
	}

	/**
	 * ロード画面を表示し、プログレスバーを開始します。
	 */
	public void showLoad() {
		cardLayout.show(cardPanel, CARD_LOAD);
		loadPanel.startProgress();
	}

	/**
	 * ホーム画面を表示します。
	 */
	public void showHome() {
		cardLayout.show(cardPanel, CARD_HOME);
	}

	/**
	 * ゲーム画面を表示します。
	 */
	public void showGame() {
		cardLayout.show(cardPanel, CARD_GAME);
	}

	public void showMessage(String text) {
		gamePanel.showMessage(text);
	}

	/**
	 * 結果画面を表示します。
	 *
	 * @param blackCount 黒の駒数
	 * @param whiteCount 白の駒数
	 */
	public void showResult(String result, int blackCount, int whiteCount) {
		resultPanel.setResult(result, blackCount, whiteCount);
		cardLayout.show(cardPanel, CARD_RESULT);
	}

	/**
	 * マッチング設定パネルをオーバーレイ表示します。
	 */
	public void showMatchingPanel() {
		matchingPanel.reset();
		matchingPanel.setBounds(0, 0, getWidth(), getHeight());
		getLayeredPane().add(matchingPanel, JLayeredPane.POPUP_LAYER);
		matchingPanel.setVisible(true);
		matchingPanel.revalidate();
		matchingPanel.repaint();
	}

	/**
	 * マッチング設定パネルを非表示にします。
	 */
	public void hideMatchingPanel() {
		getLayeredPane().remove(matchingPanel);
		matchingPanel.setVisible(false);
		repaint();
	}

	public void setPiece(Piece piece, int row, int col) {
		gamePanel.setPiece(piece, row, col);
	}

	/**
	 * ゲームを開始します。
	 *
	 * @param userName  ユーザー名
	 * @param boardSize ボードサイズ
	 * @return ゲーム開始に成功したかどうか
	 */
	public boolean startGame(String userName, int boardSize) {
		hideMatchingPanel();
		controller = new GameController(this, userName, boardSize);
		gamePanel = new GamePanel(this, controller, boardSize);
		cardPanel.add(gamePanel, CARD_GAME);
		boolean connect = controller.connect();
		if (!connect) return false;
		showGame();
		return true;
	}
}
