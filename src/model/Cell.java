package model;

public final class Cell {
	private Piece piece;

	public Cell() {
		piece = Piece.EMPTY;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece p) {
		piece = p;
	}

	public boolean isEmpty() {
		return piece == Piece.EMPTY;
	}

	public boolean isBlack() {
		return piece == Piece.BLACK;
	}

	public boolean isWhite() {
		return piece == Piece.WHITE;
	}

}
