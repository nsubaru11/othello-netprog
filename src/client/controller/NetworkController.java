package client.controller;

import model.*;

import java.io.*;
import java.net.*;

public class NetworkController {
	private static final int DEFAULT_PORT = 10000;
	private static final String DEFAULT_HOST = "localhost";
	private final NetworkListener networkListener;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	public NetworkController(NetworkListener listener) {
		this.networkListener = listener;
	}

	public boolean connect(String playerName, int boardSize) {
		try {
			socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("CONNECT " + playerName + " " + boardSize);
			out.flush();
			MessageReceiveThread receiveThread = new MessageReceiveThread();
			receiveThread.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void sendMove(int i, int j) {
		out.println("MOVE " + i + " " + j);
		out.flush();
	}

	public void sendResign() {
		out.println("RESIGN");
		out.flush();
	}

	public void disconnect() {
		try {
			if (socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(String message) {
		String[] tokens = message.split(" ");
		String command = tokens[0];

		switch (command) {
			case "GAME_START":
				// GAME_START BLACK または GAME_START WHITE
				Piece color = Piece.valueOf(tokens[1]);
				networkListener.onGameStart(color);
				break;

			case "YOUR_TURN":
				networkListener.onYourTurn();
				break;

			case "OPPONENT_TURN":
				networkListener.onOpponentTurn();
				break;

			case "MOVE_ACCEPTED":
				// MOVE_ACCEPTED i j
				int i = Integer.parseInt(tokens[1]);
				int j = Integer.parseInt(tokens[2]);
				networkListener.onMoveAccepted(i, j);
				break;

			case "GAME_OVER":
				// GAME_OVER WIN/LOSE/DRAW blackCount whiteCount
				int blackCount = Integer.parseInt(tokens[2]);
				int whiteCount = Integer.parseInt(tokens[3]);
				networkListener.onGameOver(tokens[1], blackCount, whiteCount);
				break;

			case "ERROR":
				System.err.println("サーバーエラー: " + message.substring(6));
				break;

			default:
				System.out.println("不明なコマンド: " + command);
		}
	}

	private class MessageReceiveThread extends Thread {
		public void run() {
			try {
				while (true) {
					String line = in.readLine();
					if (line == null) break;
					System.out.println("受信: " + line);
					handleMessage(line);
				}
			} catch (IOException e) {
				networkListener.onNetworkError("Connection lost");
			}
		}
	}
}