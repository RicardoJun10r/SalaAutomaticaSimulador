package simulacao_v3.server_v4;

import java.io.IOException;

public class MainServerV4 {
    public static void main(String[] args) {

        ServerV4 serverV4 = new ServerV4(2000);
        try {
            serverV4.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
