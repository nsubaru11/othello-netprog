package View;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * オセロゲームのメインウィンドウを管理するクラス。
 * CardLayoutを使用して画面の切り替えを行います。
 */
public class OthelloGUI extends JFrame {
	// --------------- クラス定数定義 ---------------
	/** アプリケーションアイコンの画像 */
	private static final Image iconImage;
	/** アプリケーションアイコンの画像パス */
	private static final String iconImagePath = "../Assets/icon.png";
	static {
		// iconは読み込み失敗してもアプリ動作には影響しないため、ログ出力のみで続行
		Image image = null;
		try {
			URL imageUrl = OthelloGUI.class.getResource(iconImagePath);
			image = ImageIO.read(Objects.requireNonNull(imageUrl, "Icon image not found: " + iconImagePath));
		} catch (final IOException e) {
			System.err.println("Failed to load icon image file due to I/O error.");
		} catch (final NullPointerException e) {
			System.err.println(e.getMessage());
		}
		iconImage = image;
	}
	// CardLayout用のパネル識別文字列
	/** ロード画面の識別子 */
	private static final String CARD_LOAD = "load";
	/** ホーム画面の識別子 */
	private static final String CARD_HOME = "home";
	/** ゲーム画面の識別子 */
	private static final String CARD_GAME = "game";

	// --------------- フィールド ---------------
	/** 画面切り替えレイアウトマネージャ */
	private final CardLayout cardLayout;
	/** 各画面パネルを保持する親パネル */
	private final JPanel cardPanel;
	/** ロード画面パネル */
	private final LoadPanel loadPanel;
	/** ホーム画面パネル */
	private final HomePanel homePanel;
	/** ゲーム画面パネル */
	private final GamePanel gamePanel;

	/**
	 * OthelloGUIを構築し、メインウィンドウを初期化します。
	 * 画面サイズの設定、レイアウトの構築、各パネルの生成と配置を行います。
	 */
	public OthelloGUI() {
		// フレームの基本設定
		setTitle("Othello");
		if (iconImage != null) setIconImage(iconImage);
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
		gamePanel = new GamePanel(this, 8);

		// CardLayoutにパネルを追加
		cardPanel.add(loadPanel, CARD_LOAD);
		cardPanel.add(homePanel, CARD_HOME);
		cardPanel.add(gamePanel, CARD_GAME);
		add(cardPanel);

		// ウィンドウを表示し、ロード画面を開始する
		setVisible(true);
		showLoad();
	}

	/**
	 * ロード画面の表示 + プログレッスバーの開始
	 */
	public void showLoad() {
		cardLayout.show(cardPanel, CARD_LOAD);
		loadPanel.startProgress();
	}

	/**
	 * ホーム画面の表示
	 */
	public void showHome() {
		cardLayout.show(cardPanel, CARD_HOME);
	}

	/**
	 * ゲーム画面の表示
	 */
	public void showGame() {
		cardLayout.show(cardPanel, CARD_GAME);
	}
}
