import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacClientEnd extends Frame implements ActionListener{

       private JDialog loginDialog;
    //private JTextField ;
    private JButton tileButtonA1, tileButtonA2, tileButtonA3, tileButtonB1, tileButtonB2, tileButtonB3, tileButtonC1, tileButtonC2, tileButtonC3, exitButton, playAgainButton;
    //private TextArea errorMessage;
    //private JTextArea chatArea;
    //definitions for awt elements 
    private Socket socket;
    private BufferedReader input;
    private String username;


    public TicTacClientEnd() {
        setTitle("Secure Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //also send a message to close the other window 
        setLayout(new BorderLayout());

        createGameResultDialog(); //awt components 
    }
//method for login window 
    private void createGameResultDialog() {
        loginDialog = new JDialog(this, "Login", true);
        loginDialog.setSize(300, 250);
        loginDialog.setLayout(new GridLayout(5, 2));

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
       
        //errorMessage = new TextArea();
        //errorMessage.setEditable(false);

        //signUp = new JButton("Sign Up");
        //login = new JButton("Login");

        tileButtonA1.addActionListener(this);
        tileButtonA2.addActionListener(this);
        tileButtonA3.addActionListener(this);
        tileButtonB1.addActionListener(this);
        tileButtonB2.addActionListener(this);
        tileButtonB3.addActionListener(this);
        tileButtonC1.addActionListener(this);
        tileButtonC2.addActionListener(this);
        tileButtonC3.addActionListener(this);
        exitButton.addActionListener(this);
        playAgainButton.addActionListener(this);


        //additions for elements 
        //loginDialog.add(lblUser);
        //loginDialog.add(tfUser);
        //loginDialog.add(lblPassword);
        //loginDialog.add(tfPassword);
        //loginDialog.add(signUp);
        //loginDialog.add(login);
        loginDialog.add(errorMessage);

        loginDialog.setVisible(true);
    }

    private void createGameWindow() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        //chat window elements 
        tfChatbox = new JTextField();
        send = new JButton("Send");
        clearChat = new JButton("Clear Chat");
        logOut = new JButton("Log Out");

        send.addActionListener(this);
        clearChat.addActionListener(this);
        logOut.addActionListener(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(tfChatbox, BorderLayout.CENTER);
        inputPanel.add(send, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearChat);
        buttonPanel.add(logOut);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    //method for connecting to server 
    private void connectToServer() {
        try {
            String serverAddress = "localhost";
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
        if (e.getSource() == signUp) {
            String uname = tfUser.getText().trim();
            String pwd = tfPassword.getText();
            //login logic for for logging in 
            if (uname.isEmpty() || pwd.isEmpty()) {
                errorMessage.setText("Error, please fill in all fields");
            } else if (!pwd.matches(passwordPattern)) {
                errorMessage.setText("Password must be at least 6 alphanumeric characters.");
            } else if (userExists(uname)) {
                errorMessage.setText("Username already exists.");
            } else {
                if (registerUser(uname, pwd)) {
                    errorMessage.setText("Sign up successful. You can now log in.");
                } else {
                    errorMessage.setText("Error storing sign-up info.");
                }
            }
        }   
        //login button logic 
        if (e.getSource() == login) {
            String uname = tfUser.getText().trim();
            String pwd = tfPassword.getText();

            if (uname.isEmpty() || pwd.isEmpty()) {
                errorMessage.setText("Error, please fill in all fields");
            } else if (validateLogin(uname, pwd)) {
                username = uname;
                loginDialog.setVisible(false);
                createChatWindow();
                connectToServer();
            } else {
                errorMessage.setText("Invalid username or password.");
            }
        }
        //method for sending a chat message 
        if (e.getSource() == send) {
            String message = tfChatbox.getText();
            if (!message.isEmpty()) {
                String encryptedMessage = EncryptUtil.encrypt(message);
                output.println(encryptedMessage);
                tfChatbox.setText("");
            }
        }   //method for clearing chat 

        if (e.getSource() == clearChat) {
            chatArea.setText("");
        }
        //mmethod for logging out 
        if (e.getSource() == logOut) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }
    //method for registering as a user 
    private boolean registerUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    //boolean for checking if user already has registered 
    private boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0 && parts[0].equalsIgnoreCase(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // file might not exist yet
        }
        return false;
    }
    //validates logic 
    private boolean validateLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equalsIgnoreCase(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            errorMessage.setText("Error reading user file.\n");
        }
        return false;
    }
    //main method for running chat 
    public static void main(String[] args) {
        new TicTacClientServer();
    }
        }
    
    
