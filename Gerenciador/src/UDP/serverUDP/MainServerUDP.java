package UDP.serverUDP;

import java.net.SocketException;
import java.net.UnknownHostException;

public class MainServerUDP {
    public static void main(String[] args) {
        try {
            ServerUDP servidor = new ServerUDP(1026, 1026, "127.0.0.1");
            servidor.start();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
