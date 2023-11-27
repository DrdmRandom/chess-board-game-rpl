package com.chess.chess_board_game_rpl;

import android.content.Context;
import android.widget.Toast;

public class Knight extends Piece {
    public Knight(String color, String piece_tag) {
        super(color, piece_tag);
    }

    @Override
    public boolean validMove(Square currentSquare, Square targetSquare, GameBoard gameBoard, Context context) {
        int deltaX = Math.abs(targetSquare.getXPosition() - currentSquare.getXPosition());
        int deltaY = Math.abs(targetSquare.getYPosition() - currentSquare.getYPosition());

        // Check if the move is L-shaped (2 squares in one direction and 1 square in the other)
        if (!((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2))) {
            return false;
        }

        // Check if the target square is occupied by a piece of the same color
        if (targetSquare.isOccupied() && targetSquare.getOccupiedBy().getColor().equals(currentSquare.getOccupiedBy().getColor())) {
            return false; // Cannot capture your own piece
        }

        // If capturing, show a Toast message
        if (targetSquare.isOccupied() && !targetSquare.getOccupiedBy().getColor().equals(currentSquare.getOccupiedBy().getColor())) {
            Toast.makeText(context, "Knight captured " + targetSquare.getOccupiedBy().getPiece_tag(), Toast.LENGTH_SHORT).show();
        }

        return true; // The move is valid
    }
}