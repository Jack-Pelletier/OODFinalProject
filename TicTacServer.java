import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TicTacServer {
    private static final int PORT = 5000;
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private static final TurnActions turnManager = new TurnActions();
    private boolean isPlayer1;

    
    
    public static void main(String[] args) {
        System.out.println("Tic Tac Toe Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        ClientHandler(Socket socket) {
            this.socket = socket;
            this.isPlayer1 = clients.size() == 0; // FIRST client becomes player 1
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                System.err.println("Error setting up I/O for a client.");
            }
        }

        public void run() {
            try {
                while (true) {
                    if (isPlayer1()) {  
                        String move = reader.readLine();
                        if (move == null) break;
                        turnManager.player1action(Integer.parseInt(move));
                        broadcast("Player 1 moved: " + move, this);
                    } else {
                        int move = turnManager.player2action();
                        sendMessage("Player 2 moved: " + move);
                    }
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
// Font f = new Font("serif", Font.PLAIN, fontSize);
