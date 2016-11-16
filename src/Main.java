import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Marten on 11/16/2016.
 */
public class Main {
    private static final int SERVER_PORT = 8080;
    private static ArrayList<ClientThread> loggedInClients = new ArrayList<>();

    private void run() {
        try {
            //make the server socket
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            while (true) {
                ClientThread clientThread = new ClientThread(serverSocket.accept());
                System.out.println("New client connected");

                loggedInClients.add(clientThread);
                clientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread that clients 'use'
     * Waits for input from the client or other clients
     */
    private class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String inputLine = in.readLine();
                broadcastMessage(inputLine);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Send a message to the client
         * @param message the message send
         */
        public void sendMessage(String message){
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message to all logged in clients
     * @param message the message send
     */
    private void broadcastMessage(String message){
        for (ClientThread clientThread: loggedInClients){
            clientThread.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
