package server;

import common.*;
import model.*;

public class GameRoom {
	private static int roomIdCounter = 0;

	private final int roomId;
	private final Board board;
	private final ClientHandler player1;  // 黒
	private final ClientHandler player2;  // 白
	private Piece currentTurn;

	public GameRoom(ClientHandler player1, ClientHandler player2, int boardSize) {
		this.roomId = roomIdCounter++;
		this.board = new Board(boardSize);
		this.currentTurn = Piece.BLACK;

		// player1
		this.player1 = player1;
		player1.setGameRoom(this);

		// player2
		this.player2 = player2;
		player2.setGameRoom(this);

		startGame();
	}

	private void startGame() {
		player1.sendMessage(Protocol.gameStart(Piece.BLACK));
		player2.sendMessage(Protocol.gameStart(Piece.WHITE));

		player1.sendMessage(Protocol.yourTurn());
		player2.sendMessage(Protocol.opponentTurn());

		System.out.println("ルーム " + roomId + " でゲーム開始");
	}

	public synchronized void processMove(int i, int j) {
		// オセロを置いて全体に知らせる
		board.setPiece(currentTurn, i, j);
		broadcastMessage(Protocol.moveAccepted(i, j));

		// ゲーム終了判定
		if (isGameOver()) endGame();

		// ターンを切り替える
		currentTurn = currentTurn == Piece.BLACK ? Piece.WHITE : Piece.BLACK;

		// 置けるかどうかチェック
		if (board.countValidCells(currentTurn) > 0) {
			notifyTurnChange(); // 置ける
		} else {
			handlePass(); // パス処理
		}
	}

	private void handlePass() {
		System.out.println(currentTurn + " は置ける場所がないためパス");
		broadcastMessage(Protocol.pass(currentTurn));

		// ターンを切り替える
		currentTurn = currentTurn == Piece.BLACK ? Piece.WHITE : Piece.BLACK;
		notifyTurnChange();
	}

	private void notifyTurnChange() {
		if (currentTurn == Piece.BLACK) {
			player1.sendMessage(Protocol.yourTurn());
			player2.sendMessage(Protocol.opponentTurn());
		} else {
			player1.sendMessage(Protocol.opponentTurn());
			player2.sendMessage(Protocol.yourTurn());
		}
	}

	private boolean isGameOver() {
		return board.countValidCells(Piece.BLACK) == 0 && board.countValidCells(Piece.WHITE) == 0;
	}

	private void endGame() {
		notifyResult();
		player1.close();
		player2.close();
	}

	private void notifyResult() {
		int blackCount = board.getStoneCount(Piece.BLACK);
		int whiteCount = board.getStoneCount(Piece.WHITE);

		String result;
		if (blackCount > whiteCount) {
			player1.sendMessage(Protocol.gameWin(blackCount, whiteCount));
			player2.sendMessage(Protocol.gameLose(blackCount, whiteCount));
			result = "黒の勝利";
		} else if (whiteCount > blackCount) {
			player1.sendMessage(Protocol.gameLose(blackCount, whiteCount));
			player2.sendMessage(Protocol.gameWin(blackCount, whiteCount));
			result = "白の勝利";
		} else {
			player1.sendMessage(Protocol.gameDraw(blackCount, whiteCount));
			player2.sendMessage(Protocol.gameDraw(blackCount, whiteCount));
			result = "引き分け";
		}

		System.out.println("ルーム " + roomId + " でゲーム終了: " + result);
	}

	public void handleResign(ClientHandler resigner) {
		ClientHandler opponent = resigner == player1 ? player2 : player1;

		int blackCount = board.getStoneCount(Piece.BLACK);
		int whiteCount = board.getStoneCount(Piece.WHITE);

		resigner.sendMessage(Protocol.gameLose(blackCount, whiteCount));

		opponent.sendMessage(Protocol.opponentResigned());
		opponent.sendMessage(Protocol.gameWin(blackCount, whiteCount));

		System.out.println("Room " + roomId + ": Player resigned");
		endGame();
	}

	public void handleDisconnect(ClientHandler player) {
		// 相手に勝利通知
		ClientHandler opponent = player == player1 ? player2 : player1;
		if (opponent != null) {
			opponent.sendMessage(Protocol.opponentDisconnected());
			int blackCount = board.getStoneCount(Piece.BLACK);
			int whiteCount = board.getStoneCount(Piece.WHITE);
			opponent.sendMessage(Protocol.gameWin(blackCount, whiteCount));
		}

		System.out.println("ルーム " + roomId + " でプレイヤー切断");
	}

	private void broadcastMessage(String message) {
		if (player1 != null) player1.sendMessage(message);
		if (player2 != null) player2.sendMessage(message);
	}
}
