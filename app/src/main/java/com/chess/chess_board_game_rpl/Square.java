package com.chess.chess_board_game_rpl;

public class Square {
    private int xPosition;
    private int yPosition;
    private Piece occupiedBy; // This will be a reference to the piece that occupies the square

    public Square(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
        this.occupiedBy = null; // Initially, the square is not occupied
    }

    public Piece isOccupied() {
        return this.occupiedBy;
    }

    public void setOccupiedBy(Piece occupiedBy) {
        this.occupiedBy = occupiedBy;
    }
}