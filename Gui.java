import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui extends JFrame {
    JButton[][] buttons = new JButton[3][3];
    private gameLogic gameLogic;

    private JLabel roundLabel;
    private JLabel scoreLabel;
    private JLabel p1Label;
    private JLabel tieLabel;
    private JLabel p2Label;
    private JLabel turnLabel;

    private String player1Name;
    private String player2Name;

    // main GUI constructor
    public Gui() {
        setTitle("Login - Tic Tac Toe");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // labels and input fields for player login
        JLabel p1LoginLabel = new JLabel("Player 1 Username:");
        p1LoginLabel.setBounds(50, 50, 150, 30);
        add(p1LoginLabel);

        JTextField player1Field = new JTextField();
        player1Field.setBounds(200, 50, 150, 30);
        add(player1Field);

        JLabel p2LoginLabel = new JLabel("Player 2 Username:");
        p2LoginLabel.setBounds(50, 100, 150, 30);
        add(p2LoginLabel);

        JTextField player2Field = new JTextField();
        player2Field.setBounds(200, 100, 150, 30);
        add(player2Field);

        // button to start the game
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(125, 180, 150, 40);
        add(startButton);

        // listener for start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String p1 = player1Field.getText().trim();
                String p2 = player2Field.getText().trim();

                if (validateUsername(p1) && validateUsername(p2)) {
                    player1Name = p1;
                    player2Name = p2;
                    setupGameBoard();
                } else {
                    JOptionPane.showMessageDialog(null, "Usernames must:\n- Have at least 1 uppercase letter\n- Be 1-12 characters\n- Only letters and numbers", "Invalid Username", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    public Gui(TicTacClientEnd ticTacClientEnd) {
        //TODO Auto-generated constructor stub
    }

    // method to validate player usernames with regex
    private boolean validateUsername(String username) {
        return username.matches("^(?=.*[A-Z])[A-Za-z0-9]{1,12}$");
    }

    // method to set up the game board GUI
    private void setupGameBoard() {
        getContentPane().removeAll();
        repaint();
        setTitle("Tic Tac Toe - " + player1Name + " vs " + player2Name);
        setSize(650, 500);
        setLayout(null);

        gameLogic = new gameLogic();

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBounds(10, 10, 400, 400);
        add(boardPanel);

        // create 3x3 board buttons
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

        // round and score labels
        roundLabel = new JLabel("Round: 1");
        roundLabel.setFont(new Font("Arial", Font.BOLD, 18));
        roundLabel.setBounds(430, 10, 200, 30);
        add(roundLabel);

        scoreLabel = new JLabel("Scores");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBounds(430, 50, 100, 30);
        add(scoreLabel);

        p1Label = new JLabel(player1Name + ": 0");
        tieLabel = new JLabel("Tie: 0");
        p2Label = new JLabel(player2Name + ": 0");

        p1Label.setFont(new Font("Arial", Font.BOLD, 16));
        tieLabel.setFont(new Font("Arial", Font.BOLD, 16));
        p2Label.setFont(new Font("Arial", Font.BOLD, 16));

        p1Label.setBounds(430, 90, 200, 30);
        tieLabel.setBounds(430, 130, 200, 30);
        p2Label.setBounds(430, 170, 200, 30);

        add(p1Label);
        add(tieLabel);
        add(p2Label);

        // label to show which player's turn it is
        turnLabel = new JLabel("Turn: " + player1Name + " (X)");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setBounds(10, 420, 300, 30);
        add(turnLabel);

        revalidate();
        repaint();
    }

    // method called when a board button is clicked
    private void buttonClicked(int row, int col) {
        if (gameLogic.makeMove(row, col)) {
            buttons[row][col].setText(gameLogic.getCell(row, col));
    
            String winner = gameLogic.checkWinner();
            if (winner != null) {
                gameLogic.updateScore(winner);
    
                // Create message for result
                String message;
                if (winner.equals("Tie")) {
                    message = "Round " + gameLogic.getRound() + " Result: It's a tie!";
                } else {
                    String winnerName = winner.equals("X") ? player1Name : player2Name;
                    message = "Round " + gameLogic.getRound() + " Result: " + winnerName + " wins!";
                }
    
                // Show dialog with "Next Round" and "Exit" options
                int option = JOptionPane.showOptionDialog(
                    this,
                    message + "\nWould you like to play the next round or exit?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Next Round", "Exit Game"},
                    "Next Round"
                );
    
                if (option == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
    
                updateScoreLabels();
                gameLogic.nextRound();
                updateRoundLabel();
                resetBoardUI();
                turnLabel.setText("Turn: " + player1Name + " (X)");
            } else {
                turnLabel.setText("Turn: " + (gameLogic.isPlayer1Turn() ? player1Name + " (X)" : player2Name + " (O)"));
            }
        }
    }
    

    // method to reset button text for new round
    private void resetBoardUI() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
    }

    // method to update scores on the GUI
    // no params
    // no return
    private void updateScoreLabels() {
        p1Label.setText(player1Name + ": " + gameLogic.getP1Score());
        p2Label.setText(player2Name + ": " + gameLogic.getP2Score());
        tieLabel.setText("Tie: " + gameLogic.getTieScore());
    }

    // method to update round label
    // no params
    // no return
    private void updateRoundLabel() {
        roundLabel.setText("Round: " + gameLogic.getRound());
    }

    // main method 
    public static void main(String[] args) {
        new Gui();
    }
}
