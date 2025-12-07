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

	public void setPiece(int row, int col) {
		if (currentTurn != myColor) return;
		if (!board.canSet(currentTurn, row, col)) return;
		networkController.sendMove(row, col);
		System.out.println("Move sent: (" + row + ", " + col + ")");
	}

	public void giveUp() {
		networkController.sendResign();
	}

	public void onGameStart(Piece assignedColor) {
		this.myColor = assignedColor;
		this.currentTurn = Piece.BLACK;
		System.out.println("Game started! You are " + myColor);
		SwingUtilities.invokeLater(() -> gui.showMessage("Game started! You are " + myColor));
	}

	public void onYourTurn() {
		this.currentTurn = myColor;
		System.out.println("Your turn!");
		SwingUtilities.invokeLater(() -> gui.showMessage("Your turn! Your color is " + myColor));
	}

	public void onOpponentTurn() {
		this.currentTurn = myColor == Piece.BLACK ? Piece.WHITE : Piece.BLACK;
		System.out.println("Opponent's turn");
		SwingUtilities.invokeLater(() -> gui.showMessage("Opponent's turn"));
	}

	public void onMoveAccepted(int row, int col) {
		System.out.println("Move accepted: (" + row + ", " + col + ")");
		placePieces(row, col);
		List<Integer> validCells = board.getValidCells(currentTurn).get(row * boardSize + col);
		for (int cell : validCells) {
			int ni = cell / boardSize;
			int nj = cell % boardSize;
			placePieces(ni, nj);
		}
		board.updateValidMoves();
	}

	public void onGameOver(String result, int blackCount, int whiteCount) {
		System.out.println(result + " " + blackCount + " - " + whiteCount);
		SwingUtilities.invokeLater(() -> gui.showResult(result, blackCount, whiteCount));
	}

	public void onNetworkError(String message) {
		System.err.println("Network error: " + message);
		SwingUtilities.invokeLater(() -> gui.showMessage("Network error: " + message));
	}

	private void placePieces(int row, int col) {
		if (currentTurn.isBlack()) {
			board.placeBlack(row, col);
			SwingUtilities.invokeLater(() -> gui.setPiece(currentTurn, row, col));
		} else {
			board.placeWhite(row, col);
			SwingUtilities.invokeLater(() -> gui.setPiece(currentTurn, row, col));
		}
	}

}
