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
public class Server {
    private static final int SERVER_PORT = 3000;
    private static ArrayList<ClientThread> loggedInClients = new ArrayList<>();

    private void run() {
        try {
            //make the server socket
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            System.out.println("Server online!");

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
        private String nickname = "Anonymous";
        PrintWriter out;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                boolean loggedin = true;
                while (loggedin) {
                    //make the in and out writers
                    out =
                            new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

                    //get the inputLine
                    String inputLine = in.readLine();

                    String command;
                    String line = "";

                    //filter the command out
                    if (inputLine.indexOf(" ") >= 0) {
                        command = inputLine.substring(0, inputLine.indexOf(" "));
                        line = inputLine.substring(inputLine.indexOf(" ") + 1);
                    } else {
                        command = inputLine;
                    }
                    switch (command) {
                        case "/broadcast":
                            broadcastMessage("[BROADCAST][" + nickname + "] " + line);
                            break;
                        //change nickname
                        case "/newNickname":
                            nickname = line;
                            out.println("Nickname changed!");
                            break;
                        case "/pm":
                            String receiver = line.substring(0, line.indexOf(" "));
                            String message = line.substring(line.indexOf(" ") + 1);
                            System.out.println(receiver + message);

                            if (nickname.equals("Anonymous")){
                                out.println("Can not send a message as Anonymous, please register a nickname first!");
                                break;
                            }

                            if (receiver.equals("Anonymous")) {
                                out.println("Can not send a message to Anonymous");
                                break;
                            }
                            out.println("[You to " + receiver + "]" + message);
                            //send the message
                            sendMessageTo(receiver, nickname, message);
                            break;

                        case "/logout":
                            //logout
                            System.out.println("Client disconnected");
                            loggedin = false;

                            //tell the client he is logged of
                            out.println("Logging out!");
                            break;
                    }
                    out.flush();
                }

                //if we are here it means the client logged of
                logout(this);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Send a message to the client
         *
         * @param message the message send
         */
        public void sendMessage(String message) {
            out.println(message);
            out.flush();
        }

        public String getNickname() {
            return nickname;
        }



    }

    /**
     * Send a message to all logged in clients
     *
     * @param message the message send
     */
    private void broadcastMessage(String message) {
        System.out.println("BROADCAST: [" + message + "]");
        for (ClientThread clientThread : loggedInClients) {
            clientThread.sendMessage(message);
        }
    }

    /**
     * Method that checks if a nickname is unique
     *
     * @param name the name that gets checked
     * @return true if unique else false
     */
    private boolean nameUnique(String name) {
        for (ClientThread clientThread : loggedInClients) {
            if (clientThread.getNickname().equals(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Message that sends a message to a specifick user
     *
     * @param receiver nickname of the person the message should be sent
     * @param sender   nickname of the sender
     * @param message  the message to send
     */
    private void sendMessageTo(String receiver, String sender, String message) {
        for (ClientThread clientThread : loggedInClients) {
            if (clientThread.getNickname().equals(receiver)) {
                clientThread.sendMessage("[Message from " + sender + "]" + message);
            }
        }
    }

    /**
     * Removes a client from the logged in clients
     * @param clientToBeLoggedOut the client thats logging of
     */
    public void logout(ClientThread clientToBeLoggedOut){
        int i = 0;
        int toRemove = -1;
        for (ClientThread clientThread: loggedInClients){

            //store what to remove
            if (clientThread == clientToBeLoggedOut){
                toRemove = i;
            }
            i++;
        }

        //remove it
        loggedInClients.remove(toRemove);
    }

    public static void main(String[] args) {
        new Server().run();
    }
}
