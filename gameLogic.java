public class GameLogic {
    private String[][] board;
    private boolean player1Turn = true;
    private int round = 1;
    private int p1Score = 0;
    private int p2Score = 0;
    private int tieScore = 0;

    public GameLogic() {
        board = new String[3][3];
    }

    public boolean makeMove(int row, int col) {
        if (board[row][col] == null) {
            board[row][col] = player1Turn ? "X" : "O";
            player1Turn = !player1Turn;
            return true;
        }
        return false;
    }

    public String getCell(int row, int col) {
        return board[row][col] == null ? "" : board[row][col];
    }

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

    private boolean same(String a, String b, String c) {
        return a != null && a.equals(b) && b.equals(c);
    }

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

    public void resetBoard() {
        board = new String[3][3];
        player1Turn = true;
    }

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
