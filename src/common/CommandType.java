package common;

public enum CommandType {
	MOVE("MOVE"),
	PASS("PASS"),
	RESIGN("RESIGN"),
	CONNECT("CONNECT"),
	GAME_START("GAME_START"),
	MOVE_ACCEPTED("MOVE_ACCEPTED"),
	YOUR_TURN("YOUR_TURN"),
	OPPONENT_TURN("OPPONENT_TURN"),
	GAME_OVER("GAME_OVER"),
	OPPONENT_RESIGNED("OPPONENT_RESIGNED"),
	OPPONENT_DISCONNECTED("OPPONENT_DISCONNECTED"),
	ERROR("ERROR"),
	UNKNOWN("UNKNOWN");

	private final String command;

	CommandType(String command) {
		this.command = command;
	}

	public static CommandType fromToken(String text) {
		if (text == null) return UNKNOWN;
		for (CommandType type : values()) {
			if (type.command.equals(text)) return type;
		}
		return UNKNOWN;
	}

	public String command() {
		return command;
	}
}
