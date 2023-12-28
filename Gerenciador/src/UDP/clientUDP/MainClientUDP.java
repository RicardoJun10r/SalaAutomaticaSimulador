package UDP.clientUDP;

import java.net.SocketException;
import java.net.UnknownHostException;

import salaAula.Sala;

public class MainClientUDP {

    private static final int NUMERO_SALAS = 4;

    private static final int PORTA_INICIAL = 1027;

    public static void main(String[] args) {

        for (int i = 0; i < NUMERO_SALAS; i++) {
            int PORTA = PORTA_INICIAL + i;
            int PORTA_DESTINO = PORTA + 1;
            int ID = 10 + i;
            int NUMERO_APARELHOS = 1 + i;
            new Thread(() -> {
                try {
                    MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(PORTA, PORTA_DESTINO, "127.0.0.1", ID, new Sala(NUMERO_APARELHOS));
                    microcontrolador.getSALA().encherSala();
                    microcontrolador.start();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        new Thread(() -> {
                try {
                    MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(((PORTA_INICIAL+NUMERO_SALAS)), 1026, "127.0.0.1", ((10+NUMERO_SALAS)), new Sala(4));
                    microcontrolador.getSALA().encherSala();
                    microcontrolador.start();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            }).start();

    }
}
