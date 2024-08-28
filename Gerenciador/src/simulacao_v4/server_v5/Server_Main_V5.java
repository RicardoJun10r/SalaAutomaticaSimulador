package simulacao_v4.server_v5;

public class Server_Main_V5 {
    public static void main(String[] args) {
        ServerV7 serverV7 = new ServerV7("localhost", 5000);
        serverV7.start();
    }
}
