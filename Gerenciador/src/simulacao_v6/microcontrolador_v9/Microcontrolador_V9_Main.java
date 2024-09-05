package simulacao_v6.microcontrolador_v9;

import salaAula.Sala;

public class Microcontrolador_V9_Main {
    public static void main(String[] args) {
        Sala sala = new Sala(2);

        sala.encherSala();

        Microcontrolador_V9 microcontrolador_V9 = new Microcontrolador_V9("127.0.0.1", 5001, 2, sala, true);

        microcontrolador_V9.start();
    }
}
