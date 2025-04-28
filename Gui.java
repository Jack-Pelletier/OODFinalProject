import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private boolean player1Turn = true;
    private GameLogic gameLogic;

    private JLabel roundLabel;
    private JLabel scoreLabel;
    private JLabel p1Label;
    private JLabel tieLabel;
    private JLabel p2Label;
    private JLabel turnLabel;

    public Gui() {
        setTitle("Tic Tac Toe");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        gameLogic = new GameLogic(); // initialize game logic

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBounds(10, 10, 400, 400);
        add(boardPanel);

        // Create board buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 48));
                buttons[i][j].setFocusPainted(false);
                boardPanel.add(buttons[i][j]);

                int row = i;
                int col = j;

                buttons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttonClicked(row, col);
                    }
                });
            }
        }

        // Right side panel labels
        roundLabel = new JLabel("Round: 1");
        roundLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roundLabel.setBounds(430, 10, 200, 30);
        add(roundLabel);

        scoreLabel = new JLabel("Scores");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBounds(430, 50, 100, 30);
        add(scoreLabel);

        p1Label = new JLabel("P1: 0");
        tieLabel = new JLabel("Tie: 0");
        p2Label = new JLabel("P2: 0");

        p1Label.setFont(new Font("Arial", Font.BOLD, 16));
        tieLabel.setFont(new Font("Arial", Font.BOLD, 16));
        p2Label.setFont(new Font("Arial", Font.BOLD, 16));

        p1Label.setBounds(430, 90, 100, 30);
        tieLabel.setBounds(430, 130, 100, 30);
        p2Label.setBounds(430, 170, 100, 30);

        add(p1Label);
        add(tieLabel);
        add(p2Label);


        turnLabel = new JLabel("Turn: Player 1 (X)");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setBounds(10, 420, 300, 30);
        add(turnLabel);

        setVisible(true);
    }

    private void buttonClicked(int row, int col) {
        if (gameLogic.makeMove(row, col)) {
            buttons[row][col].setText(gameLogic.getCell(row, col));

            String winner = gameLogic.checkWinner();
            if (winner != null) {
                gameLogic.updateScore(winner);
                updateUI();
                showGameResult(winner);
                gameLogic.nextRound();
            } else {
                updateUI();
                toggleTurnLabel();
            }
        }
    }

    private void showGameResult(String winner) {
        String message = "Round " + gameLogic.getRound() + " Result: ";
        if (winner.equals("Tie")) {
            message += "It's a tie!";
        } else {
            message += "Player " + (winner.equals("X") ? "1" : "2") + " wins!";
        }
        JOptionPane.showMessageDialog(this, message);
    }

    private void updateUI() {
        roundLabel.setText("Round: " + gameLogic.getRound());
        p1Label.setText("P1: " + gameLogic.getP1Score());
        tieLabel.setText("Tie: " + gameLogic.getTieScore());
        p2Label.setText("P2: " + gameLogic.getP2Score());
    }

    private void toggleTurnLabel() {
        turnLabel.setText("Turn: Player " + (gameLogic.isPlayer1Turn() ? "1 (X)" : "2 (O)"));
    }

    public static void main(String[] args) {
        new Gui();
    }
}
