import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Marten on 11/16/2016.
 */
public class Main {
    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
