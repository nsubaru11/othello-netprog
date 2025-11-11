package Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OthelloServer {
	private static final int DEFAULT_PORT = 10000;
	private static final int DEFAULT_BOARD_SIZE = 8;
	private ServerSocket serverSocket;
	private List<GameRoom> rooms;
	private Queue<ClientHandler> waitingPlayers;

	public OthelloServer(int port) {
		rooms = new ArrayList<>();
		waitingPlayers = new ConcurrentLinkedQueue<>();

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Othello Server started on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OthelloServer server = new OthelloServer(DEFAULT_PORT);
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

	public synchronized void addWaitingPlayer(ClientHandler player) {
		waitingPlayers.offer(player);
		System.out.println("Player added to waiting queue: " + player.getPlayerName());

		// マッチング試行
		matchPlayers();
	}

	private void matchPlayers() {
		while (waitingPlayers.size() >= 2) {
			ClientHandler player1 = waitingPlayers.poll();
			ClientHandler player2 = waitingPlayers.poll();

			if (player1 != null && player2 != null) {
				GameRoom room = new GameRoom(DEFAULT_BOARD_SIZE);
				room.addPlayer(player1);
				room.addPlayer(player2);
				rooms.add(room);

				System.out.println("Matched: " + player1.getPlayerName() +
						" vs " + player2.getPlayerName());
			}
		}
	}
}