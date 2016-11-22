import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Sander on 22-11-2016.
 */
public class Client {
    private static final int SERVER_PORT = 3000;
    private static final String SERVER_ADRESS = "localhost";
    private Socket socket;
    private InputStream inputStream;

    public void run(){
        try {
            //Maak verbinding met de server
            socket = new Socket(SERVER_ADRESS, SERVER_PORT);

            System.out.println("Client online!");

            inputStream = socket.getInputStream();

            //start waiting for input
            InputThread inputThread = new InputThread();
            inputThread.start();

            while(true) {
                //get the outputstream
                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(out);

                //Ask for input
                Scanner scanner = new Scanner(System.in);
                System.out.println("Send message: ");

                //write the message to the server
                writer.println(scanner.nextLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class InputThread extends Thread {

        public void run() {
            while (true) {
                try {
                    // Blokkeer de thread tot er een volledige regel binnenkomt
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));

                    //get the line
                    String nextLine = reader.readLine();

                    //print the line
                    System.out.println("Message from server: " + nextLine);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }
}
