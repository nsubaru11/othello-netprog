import View.*;

import javax.swing.*;

/**
 * オセロゲームアプリケーションのエントリポイントとなるクラスです。
 */
public final class Main {

	/**
	 * アプリケーションのメインメソッドです。
	 * GUIの作成と表示をイベントディスパッチスレッドで行います。
	 */
	public static void main(final String[] args) {
		// GUIの構築はイベントディスパッチスレッドで行う（専用スレッドで安全に実行）
		SwingUtilities.invokeLater(OthelloGUI::new);
	}
}
