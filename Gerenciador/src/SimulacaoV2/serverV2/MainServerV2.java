package SimulacaoV2.serverV2;

public class MainServerV2 {
    public static void main(String[] args) {
        ServerV2 serverV2 = new ServerV2(1025, "127.0.0.1");
        serverV2.start();
    }
}
