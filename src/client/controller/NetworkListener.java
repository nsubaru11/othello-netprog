package client.controller;

import model.*;

public interface NetworkListener {
	void onGameStart(Piece assignedColor);

	void onYourTurn();

	void onOpponentTurn();

	void onMoveAccepted(int i, int j);

	void onGameOver(String result, int blackCount, int whiteCount);

	void onNetworkError(String message);
}
