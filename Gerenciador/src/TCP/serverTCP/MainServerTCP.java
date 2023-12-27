package TCP.serverTCP;

import java.io.IOException;

public class MainServerTCP {
    
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Servidor finalizado!");
    }
}
