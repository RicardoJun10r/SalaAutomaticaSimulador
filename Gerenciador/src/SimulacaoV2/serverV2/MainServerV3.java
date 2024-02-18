package SimulacaoV2.serverV2;

import java.io.IOException;
import java.util.Scanner;

public class MainServerV3 {

    static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) {

        int port = 2000;

        ServerV3 serverV3 = new ServerV3(port);
        try {
            serverV3.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
