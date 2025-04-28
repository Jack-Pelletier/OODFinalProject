public class GameLogic {
    private String[][] board = new String[3][3];
    private int p1Score = 0;
    private int p2Score = 0;
    private int ties = 0;
    private int round = 1;
    private boolean player1Turn = true;

    // Constructor
    public GameLogic() {
        resetBoard();
    }

    // Reset the game board
    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    // Mark a move on the board
    public boolean makeMove(int row, int col) {
        if (board[row][col].equals("")) {
            if (player1Turn) {
                board[row][col] = "X";
            } else {
                board[row][col] = "O";
            }
            player1Turn = !player1Turn;
            return true;
        }
        return false;
    }

    // Check if there is a winner
    public String checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]) && !board[i][0].equals("")) {
                return board[i][0];
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]) && !board[0][i].equals("")) {
                return board[0][i];
            }
        }

        // Check diagonals
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]) && !board[0][0].equals("")) {
            return board[0][0];
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]) && !board[0][2].equals("")) {
            return board[0][2];
        }

        // Check for a tie
        boolean tie = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("")) {
                    tie = false;
                    break;
                }
            }
        }
        if (tie) {
            return "Tie";
        }

        return null; // No winner yet
    }

    // Update the score based on the result of a round
    public void updateScore(String winner) {
        if (winner.equals("X")) {
            p1Score++;
        } else if (winner.equals("O")) {
            p2Score++;
        } else if (winner.equals("Tie")) {
            ties++;
        }
    }

    // Reset the round
    public void nextRound() {
        round++;
        resetBoard();
    }

    // Getters for the current game state
    public int getP1Score() {
        return p1Score;
    }

    public int getP2Score() {
        return p2Score;
    }

    public int getTieScore() {
        return ties;
    }

    public int getRound() {
        return round;
    }

    public boolean isPlayer1Turn() {
        return player1Turn;
    }

    public String getCell(int row, int col) {
        return board[row][col];
    }
}
