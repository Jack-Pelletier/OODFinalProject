public class gameLogic {
    private String[][] board;
    private boolean player1Turn = true;
    private int round = 1;
    private int p1Score = 0;
    private int p2Score = 0;
    private int tieScore = 0;

    // game logic constructor 
    public gameLogic() {
        board = new String[3][3];
    }


    // method to make a move during a player's turn
    // params: row (int), col (int)
    // returns a boolean value indicating if turn was successful or not
    public boolean makeMove(int row, int col) {
        if (board[row][col] == null) {
            board[row][col] = player1Turn ? "X" : "O";
            player1Turn = !player1Turn;
            return true;
        }
        return false;
    }


    // method to check a given cell
    // params: row (int), col(int)
    // returns a string either empty or the value in the given cell
    public String getCell(int row, int col) {
        return board[row][col] == null ? "" : board[row][col];
    }


    // method to determine current game status
    // no params
    // returns X if player 1 won, O if player 2 won, tie if tie, null if game is still in progress
    public String checkWinner() {
        // Rows and columns
        for (int i = 0; i < 3; i++) {
            if (same(board[i][0], board[i][1], board[i][2])) return board[i][0];
            if (same(board[0][i], board[1][i], board[2][i])) return board[0][i];
        }
        // Diagonals
        if (same(board[0][0], board[1][1], board[2][2])) return board[0][0];
        if (same(board[0][2], board[1][1], board[2][0])) return board[0][2];

        // Check for tie
        boolean full = true;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == null)
                    full = false;

        return full ? "Tie" : null;
    }

    // helper method to check whether three given strings are equal and not null
    // params: a (string), b(string), c (string)
    // returns a boolean value to indicate whether three strings are equal or not
    private boolean same(String a, String b, String c) {
        return a != null && a.equals(b) && b.equals(c);
    }

    // method to update the score for each possible game outcome
    // params: winner (string)
    // no return
    public void updateScore(String winner) {
        switch (winner) {
            case "X":
                p1Score++;
                break;
            case "O":
                p2Score++;
                break;
            case "Tie":
                tieScore++;
                break;
        }
    }


    // method to reset the board after the end of each round
    // no params
    // no return
    public void resetBoard() {
        board = new String[3][3];
        player1Turn = true;
    }

    // method to increment round number for start of new round
    // no params
    // no return 
    public void nextRound() {
        round++;
        resetBoard();
    }

    // Getters
    public int getRound() { return round; }
    public int getP1Score() { return p1Score; }
    public int getP2Score() { return p2Score; }
    public int getTieScore() { return tieScore; }
    public boolean isPlayer1Turn() { return player1Turn; }
}
