package client.controller;

import client.view.*;
import model.*;

import javax.swing.*;
import java.util.*;

/**
 * ゲームの進行およびボードの状態を管理します。
 */
public class GameController implements NetworkListener {
	// --------------- フィールド ---------------
	private final OthelloGUI gui;
	private final NetworkController networkController;
	private final int boardSize;
	private final String playerName;
	private final Board board;
	private Piece myColor;
	private Piece currentTurn;

	// --------------- OthelloGUUIから呼ばれるメソッド及びコンストラクタ ---------------

	/**
	 * GameControllerを構築します。
	 *
	 * @param gui        OthelloGUIインスタンス
	 * @param playerName ユーザー名
	 * @param boardSize  ボードサイズ
	 */
	public GameController(OthelloGUI gui, String playerName, int boardSize) {
		this.gui = gui;
		this.playerName = playerName;
		this.boardSize = boardSize;
		this.board = new Board(boardSize);
		networkController = new NetworkController(this);
	}

	public boolean connect() {
		return networkController.connect(playerName, boardSize);
	}

	public void setPiece(int i, int j) {
		// 選択したマスに駒が置ける場合NetworkControllerに伝達
		if (myColor == null) return;
		if (currentTurn != myColor) return;
		if (!board.isValidMove(currentTurn, i, j)) return;
		networkController.sendMove(i, j);
		System.out.println("手を送信: (" + i + ", " + j + ")");
	}

	public void giveUp() {
		networkController.sendResign();
	}

	// --------------- NetworkListenerの実装 ---------------
	@Override
	public void onGameStart(Piece assignedColor) {
		this.myColor = assignedColor;
		this.currentTurn = Piece.WHITE;
		System.out.println("ゲーム開始！あなたは " + myColor);
		SwingUtilities.invokeLater(() -> gui.showMessage("Game started! You are " + myColor));
	}

	@Override
	public void onYourTurn() {
		this.currentTurn = myColor;
		System.out.println("あなたのターン！");
		SwingUtilities.invokeLater(() -> gui.showMessage("Your turn! Your color is " + myColor));
		// 自分のターンの場合、駒を置けるマスを表示する
		for (int index : board.getValidMoves(myColor).keySet()) {
			int i = index / boardSize;
			int j = index % boardSize;
			SwingUtilities.invokeLater(() -> gui.setValidPiece(myColor, i, j));
		}
	}

	@Override
	public void onOpponentTurn() {
		this.currentTurn = myColor == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
		System.out.println("相手のターン");
		SwingUtilities.invokeLater(() -> gui.showMessage("Opponent's turn"));
	}

	@Override
	public void onMoveAccepted(int i, int j) {
		// 自分または相手が駒を正しく置いたときに呼ばれる
		System.out.println("手が受理されました: (" + i + ", " + j + ")");
		resetValidMoves(board.getValidMoves(myColor).keySet());
		List<Integer> changedCells = board.applyMove(currentTurn, i, j);
		updateCells(currentTurn, changedCells);
	}

	@Override
	public void onGameOver(String result, int whiteCount, int blackCount) {
		System.out.println(result + " " + whiteCount + " - " + blackCount);
		SwingUtilities.invokeLater(() -> gui.showResult(result, whiteCount, blackCount));
	}

	@Override
	public void onOpponentResigned() {
		System.out.println("相手が降参しました。");
		SwingUtilities.invokeLater(() -> gui.showMessage("Opponent resigned."));
	}

	@Override
	public void onNetworkError(String message) {
		System.err.println("ネットワークエラー: " + message);
		SwingUtilities.invokeLater(() -> gui.showMessage("Network error: " + message));
	}

	private void resetValidMoves(Set<Integer> changedCells) {
		for (int index : changedCells) {
			int i = index / boardSize;
			int j = index % boardSize;
			SwingUtilities.invokeLater(() -> gui.setPiece(Piece.EMPTY, i, j));
		}
	}

	private void updateCells(Piece piece, List<Integer> validMoves) {
		for (int index : validMoves) {
			int i = index / boardSize;
			int j = index % boardSize;
			SwingUtilities.invokeLater(() -> gui.setPiece(piece, i, j));
		}
	}

}
