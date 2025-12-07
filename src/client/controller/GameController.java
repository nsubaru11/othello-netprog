package client.controller;

import client.view.*;
import model.*;

import javax.swing.*;
import java.util.*;

public class GameController implements NetworkListener {
	private final OthelloGUI gui;
	private final NetworkController networkController;
	private final int boardSize;
	private final String playerName;
	private final Board board;
	private Piece myColor;
	private Piece currentTurn;

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
		if (currentTurn != myColor) return;
		if (!board.canSet(currentTurn, i, j)) return;
		networkController.sendMove(i, j);
		System.out.println("手を送信: (" + i + ", " + j + ")");
	}

	public void giveUp() {
		networkController.sendResign();
	}

	public void onGameStart(Piece assignedColor) {
		this.myColor = assignedColor;
		this.currentTurn = Piece.BLACK;
		System.out.println("ゲーム開始！あなたは " + myColor);
		SwingUtilities.invokeLater(() -> gui.showMessage("Game started！ You are " + myColor));
	}

	public void onYourTurn() {
		this.currentTurn = myColor;
		System.out.println("あなたのターン！");
		SwingUtilities.invokeLater(() -> gui.showMessage("Your turn！ Your color is " + myColor));
	}

	public void onOpponentTurn() {
		this.currentTurn = myColor == Piece.BLACK ? Piece.WHITE : Piece.BLACK;
		System.out.println("相手のターン");
		SwingUtilities.invokeLater(() -> gui.showMessage("Opponent's turn"));
	}

	public void onMoveAccepted(int i, int j) {
		System.out.println("手が受理されました: (" + i + ", " + j + ")");
		placePiece(i, j);
		List<Integer> validCells = board.getValidCells(currentTurn).get(i * boardSize + j);
		for (int cell : validCells) {
			int ni = cell / boardSize;
			int nj = cell % boardSize;
			placePiece(ni, nj);
		}
		board.updateValidMoves();
	}

	public void onGameOver(String result, int blackCount, int whiteCount) {
		System.out.println(result + " " + blackCount + " - " + whiteCount);
		SwingUtilities.invokeLater(() -> gui.showResult(result, blackCount, whiteCount));
	}

	public void onNetworkError(String message) {
		System.err.println("ネットワークエラー: " + message);
		SwingUtilities.invokeLater(() -> gui.showMessage("Network error: " + message));
	}

	private void placePiece(int i, int j) {
		// この代入式を消すとバグる（SwingUtilitiesは処理を後回しにするから、先に通信が終わってcurrentTurnが更新されてしまう。）
		Piece piece = currentTurn;
		if (piece.isBlack()) {
			board.placeBlack(i, j);
			SwingUtilities.invokeLater(() -> gui.setPiece(piece, i, j));
		} else {
			board.placeWhite(i, j);
			SwingUtilities.invokeLater(() -> gui.setPiece(piece, i, j));
		}
	}

}
