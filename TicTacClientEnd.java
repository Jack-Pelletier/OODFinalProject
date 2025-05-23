import java.io.*;
import java.net.*;
import javax.swing.*;

public class TicTacClientEnd{

    
    //definitions for awt elements 
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    public Gui gui;
    
    //public board = String[][] = ;


    public TicTacClientEnd(){ //runs client end, connecting  to server and starting threads 
        gui = new Gui(this);  // Pass `this` to the GUI
        connectToServer();
        startListening();
    }

        public static void main(String[] args) { // main :D 
            new TicTacClientEnd();
        } 
    
    //method for connecting to server 
    private void connectToServer() {
       
       try{
        String serverAddress = "localhost";
        int port = 5000;
        //socket for connecting to server 
        socket = new Socket(serverAddress, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println("Connected to server!");
            
       } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error connecting to server, please try again.");
        System.exit(1);
       }}

    //method for sending a turn 
    private boolean sendGameData(int i, int j) {
        if (socket != null && output != null) {
            output.println(i + "," + j);  // Send as "row,col"
            return true;
        } else {
            return false;
        }
    }
     //thread to listen to server for connection 
    private void startListening() {
        Thread thread = new Thread(() -> {
            try{
                String line; 
                while ((line = input.readLine()) != null){
                    System.out.println("Server: " + line);
                }
            }catch(IOException e){
                System.out.println("Disconnected from the server.");
            }
        });
        thread.start();
    }
}
    

