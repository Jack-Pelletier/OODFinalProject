import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class TicTacClientEnd extends Frame implements ActionListener{

    private int moveCount = 0;
    private JDialog loginDialog;
    private JTextField tfP1Score, tfP2Score;
    private JButton  exitButton, playAgainButton;
    private TextArea resultMessage;
    
    //definitions for awt elements 
    private Socket socket;
    private BufferedReader input;
    private String username;


    public TicTacClientEnd() {
        setTitle("Multiplayer Tic Tac Toe");
        setSize(500, 400);
        //also send a message to close the other window 
        setLayout(new BorderLayout());

        createGameResultDialog(); //awt components 
    }
//method for login window 
    private void createGameResultDialog() { //needs boolean toggle that only turns true once winning pattern is found 
        resultDialog = new JDialog(this, "Game Results", true); 
        resultDialog.setSize(300, 250);
        resultDialog.setLayout(new GridLayout(5, 2));

       
        resultMessage = new TextArea();
        resultMessage.setEditable(false);

        playAgainButton = new JButton("Play Again");
        exitButton = new JButton("Exit");

        
        exitButton.addActionListener(this);
        playAgainButton.addActionListener(this);


        //additions for elements 
        resultDialog.add(exitButton);
        resultDialog.add(playAgainButton);
        resultDialog.add(resultMessage);

        resultDialog.setVisible(true);
    }

    public void createGameWindow() {
        
        //chat window elements 
        JFrame frame = new JFrame("Tic Tac Toe Board");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final JButton button = new JButton();
                button.setName(i + "" + j);
                button.setText("");
                button.setFont(new Font("Arial", Font.BOLD, 40));
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (button.getText().equals("")) {
                            if (moveCount % 2 == 0) {
                                button.setText("X");
                            } else {
                                button.setText("O");
                            }
                            moveCount++;
                        }
                    }
                });

                panel.add(button);
            }
        }

        frame.add(panel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
        
    }
    //method for connecting to server 
    private void connectToServer() {
        try {
            String serverAddress = "localhost"; // will change to whoevers ip server is run on 
            int port = 6000;

            socket = new Socket(serverAddress, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // Send encrypted username to server
            output.println(EncryptUtil.encrypt(username));

            new Thread(() -> {
                try { //thread creation for server connection and ecryption 
                    String line;
                    while ((line = input.readLine()) != null) {
                        // Decrypt the message received from the server
                        String decryptedMessage = EncryptUtil.decrypt(line);
                        
                        
                        SwingUtilities.invokeLater(() -> {
                            chatArea.append(decryptedMessage + "\n");
                        });
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append("Disconnected from server.\n");
                    });//error message for server disconnection 
                }
            }).start();

        } catch (IOException e) {
            errorMessage.setText("Error connecting to server: " + e.getMessage() + "\n");
        }
    }
    //method for signing up  
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ) { // possible use of switch case to prevent bloated code 
            String uname = tfUser.getText().trim();
            String pwd = tfPassword.getText();
            //login logic for for logging in 
           // if (uname.isEmpty() || pwd.isEmpty()) {
              //  errorMessage.setText("Error, please fill in all fields");
           // } else if (!pwd.matches(passwordPattern)) {
              //  errorMessage.setText("Password must be at least 6 alphanumeric characters.");
           // } else if (userExists(uname)) {
             //   errorMessage.setText("Username already exists.");
          //  } else {
                //if (registerUser(uname, pwd)) {
                  //  errorMessage.setText("Sign up successful. You can now log in.");
               // } else {
                 //   errorMessage.setText("Error storing sign-up info.");
                }
            }
        }   
        //login button logic 
        if (e.getSource() == exitButton) {

            if (uname.isEmpty() || pwd.isEmpty()) {
               //
            } else if () { //
                username = uname;
                loginDialog.setVisible(false);
                createChatWindow();
                connectToServer();
            } else {
                errorMessage.setText("Invalid username or password.");
            }
        }
        //method for resetting the game board 
        if (e.getSource() == playAgainButton) {
          //set score board to zero - its gonna be text fields being changed to null or something 
            if () {//button(array) value is not zero change it back to zero
               //reset buttons to zero 
            }
        } 

        // if (e.getSource() == clearChat) {
        //     chatArea.setText("");
        // }
        //mmethod for logging out 
        if (e.getSource() == logOut) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    
    //method for sending a turn 
    private boolean sendTurn(String button) {
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
    //         writer.write(username + ":" + password);
    //         writer.newLine();
    //         return true;
    //     } catch (IOException e) {
    //         return false;
    //     }
    }
    
    //validates logic 
    // private boolean validateLogin(String username, String password) {
    //     try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             String[] parts = line.split(":");
    //             if (parts.length == 2 && parts[0].equalsIgnoreCase(username) && parts[1].equals(password)) {
    //                 return true;
    //             }
    //         }
    //     } catch (IOException e) {
    //         errorMessage.setText("Error reading user file.\n");
    //     }
    //     return false;
    // }
    //main method for running chat 
    public static void main(String[] args) {
        new TicTacClientEnd();
    }
}    
    
    
