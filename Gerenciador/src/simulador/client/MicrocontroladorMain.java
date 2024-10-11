package simulador.client;

import salaAula.Sala;

public class MicrocontroladorMain {
    public static void main(String[] args) {
        Sala sala = new Sala(2);

        sala.encherSala();

        MicrocontroladorSocket microcontroladorSocket = new MicrocontroladorSocket("127.0.0.1", 5001, 3, sala, true);

        microcontroladorSocket.start();
    }
}
