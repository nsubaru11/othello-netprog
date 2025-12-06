package server;

import model.*;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String playerName;
	private Piece playerColor;
	private GameRoom gameRoom;
	private OthelloServer server;

	public ClientHandler(Socket socket, OthelloServer server) {
		this.socket = socket;
		this.server = server;

		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			// 最初のメッセージでプレイヤー名を取得
			String firstLine = in.readLine();
			if (firstLine != null && firstLine.startsWith("CONNECT ")) {
				playerName = firstLine.substring(8);
				System.out.println("Player connected: " + playerName);

				// マッチング待ちキューに追加
				server.addWaitingPlayer(8, this);
			}

			// メッセージ受信ループ
			while (true) {
				String line = in.readLine();
				if (line == null) break;

				System.out.println("From " + playerName + ": " + line);
				handleMessage(line);
			}
		} catch (IOException e) {
			System.out.println("Connection error with " + playerName);
		} finally {
			cleanup();
		}
	}

	private void handleMessage(String message) {
		String[] tokens = message.split(" ");
		String command = tokens[0];

		switch (command) {
			case "MOVE":
				int row = Integer.parseInt(tokens[1]);
				int col = Integer.parseInt(tokens[2]);
				if (gameRoom != null) {
					gameRoom.processMove(this, row, col);
				}
				break;

			case "RESIGN":
				if (gameRoom != null) {
					gameRoom.handleDisconnect(this);
				}
				break;

			default:
				System.out.println("Unknown command: " + command);
		}
	}

	public void sendMessage(String msg) {
		out.println(msg);
		out.flush();
	}

	public void setGameRoom(GameRoom room) {
		this.gameRoom = room;
	}

	public Piece getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(Piece color) {
		this.playerColor = color;
	}

	public String getPlayerName() {
		return playerName;
	}

	private void cleanup() {
		if (gameRoom != null) {
			gameRoom.handleDisconnect(this);
		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Player disconnected: " + playerName);
	}
}