package simulacao_v4.microcontrolador_v5;

import salaAula.Sala;

public class Microcontrolador_Main_V5 {
    public static void main(String[] args) {

        Sala sala = new Sala(2);

        sala.encherSala();

        MicrocontroladorV7 microcontroladorV7 = new MicrocontroladorV7("localhost", 5000, 2, sala, true);

        microcontroladorV7.start();
    }
}
