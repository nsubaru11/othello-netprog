package Controller;

import Model.Board;
import Model.Piece;
import Network.NetworkController;

import java.util.*;

public class GameController {
	private final Board board;
	private Piece myColor;
	private NetworkController networkController;
	private Piece currentTurn;
	private Map<Integer, List<Integer>> validCells;

	public GameController(int boardSize, String host, int port, String playerName) {
		this.board = new Board(boardSize);
		networkController = new NetworkController(this);
		boolean connected = networkController.connect(host, port, playerName, boardSize);
		if (!connected) throw new RuntimeException("Failed to connect to server");

		// サーバーからの色割り当て待ち
		// myColor は onGameStart() で設定される
	}

	public void setPiece(int row, int col) {
		if (currentTurn != myColor) return;
		networkController.sendMove(row, col);
	}

	private void updateValidCells() {
		validCells = board.getValidCells(currentTurn);
	}

	public boolean isMyTurn() {
		return currentTurn == myColor;
	}

	public boolean canMove() {
		return !validCells.isEmpty();
	}

	public boolean isGameOver() {
		return board.getValidCells(Piece.BLACK).isEmpty()
				&& board.getValidCells(Piece.WHITE).isEmpty();
	}

	public Piece getCurrent() {
		return currentTurn;
	}

	public Set<Integer> getValidCells() {
		return validCells.keySet();
	}

	public int getStoneCount(Piece piece) {
		return board.getStoneCount(piece);
	}

	public Piece getWinner() {
		int blackCount = board.getStoneCount(Piece.BLACK);
		int whiteCount = board.getStoneCount(Piece.WHITE);
		if (blackCount == whiteCount) return null;
		return blackCount > whiteCount ? Piece.BLACK : Piece.WHITE;
	}

	public void onGameStart(Piece assignedColor) {
		this.myColor = assignedColor;
		this.currentTurn = Piece.BLACK;
		updateValidCells();
		System.out.println("Game started! You are " + myColor);
	}

	public void onYourTurn() {
		this.currentTurn = myColor;
		updateValidCells();
		System.out.println("Your turn!");
	}

	public void onOpponentTurn() {
		this.currentTurn = (myColor == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;
		updateValidCells();
		System.out.println("Opponent's turn");
	}

	public void onMoveAccepted(int row, int col) {
		System.out.println("Move accepted: (" + row + ", " + col + ")");
	}

	public void onBoardUpdate(int row, int col, Piece piece, List<Integer> flipped) {
		board.setPiece(piece, row, col);
		updateValidCells();
		System.out.println("Board updated");
	}

	public void onGameOver(String result) {
		System.out.println("Game over: " + result);
	}

	public void onNetworkError(String message) {
		System.err.println("Network error: " + message);
	}

}