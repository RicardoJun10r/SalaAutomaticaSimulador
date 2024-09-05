package simulacao_v6.sever_v9;

public class Server_V9_Main {
    public static void main(String[] args) {
        Server_V9 server_V9 = new Server_V9("127.0.0.1", 5001, true);

        server_V9.start();
    }
}
