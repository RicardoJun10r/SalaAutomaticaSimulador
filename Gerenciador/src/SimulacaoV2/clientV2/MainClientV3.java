package SimulacaoV2.clientV2;

import java.io.IOException;

import salaAula.Sala;

public class MainClientV3 {
    public static void main(String[] args) {
        Sala sala = new Sala(2);

        sala.encherSala();

        MicrocontroladorV3 microcontroladorSocket = new MicrocontroladorV3("2", sala, "localhost", 2000);

        try {
            microcontroladorSocket.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
