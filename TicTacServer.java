import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

// Main server class for Tic Tac Toe
public class TicTacServer {
    private static final int PORT = 5000; // Server will listen on port 5000
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet(); // Thread-safe set of connected clients
    private static final TurnActions turnManager = new TurnActions(); // Handles player turns
    private static GameLogic gameLogic; // Game logic for maintaining board state

    public static void main(String[] args) {
        System.out.println("Tic Tac Toe Server started on port " + PORT);
        gameLogic = new GameLogic(); // Initialize game logic

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept client connections
                boolean isPlayer1 = clients.size() == 0; // First client is Player 1
                ClientHandler handler = new ClientHandler(clientSocket, isPlayer1);
                clients.add(handler);
                new Thread(handler).start(); // Handle each client on a new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to manage player turns in a synchronized way
    static class TurnActions {
        private boolean turn = false; // false = Player 1's turn, true = Player 2's turn

        // Handle Player 1's action
        public synchronized String[][] player1action(int i, int j) throws InterruptedException {
            while (turn) wait(); // Wait if it's not Player 1's turn
            if (gameLogic.board[i][j].equals(" ")) {
                gameLogic.board[i][j] = "X"; // Mark the board
                turn = true; // Switch turn to Player 2
                notifyAll(); // Wake up waiting threads
            }
            return gameLogic.board;
        }

        // Handle Player 2's action
        public synchronized String[][] player2action(int i, int j) throws InterruptedException {
            while (!turn) wait(); // Wait if it's not Player 2's turn
            if (gameLogic.board[i][j].equals(" ")) {
                gameLogic.board[i][j] = "O"; // Mark the board
                turn = false; // Switch turn to Player 1
                notifyAll(); // Wake up waiting threads
            }
            return gameLogic.board;
        }

        // Get the current state of the board as a string
        public synchronized String getBoardString() {
            StringBuilder sb = new StringBuilder();
            for (String[] row : gameLogic.board) {
                sb.append(String.join(",", row)).append(";");
            }
            return sb.toString();
        }
    }

    //Send a message to all clients except the sender
    static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    //class to handle commynication with each client
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private final boolean isPlayer1;

        // Constructor for client handler
        ClientHandler(Socket socket, boolean isPlayer1) {
            this.socket = socket;
            this.isPlayer1 = isPlayer1;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                sendMessage("You are " + (isPlayer1 ? "Player 1 (X)" : "Player 2 (O)")); // Inform client of their role
            } catch (IOException e) {
                System.err.println("Error setting up I/O for a client.");
            }
        }

        // Handle client input and turn processing
        public void run() {
            try {
                while (true) {
                    String move = reader.readLine(); // Read move from client
                    if (move == null) break; // Client disconnected

                    String[] parts = move.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);

                    // Execute move and notify other player
                    if (isPlayer1) {
                        turnManager.player1action(row, col);
                        broadcast("Player 1 moved: " + move, this);
                    } else {
                        turnManager.player2action(row, col);
                        broadcast("Player 2 moved: " + move, this);
                    }

                    // Send updated board state to all players
                    String boardState = turnManager.getBoardString();
                    broadcast("Board: " + boardState, null);
                }
            } catch (Exception e) {
                System.err.println("Client disconnected.");
            } finally {
                try {
                    clients.remove(this); // Remove client on disconnect
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        // Send a message to this client
        void sendMessage(String message) {
            try {
                writer.write(message + "\n");
                writer.flush();
            } catch (Exception e) {
                System.err.println("Failed to send message to client.");
            }
        }
    }
}
