package com.chess.chess_board_game_rpl;

import static com.chess.chess_board_game_rpl.Pieces.King.isKingInCheck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.chess.chess_board_game_rpl.Initializer.GameBoard;
import com.chess.chess_board_game_rpl.Initializer.Square;
import com.chess.chess_board_game_rpl.Pieces.King;
import com.chess.chess_board_game_rpl.Pieces.Pawn;
import com.chess.chess_board_game_rpl.Pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public class InGameActivity extends AppCompatActivity {

    private final SquareWrapper selectedSquareWrapper = new SquareWrapper(null, null);

    private void updateTurnIndicator(String currentPlayer) {
        TextView turnIndicator = findViewById(R.id.turnIndicator);
        turnIndicator.setText(currentPlayer + "'s Turn");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        // Retrieve the difficulty level from the intent
        Intent intent = getIntent();
        String difficultyLevel = intent.getStringExtra("difficulty");

        // Now you can initialize your game board and adjust settings based on the difficulty level
        setupGameBoard();
        //setGameDifficulty(difficultyLevel);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(difficultyLevel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupGameBoard() {

        // Making a HashMap to make everything more efficient
        Map<String, Integer> pieceImageMap = new HashMap<>();
        pieceImageMap.put("PAWN_BLACK", R.drawable.pawn_black);
        pieceImageMap.put("PAWN_WHITE", R.drawable.pawn_white);
        pieceImageMap.put("ROOK_BLACK", R.drawable.rook_black);
        pieceImageMap.put("ROOK_WHITE", R.drawable.rook_white);
        pieceImageMap.put("KNIGHT_BLACK", R.drawable.knight_black);
        pieceImageMap.put("KNIGHT_WHITE", R.drawable.knight_white);
        pieceImageMap.put("BISHOP_WHITE", R.drawable.bishop_white);
        pieceImageMap.put("BISHOP_BLACK", R.drawable.bishop_black);
        pieceImageMap.put("KING_WHITE", R.drawable.king_white);
        pieceImageMap.put("KING_BLACK", R.drawable.king_black);
        pieceImageMap.put("QUEEN_WHITE", R.drawable.queen_white);
        pieceImageMap.put("QUEEN_BLACK", R.drawable.queen_black);
        // Add other pieces here

        GameBoard gameBoard = new GameBoard(); //Initialize the board
        GridLayout chessBoard = findViewById(R.id.chessBoard);
        String squareTag; //Square tag is to track where the square should be

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                ImageView square = new ImageView(this);
                Square gameSquare = gameBoard.getSquare(row, col); //Get the square data from row and column
                squareTag = row + "," + col; //Square tag consist of row,col for example row 1 and column 1 will be 1,1

                square.setTag(squareTag); //Set the tag

                // Set the background color or image depending on the square's color
                if ((row + col) % 2 == 0) {
                    square.setBackgroundColor(getResources().getColor(R.color.light_square_color)); // Light color
                } else {
                    square.setBackgroundColor(getResources().getColor(R.color.dark_square_color)); // Dark color
                }

                // Set the image based on the state of the square in the GameBoard
                assert gameSquare != null;
                if (gameSquare.isOccupied()) { //Check if the square is occupied
                    Piece piece = gameSquare.getOccupiedBy(); //Get the pieces data if occupied
                    String pieceType = piece.getClass().getSimpleName().toUpperCase(); // e.g., "PAWN", "ROOK", "KNIGHT"
                    String pieceColor = piece.getColor().toUpperCase(); // e.g., "BLACK", "WHITE"
                    String key = pieceType + "_" + pieceColor; // e.g., "PAWN_BLACK", "ROOK_WHITE"

                    if (pieceImageMap.containsKey(key)) {
                        square.setImageResource(pieceImageMap.get(key));
                    }
                    if (gameSquare.getOccupiedBy() instanceof King) {
                        if (gameSquare.getOccupiedBy().getColor().equals("BLACK")) {
                            gameBoard.setKingSquare("BLACK", new Square(row, col));
                        } else if (gameSquare.getOccupiedBy().getColor().equals("WHITE")) {
                            gameBoard.setKingSquare("WHITE", new Square(row, col)); // Replace x and y with the appropriate coordinates
                        }
                    }

                }

                // Handle when the square is clicked
                square.setOnClickListener(v -> { // This is when the square is clicked
                    String tag = (String) v.getTag();
                    String[] parts = tag.split(","); // Split it and make it into variable
                    int clickedRow = Integer.parseInt(parts[0]);
                    int clickedCol = Integer.parseInt(parts[1]);

                    Square clickedSquare = gameBoard.getSquare(clickedRow, clickedCol); //Get the square data from clicked square
                    if (selectedSquareWrapper.square == null) {

                        // First click - Selecting the pieces
                        assert clickedSquare != null;
                        if (clickedSquare.isOccupied() & clickedSquare.getOccupiedBy() != null) {
                            if (selectedSquareWrapper.view != null) {
                                resetHighlighting(selectedSquareWrapper.view, selectedSquareWrapper.square);
                            }
                            selectedSquareWrapper.square = clickedSquare; //The wrapper
                            selectedSquareWrapper.view = (ImageView) v;
                            applyHighlighting(selectedSquareWrapper.view);
                        }
                    } else {
                        Piece selectedPiece = selectedSquareWrapper.square.getOccupiedBy();
                        String currentPlayer = gameBoard.getCurrentPlayer();

                        if (selectedPiece.getColor().equals(currentPlayer)) {
                            // Second click - Attempting to move the piece
                            Piece clickedPiece = clickedSquare.getOccupiedBy();
                            String clickedPieceColor = (clickedPiece != null) ? clickedPiece.getColor() : null;

                            if (isValidMove(selectedPiece, selectedSquareWrapper.square, clickedSquare, gameBoard, clickedPieceColor)) {
                                movePiece(selectedSquareWrapper.square, clickedSquare, selectedSquareWrapper.view, (ImageView) v, pieceImageMap, gameBoard, this);
                                gameBoard.switchTurn();
                                updateTurnIndicator(gameBoard.getCurrentPlayer());
                            } else {
                                showToast("Invalid move");
                            }
                        } else {
                            showToast("Not Your Turn Currently. It's " + currentPlayer + "'s Turn");
                        }
                        resetHighlighting(selectedSquareWrapper.view, selectedSquareWrapper.square);
                        selectedSquareWrapper.square = null; // Reset
                        selectedSquareWrapper.view = null;

                    }
                });

                // Add LayoutParams to control the size of the squares
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = getResources().getDimensionPixelSize(R.dimen.chess_square_size); // Define this dimension in dimens.xml
                params.height = getResources().getDimensionPixelSize(R.dimen.chess_square_size); // Define this dimension in dimens.xml
                square.setLayoutParams(params);

                // Finally, add the view to your GridLayout
                chessBoard.addView(square);
            }
        }
    }

    private void movePiece(Square fromSquare, Square toSquare, ImageView fromView, ImageView toView, Map pieceImageMap, GameBoard gameBoard, InGameActivity inGameActivity) {

        Piece piece = fromSquare.getOccupiedBy(); //The square need to be checked about its content
        String pieceType = piece.getClass().getSimpleName().toUpperCase(); // e.g., "PAWN", "ROOK", "KNIGHT"
        String pieceColor = piece.getColor().toUpperCase(); // e.g., "BLACK", "WHITE"
        String key = pieceType + "_" + pieceColor; // e.g., "PAWN_BLACK", "ROOK_WHITE"

        //Unique Cases should be put here
        if (piece instanceof Pawn && ((Pawn) piece).isFirstMove()) {
            ((Pawn) piece).setFirstMove(false);
        }

        if (piece instanceof King) {
            gameBoard.setKingSquare(piece.getColor(), toSquare);
        }
        // Move the pieces
        if (pieceImageMap.containsKey(key)) {
            toView.setImageResource((Integer) pieceImageMap.get(key));
        }

        //When it kill something
        if (toSquare.isOccupied() && !toSquare.getOccupiedBy().getColor().equals(fromSquare.getOccupiedBy().getColor())) {
            Toast.makeText(inGameActivity, fromSquare.getOccupiedBy().getPiece_tag() + " captured " + toSquare.getOccupiedBy().getPiece_tag(), Toast.LENGTH_SHORT).show();
        }

        toSquare.setOccupiedBy(fromSquare.getOccupiedBy()); //Change the next square pieces content from null to the previously clicked square
        toSquare.setOccupied(true);
        fromSquare.setOccupiedBy(null); //Reset the previous clicked square to default
        fromSquare.setOccupied(false);
        fromView.setImageDrawable(null); // Clear the image from the original square

        checkForCheckAndCheckmate(gameBoard);
    }

    private void checkForCheckAndCheckmate(GameBoard gameBoard) {
        String opponentColor = gameBoard.getCurrentPlayer().equals("WHITE") ? "BLACK" : "WHITE";
        if (isKingInCheck(gameBoard, opponentColor)) {
            Log.d("ChessDebug", "KING IN CHECK");
            if (King.isCheckmate(gameBoard, opponentColor)) {
                showToast("KING CHECK MATED");
            } else {
                // Handle check scenario (e.g., notify players, highlight king)
                Toast.makeText(this, "Your king almost die! get out!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resetHighlighting(ImageView pieceView, Square square) {
        int row = square.getXPosition();
        int col = square.getYPosition();
        if ((row + col) % 2 == 0) {
            pieceView.setBackgroundColor(ContextCompat.getColor(this, R.color.light_square_color)); // Reset to light square color
        } else {
            pieceView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_square_color)); // Reset to dark square color
        }
    }

    private boolean isValidMove(Piece piece, Square fromSquare, Square toSquare, GameBoard gameBoard, String targetColor) {
        return piece.validMove(fromSquare, toSquare, gameBoard) && (piece.getColor() != null && !piece.getColor().equals(targetColor));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void applyHighlighting(ImageView pieceView) {
        pieceView.setBackgroundResource(R.drawable.selected_piece_border);
    }

    public static class SquareWrapper { //This is the wrapper for clicked square
        public Square square;
        public ImageView view;

        public SquareWrapper(Square square, ImageView view) {
            this.square = square;
            this.view = view;
        }
    }
}
