package server;

import common.*;
import model.*;

class GameRoom {
	private static int roomIdCounter = 0;

	private final int roomId;
	private final Board board;
	private final ClientHandler player1;  // 白
	private final ClientHandler player2;  // 黒
	private Piece currentTurn;

	public GameRoom(ClientHandler player1, ClientHandler player2, int boardSize) {
		this.roomId = roomIdCounter++;
		this.board = new Board(boardSize);
		this.currentTurn = Piece.WHITE;

		// player1
		this.player1 = player1;
		player1.setGameRoom(this);

		// player2
		this.player2 = player2;
		player2.setGameRoom(this);

		startGame();
	}

	private void startGame() {
		player1.sendMessage(Protocol.gameStart(Piece.WHITE));
		player2.sendMessage(Protocol.gameStart(Piece.BLACK));

		player1.sendMessage(Protocol.yourTurn());
		player2.sendMessage(Protocol.opponentTurn());

		System.out.println("ルーム " + roomId + " でゲーム開始");
	}

	public synchronized void processMove(int i, int j) {
		// オセロを置いて全体に知らせる
		board.applyMove(currentTurn, i, j);
		broadcastMessage(Protocol.moveAccepted(i, j));

		// ゲーム終了判定
		if (isGameOver()) endGame();

		// ターンを切り替える
		currentTurn = currentTurn == Piece.WHITE ? Piece.BLACK : Piece.WHITE;

		// 置けるかどうかチェック
		if (board.countValidMoves(currentTurn) > 0) {
			notifyTurnChange(); // 置ける
		} else {
			handlePass(); // パス処理
		}
	}

	public void handleResign(ClientHandler resigner) {
		ClientHandler opponent = resigner == player1 ? player2 : player1;

		int whiteCount = board.getStoneCount(Piece.WHITE);
		int blackCount = board.getStoneCount(Piece.BLACK);

		resigner.sendMessage(Protocol.gameLose(whiteCount, blackCount));

		opponent.sendMessage(Protocol.opponentResigned());
		opponent.sendMessage(Protocol.gameWin(whiteCount, blackCount));

		System.out.println("Room " + roomId + ": Player resigned");

		closeRoom();
	}

	public void handleDisconnect(ClientHandler player) {
		// 相手に勝利通知
		ClientHandler opponent = player == player1 ? player2 : player1;
		if (opponent != null) {
			opponent.sendMessage(Protocol.opponentDisconnected());
			int whiteCount = board.getStoneCount(Piece.WHITE);
			int blackCount = board.getStoneCount(Piece.BLACK);
			opponent.sendMessage(Protocol.gameWin(whiteCount, blackCount));
		}

		System.out.println("ルーム " + roomId + " でプレイヤー切断");
	}

	private void handlePass() {
		System.out.println(currentTurn + " は置ける場所がないためパス");
		broadcastMessage(Protocol.pass(currentTurn));

		// ターンを切り替える
		currentTurn = currentTurn == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
		notifyTurnChange();
	}

	private void notifyTurnChange() {
		if (currentTurn == Piece.WHITE) {
			player1.sendMessage(Protocol.yourTurn());
			player2.sendMessage(Protocol.opponentTurn());
		} else {
			player1.sendMessage(Protocol.opponentTurn());
			player2.sendMessage(Protocol.yourTurn());
		}
	}

	private boolean isGameOver() {
		return board.countValidMoves(Piece.WHITE) == 0 && board.countValidMoves(Piece.BLACK) == 0;
	}

	private void endGame() {
		notifyResult();
		closeRoom();
	}

	private void notifyResult() {
		int whiteCount = board.getStoneCount(Piece.WHITE);
		int blackCount = board.getStoneCount(Piece.BLACK);

		String result;
		if (whiteCount > blackCount) {
			player1.sendMessage(Protocol.gameWin(whiteCount, blackCount));
			player2.sendMessage(Protocol.gameLose(whiteCount, blackCount));
			result = "白の勝利";
		} else if (whiteCount < blackCount) {
			player1.sendMessage(Protocol.gameLose(whiteCount, blackCount));
			player2.sendMessage(Protocol.gameWin(whiteCount, blackCount));
			result = "黒の勝利";
		} else {
			player1.sendMessage(Protocol.gameDraw(whiteCount, blackCount));
			player2.sendMessage(Protocol.gameDraw(whiteCount, blackCount));
			result = "引き分け";
		}

		System.out.println("ルーム " + roomId + " でゲーム終了: " + result);
	}

	private void closeRoom() {
		player1.close();
		player2.close();
	}

	private void broadcastMessage(String message) {
		if (player1 != null) player1.sendMessage(message);
		if (player2 != null) player2.sendMessage(message);
	}
}
