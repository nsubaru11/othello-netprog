package common;

import model.*;

public final class Protocol {
	private Protocol() {
	}

	public static String move(int i, int j) {
		return CommandType.MOVE.command() + " " + i + " " + j;
	}

	public static String gameStart(Piece color) {
		return CommandType.GAME_START.command() + " " + color;
	}

	public static String pass(Piece color) {
		return CommandType.PASS.command() + " " + color;
	}

	public static String moveAccepted(int i, int j) {
		return CommandType.MOVE_ACCEPTED.command() + " " + i + " " + j;
	}

	public static String connect(String playerName, int boardSize) {
		return CommandType.CONNECT.command() + " " + playerName + " " + boardSize;
	}

	public static String resign() {
		return CommandType.RESIGN.command();
	}

	public static String opponentTurn() {
		return CommandType.OPPONENT_TURN.command();
	}

	public static String yourTurn() {
		return CommandType.YOUR_TURN.command();
	}

	public static String gameWin(int blackCount, int whiteCount) {
		return formatResult("WIN", blackCount, whiteCount);
	}

	public static String gameLose(int blackCount, int whiteCount) {
		return formatResult("LOSE", blackCount, whiteCount);
	}

	public static String gameDraw(int blackCount, int whiteCount) {
		return formatResult("DRAW", blackCount, whiteCount);
	}

	public static String opponentResigned() {
		return CommandType.OPPONENT_RESIGNED.command();
	}

	public static String opponentDisconnected() {
		return CommandType.OPPONENT_DISCONNECTED.command();
	}

	private static String formatResult(String result, int blackCount, int whiteCount) {
		return CommandType.GAME_OVER.command() + " " + result + " " + blackCount + " " + whiteCount;
	}
}
