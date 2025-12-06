package model;

public enum Piece {
	BLACK, WHITE, EMPTY;

	public boolean isOpponentPiece(final Piece piece) {
		if (this == EMPTY || piece == EMPTY) return false;
		return this != piece;
	}

	public boolean isMyPiece(final Piece piece) {
		if (this == EMPTY || piece == EMPTY) return false;
		return this == piece;
	}

	public boolean isBlack() {
		return this == BLACK;
	}

	public boolean isWhite() {
		return this == WHITE;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}
}
