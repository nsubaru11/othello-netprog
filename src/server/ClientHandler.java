package server;

import model.*;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
	private final Socket socket;
	private final PrintWriter out;
	private final BufferedReader in;
	private final OthelloServer server;
	private GameRoom gameRoom;
	private String playerName;

	public ClientHandler(Socket socket, OthelloServer server) {
		this.socket = socket;
		this.server = server;

		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			// 最初のメッセージでプレイヤー名を取得
			String[] firstLine = in.readLine().split(" ");
			if (firstLine[0].equals("CONNECT")) {
				playerName = firstLine[1];
				int boardSize = Integer.parseInt(firstLine[2]);
				System.out.println("Player connected: " + playerName);

				// マッチング待ちキューに追加
				server.addWaitingPlayer(boardSize, this);
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
			handleDisconnect();
		}
	}

	private void handleMessage(String message) {
		String[] tokens = message.split(" ");
		String command = tokens[0];

		switch (command) {
			case "MOVE":
				int row = Integer.parseInt(tokens[1]);
				int col = Integer.parseInt(tokens[2]);
				gameRoom.processMove(row, col);
				break;

			case "RESIGN":
				gameRoom.handleDisconnect(this);
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

	public String getPlayerName() {
		return playerName;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleDisconnect() {
		if (gameRoom != null) gameRoom.handleDisconnect(this);
		else server.disconnectPlayer(this);
		System.out.println("Player disconnected: " + playerName);
		close();
	}
}
