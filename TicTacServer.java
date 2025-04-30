import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TicTacServer {
    private static final int PORT = 5000;
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private static final TurnActions turnManager = new TurnActions();
    private static GameLogic gameLogic;
    
    public static void main(String[] args) {
        System.out.println("Tic Tac Toe Server started on port " + PORT);
        gameLogic = new GameLogic();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                boolean isPlayer1 = clients.size() == 0;
                ClientHandler handler = new ClientHandler(clientSocket, isPlayer1);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class TurnActions {
        private boolean turn = false;
    
        public synchronized String[][] player1action(int i, int j) throws InterruptedException {
            while (turn) wait(); // Not Player 1's turn
            if (gameLogic.board[i][j].equals(" ")) {
                gameLogic.board[i][j] = "X";
                turn = true;
                notifyAll();
            }
            return gameLogic.board;
        }
    
        public synchronized String[][] player2action(int i, int j) throws InterruptedException {
            while (!turn) wait(); // Not Player 2's turn
            if (gameLogic.board[i][j].equals(" ")) {
                gameLogic.board[i][j] = "O";
                turn = false;
                notifyAll();
            }
            return gameLogic.board;
        }

        public synchronized String getBoardString() {
            StringBuilder sb = new StringBuilder();
            for (String[] row : gameLogic.board) {
                sb.append(String.join(",", row)).append(";");
            }
            return sb.toString();
        }
    }
    
    
    

    static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private final boolean isPlayer1;

    ClientHandler(Socket socket, boolean isPlayer1) {
        this.socket = socket;
        this.isPlayer1 = isPlayer1;
        try {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        sendMessage("You are " + (isPlayer1 ? "Player 1 (X)" : "Player 2 (O)"));
        } catch (IOException e) {
        System.err.println("Error setting up I/O for a client.");
    }
}


public void run() {
    try {
        while (true) {
            String move = reader.readLine();
            if (move == null) break;

            String[] parts = move.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            if (isPlayer1) {
                turnManager.player1action(row, col);
                broadcast("Player 1 moved: " + move, this);
            } else {
                turnManager.player2action(row, col);
                broadcast("Player 2 moved: " + move, this);
            }

            String boardState = turnManager.getBoardString();
            broadcast("Board: " + boardState, null);
        }
    } catch (Exception e) {
        System.err.println("Client disconnected.");
    } finally {
        try {
            clients.remove(this);
            socket.close();
        } catch (IOException ignored) {}
    }
}

        

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
