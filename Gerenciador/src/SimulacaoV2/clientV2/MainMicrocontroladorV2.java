package SimulacaoV2.clientV2;

import salaAula.Sala;

public class MainMicrocontroladorV2 {
    public static void main(String[] args) {
        MicrocontroladorV2 microcontroladorV2 = new MicrocontroladorV2(new Sala(4), 1030, 1025, "127.0.0.1", "127.0.0.1", "M1");
        microcontroladorV2.getSala().encherSala();
        microcontroladorV2.start();
    }
}
