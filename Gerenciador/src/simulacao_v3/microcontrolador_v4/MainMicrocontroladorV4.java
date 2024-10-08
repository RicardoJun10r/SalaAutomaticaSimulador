package simulacao_v3.microcontrolador_v4;

import java.io.IOException;

import salaAula.Sala;

public class MainMicrocontroladorV4 {
    public static void main(String[] args) {
        Sala sala = new Sala(2);

        sala.encherSala();

        MicrocontroladorV4 microcontroladorSocket = new MicrocontroladorV4("2", sala, "localhost", 2001);

        try {
            microcontroladorSocket.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
