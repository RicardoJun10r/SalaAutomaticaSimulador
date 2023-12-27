package UDP.clientUDP;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import salaAula.Sala;

public class MainClientUDP {

    private static final int NUMERO_SALAS = 2;

    private static final int PORTA_INICIAL = 1027;

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUMERO_SALAS; i++) {
            int PORTA_DESTINO = PORTA_INICIAL + i;
            int ID = 10 + i;
            int NUMERO_APARELHOS = 1 + i;
            threads.add(
                new Thread(() -> {
                    try {
                        MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(PORTA_INICIAL, PORTA_DESTINO, "127.0.0.1", ID, new Sala(NUMERO_APARELHOS));
                        microcontrolador.getSALA().encherSala();
                        microcontrolador.start();
                    } catch (SocketException | UnknownHostException e) {
                        e.printStackTrace();
                    }
                })
            );
        }

        Iterator<Thread> iterator = threads.iterator();

        while (iterator.hasNext()) {
            Thread index = iterator.next();
            index.start();
        }

        // new Thread(() -> {
        //     try {
        //         MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1027, 1028, "127.0.0.1", 10, new Sala(2));
        //         microcontrolador.getSALA().encherSala();
        //         microcontrolador.start();
        //     } catch (SocketException | UnknownHostException e) {
        //         e.printStackTrace();
        //     }
        // }).start();

        // new Thread(() -> {
        //     try {
        //         MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1028, 1029, "127.0.0.1", 11, new Sala(3));
        //         microcontrolador.getSALA().encherSala();
        //         microcontrolador.start();
        //     } catch (SocketException | UnknownHostException e) {
        //         e.printStackTrace();
        //     }
        // }).start();

        // new Thread(() -> {
        //     try {
        //         MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1029, 1030, "127.0.0.1", 12, new Sala(4));
        //         microcontrolador.getSALA().encherSala();
        //         microcontrolador.start();
        //     } catch (SocketException | UnknownHostException e) {
        //         e.printStackTrace();
        //     }
        // }).start();

        // new Thread(() -> {
        //     try {
        //         MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1030, 1026, "127.0.0.1", 13, new Sala(5));
        //         microcontrolador.getSALA().encherSala();
        //         microcontrolador.start();
        //     } catch (SocketException | UnknownHostException e) {
        //         e.printStackTrace();
        //     }
        // }).start();
    }
}
