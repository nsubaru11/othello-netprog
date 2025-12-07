package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class OthelloServer {
	private static final int DEFAULT_PORT = 10000;
	private ServerSocket serverSocket;
	private final HashMap<Integer, Queue<ClientHandler>> waitingPlayers;

	public OthelloServer(int port) {
		waitingPlayers = new HashMap<>();

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Othello Server started on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = DEFAULT_PORT;

		// 引数があれば、それをポート番号として使う
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("ポート番号が不正です。デフォルト(" + port + ")を使用します。");
			}
		}

		OthelloServer server = new OthelloServer(port);
		server.start();
	}

	public void start() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client connected");

				ClientHandler handler = new ClientHandler(clientSocket, this);
				handler.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void addWaitingPlayer(int boardSize, ClientHandler player) {
		waitingPlayers.computeIfAbsent(boardSize, k -> new LinkedList<>()).add(player);
		System.out.println("プレイヤーが待ち行列に追加されました: " + player.getPlayerName());
		matchPlayers(boardSize);
	}

	private void matchPlayers(int boardSize) {
		while (waitingPlayers.get(boardSize).size() >= 2) {
			ClientHandler player1 = waitingPlayers.get(boardSize).poll();
			ClientHandler player2 = waitingPlayers.get(boardSize).poll();
			new GameRoom(player1, player2, boardSize);
			System.out.println("Matched: " + player1.getPlayerName() + " vs " + player2.getPlayerName());
		}
	}

	public void disconnectPlayer(ClientHandler player) {
		waitingPlayers.values().forEach(queue -> queue.remove(player));
	}
}
