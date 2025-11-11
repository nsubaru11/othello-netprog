package Network;

import Model.Board;
import Model.Piece;

import java.util.List;
import java.util.Map;

public class GameRoom {
	private static int roomIdCounter = 0;

	private final int roomId;
	private ClientHandler player1;  // 黒
	private ClientHandler player2;  // 白
	private Board board;
	private Piece currentTurn;
	private boolean gameStarted = false;

	public GameRoom(int boardSize) {
		this.roomId = roomIdCounter++;
		this.board = new Board(boardSize);
		this.currentTurn = Piece.BLACK;
	}

	public boolean addPlayer(ClientHandler player) {
		if (player1 == null) {
			player1 = player;
			player.setGameRoom(this);
			player.setPlayerColor(Piece.BLACK);
			player.sendMessage("WAITING");
			return true;
		} else if (player2 == null) {
			player2 = player;
			player.setGameRoom(this);
			player.setPlayerColor(Piece.WHITE);
			startGame();
			return true;
		}
		return false; // 部屋が満員
	}

	private void startGame() {
		gameStarted = true;

		// 両プレイヤーにゲーム開始通知
		player1.sendMessage("GAME_START BLACK");
		player2.sendMessage("GAME_START WHITE");

		// 黒のターン通知
		player1.sendMessage("YOUR_TURN");
		player2.sendMessage("OPPONENT_TURN");

		System.out.println("Game started in room " + roomId);
	}

	public synchronized void processMove(ClientHandler player, int row, int col) {
		// 1. 手番確認
		Piece playerColor = player.getPlayerColor();
		if (playerColor != currentTurn) {
			player.sendMessage("ERROR Not your turn");
			return;
		}

		// 2. 有効手確認
		Map<Integer, List<Integer>> validMoves = board.getValidCells(playerColor);
		int posIndex = row * board.getSize() + col;

		if (!validMoves.containsKey(posIndex)) {
			player.sendMessage("ERROR Invalid move");
			return;
		}

		// 3. 駒を置く
		board.setPiece(playerColor, row, col);

		// 4. 両プレイヤーに通知
		List<Integer> flipped = validMoves.get(posIndex);
		String flippedStr = flipped.toString().replaceAll("[\\[\\],]", "");

		player.sendMessage("MOVE_ACCEPTED " + row + " " + col);
		broadcastMessage("BOARD_UPDATE " + row + " " + col + " " + playerColor + " " + flippedStr);

		// 5. ターン交代
		currentTurn = (currentTurn == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

		// 6. 次のプレイヤーが動けるか確認
		if (!board.getValidCells(currentTurn).isEmpty()) {
			// 動ける
			notifyTurnChange();
		} else {
			// パス処理
			handlePass();
		}

		// 7. ゲーム終了判定
		if (isGameOver()) {
			endGame();
		}
	}

	private void handlePass() {
		System.out.println(currentTurn + " has no valid moves, passing...");
		broadcastMessage("PASS " + currentTurn);

		// ターンを戻す
		currentTurn = (currentTurn == Piece.BLACK) ? Piece.WHITE : Piece.BLACK;

		// 相手も動けないかチェック
		if (board.getValidCells(currentTurn).isEmpty()) {
			endGame();
		} else {
			notifyTurnChange();
		}
	}

	private void notifyTurnChange() {
		if (currentTurn == Piece.BLACK) {
			player1.sendMessage("YOUR_TURN");
			player2.sendMessage("OPPONENT_TURN");
		} else {
			player2.sendMessage("YOUR_TURN");
			player1.sendMessage("OPPONENT_TURN");
		}
	}

	private boolean isGameOver() {
		return board.getValidCells(Piece.BLACK).isEmpty()
				&& board.getValidCells(Piece.WHITE).isEmpty();
	}

	private void endGame() {
		int blackCount = board.getStoneCount(Piece.BLACK);
		int whiteCount = board.getStoneCount(Piece.WHITE);

		String result;
		if (blackCount > whiteCount) {
			player1.sendMessage("GAME_OVER WIN");
			player2.sendMessage("GAME_OVER LOSE");
			result = "Black wins";
		} else if (whiteCount > blackCount) {
			player1.sendMessage("GAME_OVER LOSE");
			player2.sendMessage("GAME_OVER WIN");
			result = "White wins";
		} else {
			player1.sendMessage("GAME_OVER DRAW");
			player2.sendMessage("GAME_OVER DRAW");
			result = "Draw";
		}

		System.out.println("Game over in room " + roomId + ": " + result);
	}

	public void handleDisconnect(ClientHandler player) {
		if (!gameStarted) return;

		// 相手に勝利通知
		ClientHandler opponent = (player == player1) ? player2 : player1;
		if (opponent != null) {
			opponent.sendMessage("OPPONENT_DISCONNECTED");
			opponent.sendMessage("GAME_OVER WIN");
		}

		System.out.println("Player disconnected from room " + roomId);
	}

	private void broadcastMessage(String message) {
		if (player1 != null) player1.sendMessage(message);
		if (player2 != null) player2.sendMessage(message);
	}

	public int getRoomId() {
		return roomId;
	}

	public boolean isFull() {
		return player1 != null && player2 != null;
	}
}