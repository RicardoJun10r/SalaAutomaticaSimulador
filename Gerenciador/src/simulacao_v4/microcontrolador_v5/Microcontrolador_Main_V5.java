package simulacao_v4.microcontrolador_v5;

public class Microcontrolador_Main_V5 {
    public static void main(String[] args) {
        MicrocontroladorV5 microcontroladorV6 = new MicrocontroladorV5("localhost", 5000);

        microcontroladorV6.start();
    }
}
