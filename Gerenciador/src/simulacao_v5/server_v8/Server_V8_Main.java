package simulacao_v5.server_v8;

public class Server_V8_Main {

    public static void main(String[] args) {
        
        Server_V8 server_V8 = new Server_V8("127.0.0.1", 5000);

        server_V8.start();
        
    }
    
}
