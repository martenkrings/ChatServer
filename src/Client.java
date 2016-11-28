import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Sander on 22-11-2016.
 */
public class Client {
    private static final int SERVER_PORT = 3000;
    private static final String SERVER_ADRESS = "localhost";
    private Socket socket;
    private InputThread inputThread;
    private InputStream inputStream;
    private volatile Scanner scanner;

    boolean loggedIn = true;

    public void run() {
        try {
            //Maak verbinding met de server
            socket = new Socket(SERVER_ADRESS, SERVER_PORT);

            System.out.println("Client connected to server!");

            inputStream = socket.getInputStream();

            //start waiting for input
            inputThread = new InputThread();
            inputThread.start();

            while (loggedIn) {
                //get the outputstream
                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out);

                //Ask for input
                scanner = new Scanner(System.in);
                String inputLine = scanner.nextLine();

                //if we are logged of than quit
                if (!loggedIn) {
                    break;
                }

                //default value
                String command = "-1";
                String line = "";

                //check for valid input
                if (!inputLine.equals("")) {
                    if (inputLine.charAt(0) == '/') {
                        if (inputLine.indexOf(" ") >= 0) {
                            command = inputLine.substring(0, inputLine.indexOf(" "));
                            line = inputLine.substring(inputLine.indexOf(" ") + 1);
                        } else {
                            command = inputLine;
                        }
                    } else {
                        command = "";
                        line = inputLine;
                    }
                }

                switch (command) {
                    case "/newNickname":
                        writer.println(command + " " + line);
                        break;
                    case "":
                        writer.println("/broadcast " + line);
                        break;
                    case "/pm":
                        //check if a message has been givven
                        if (line.indexOf("") < 0) {
                            System.out.println("Please provide a message");
                        } else {
                            writer.println("/pm " + line);
                        }
                        break;
                    case "/logout":
                        writer.println("/logout");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("Please give a valid command/input");

                }
                writer.flush();
            }

            //Always finish things nicely
        } catch (IOException e) {
            System.out.println("\n--------------------------------------------");
            System.out.println("Connection has been broken by server");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("--------------------------------------------\n");

        } finally {
            loggedIn = false;
        }
    }

    private class InputThread extends Thread {

        public void run() {
            try {
                while (loggedIn) {

                    // Blokkeer de thread tot er een volledige regel binnenkomt
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));

                    //get the line
                    String nextLine = reader.readLine();

                    //print the line
                    System.out.println(nextLine);


                }

                //Always finish things nicely
            } catch (IOException e) {
                System.out.println("\n--------------------------------------------");
                System.out.println("Connection has been broken by server");
                System.out.println("Error Message: " + e.getMessage());
                System.out.println("--------------------------------------------\n");

            } finally {
                System.out.println("Enter anything to close client");
                loggedIn = false;
            }
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }
}
