package UDP.clientUDP;

import java.net.SocketException;
import java.net.UnknownHostException;

import salaAula.Sala;

public class MainClientUDP {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1027, 1028, "127.0.0.1", 10, new Sala(2));
                microcontrolador.getSALA().encherSala();
                microcontrolador.start();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1028, 1029, "127.0.0.1", 11, new Sala(3));
                microcontrolador.getSALA().encherSala();
                microcontrolador.start();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1029, 1030, "127.0.0.1", 12, new Sala(4));
                microcontrolador.getSALA().encherSala();
                microcontrolador.start();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                MicrocontroladorUDP microcontrolador = new MicrocontroladorUDP(1030, 1026, "127.0.0.1", 13, new Sala(5));
                microcontrolador.getSALA().encherSala();
                microcontrolador.start();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
