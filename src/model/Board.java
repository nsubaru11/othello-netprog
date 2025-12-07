package model;

import java.util.*;

/**
 * 現在操作しているのが、黒でも白でも同じメソッドで動作するように、引数に情報を与えることで、外部の分岐を削減。
 */
public final class Board {
	private static final int[][] directions = {
			{-1, -1}, {1, 1},   // 左上-右下
			{-1, 1}, {1, -1},   // 右上-左下
			{0, -1}, {0, 1},    // 左-右
			{-1, 0}, {1, 0}     // 上-下
	};
	private final int size;
	private final Cell[][] board;
	private final Map<Integer, List<Integer>> blackValidCells = new HashMap<>();
	private final Map<Integer, List<Integer>> whiteValidCells = new HashMap<>();
	private int blackCount = 0, whiteCount = 0;

	/**
	 * n*nのオセロの作成。ただし、nは6以上の偶数
	 */
	public Board(final int size) {
		if (size < 6 || size % 2 == 1) throw new IllegalArgumentException();
		this.size = size;
		board = new Cell[size][size];
		for (int i = 0; i < size; i++) Arrays.setAll(board[i], j -> new Cell());
		initializeBoard();
	}

	private void initializeBoard() {
		int half = size / 2;
		placeBlack(half - 1, half - 1);
		placeWhite(half - 1, half);
		placeWhite(half, half - 1);
		placeBlack(half, half);
		updateValidMoves();
	}

	/**
	 * 引数に与えたplayerの保持コマ数を返す
	 *
	 * @param piece 駒の種類（黒または白）
	 * @return プレイヤーの駒数
	 */
	public int getStoneCount(final Piece piece) {
		return piece.isBlack() ? blackCount : whiteCount;
	}

	public Map<Integer, List<Integer>> getValidCells(final Piece piece) {
		return piece.isBlack() ? blackValidCells : whiteValidCells;
	}

	public void updateValidMoves() {
		blackValidCells.clear();
		whiteValidCells.clear();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Piece piece = board[i][j].getPiece();
				if (!piece.isEmpty()) continue;
				for (int[] d : directions) {
					int di = d[0], dj = d[1];
					int ni = i + di, nj = j + dj;
					if (!isInBounds(ni, nj)) continue;
					if (board[ni][nj].isWhite()) {
						if (cannotMove(ni, nj, Piece.BLACK, di, dj)) continue;
						List<Integer> list = blackValidCells.computeIfAbsent(i * size + j, k -> new ArrayList<>());
						addValidCells(ni, nj, di, dj, Piece.WHITE, list);
					} else if (board[ni][nj].isBlack()) {
						if (cannotMove(ni, nj, Piece.WHITE, di, dj)) continue;
						List<Integer> list = whiteValidCells.computeIfAbsent(i * size + j, k -> new ArrayList<>());
						addValidCells(ni, nj, di, dj, Piece.BLACK, list);
					}
				}
			}
		}
	}

	private boolean cannotMove(int i, int j, final Piece piece, final int di, final int dj) {
		while (isInBounds(i, j) && piece.isOpponentPiece(board[i][j].getPiece())) {
			i += di;
			j += dj;
		}
		return !isInBounds(i, j) || !piece.isMyPiece(board[i][j].getPiece());
	}

	private void addValidCells(int i, int j, final int di, final int dj, Piece piece, List<Integer> list) {
		while (board[i][j].getPiece() == piece) {
			list.add(i * size + j);
			i += di;
			j += dj;
		}
	}

	/**
	 * 引数に与えたplayerが置くことのできるコマ数
	 */
	public int countValidCells(final Piece player) {
		return player.isBlack() ? blackValidCells.size() : whiteValidCells.size();
	}

	/**
	 * playerが座標i, jにコマを置く（この座標は置くことができるという前提）
	 */
	public void setPiece(final Piece player, final int i, final int j) {
		if (player.isBlack()) placeBlack(i, j);
		else placeWhite(i, j);
		List<Integer> validCells = player.isBlack() ? blackValidCells.get(i * size + j) : whiteValidCells.get(i * size + j);
		for (int cell : validCells) {
			int ni = cell / size;
			int nj = cell % size;
			if (player.isBlack()) placeBlack(ni, nj);
			else placeWhite(ni, nj);
		}
		updateValidMoves();
	}

	public void placeBlack(final int i, final int j) {
		if (board[i][j].isWhite()) whiteCount--;
		blackCount++;
		board[i][j].setPiece(Piece.BLACK);
	}

	public void placeWhite(final int i, final int j) {
		if (board[i][j].isBlack()) blackCount--;
		whiteCount++;
		board[i][j].setPiece(Piece.WHITE);
	}

	public boolean canSet(Piece player, int i, int j) {
		return getValidCells(player).get(i * size + j) != null;
	}

	/**
	 * 範囲チェック
	 */
	private boolean isInBounds(final int i, final int j) {
		return 0 <= i && i < size && 0 <= j && j < size;
	}
}
