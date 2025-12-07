package server;

import common.*;

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
			if (CommandType.fromToken(firstLine[0]) == CommandType.CONNECT) {
				playerName = firstLine[1];
				int boardSize = Integer.parseInt(firstLine[2]);
				System.out.println("プレイヤー接続: " + playerName);

				// マッチング待ちキューに追加
				server.addWaitingPlayer(boardSize, this);
			}

			// メッセージ受信ループ
			while (true) {
				String line = in.readLine();
				if (line == null) break;
				System.out.println(playerName + " からの受信: " + line);
				handleMessage(line);
			}
		} catch (IOException e) {
			System.out.println(playerName + " との接続エラー");
		} finally {
			handleDisconnect();
		}
	}

	private void handleMessage(String message) {
		String[] tokens = message.split(" ");
		CommandType command = CommandType.fromToken(tokens[0]);

		switch (command) {
			case MOVE:
				int i = Integer.parseInt(tokens[1]);
				int j = Integer.parseInt(tokens[2]);
				gameRoom.processMove(i, j);
				break;

			case RESIGN:
				gameRoom.handleResign(this);
				break;

			default:
				System.out.println("不明なコマンド: " + tokens[0]);
		}
	}

	public void sendMessage(String message) {
		out.println(message);
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
		System.out.println("プレイヤー切断: " + playerName);
		close();
	}
}
