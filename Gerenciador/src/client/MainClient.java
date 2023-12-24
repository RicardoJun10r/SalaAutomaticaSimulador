package client;

import Class.Sala;

public class MainClient {
    
    private static final int NUMERO_SALAS = 4;

    public static void main(String[] args) {
        try {
            // Sala sala1 = new Sala(1);
            
            // sala1.encherSala();

            // MicrocontroladorSocket microcontroladorSocket1 = new MicrocontroladorSocket(0, sala1);

            // microcontroladorSocket1.start();

            // Sala sala2 = new Sala(2);

            // sala2.encherSala();

            // MicrocontroladorSocket microcontroladorSocket2 = new MicrocontroladorSocket(1, sala2);

            // microcontroladorSocket2.start();

            for(int i = 0; i < NUMERO_SALAS; i++){

                Sala sala = new Sala((i+1));
            
                sala.encherSala();

                MicrocontroladorSocket microcontroladorSocket = new MicrocontroladorSocket(i, sala);

                microcontroladorSocket.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
}
