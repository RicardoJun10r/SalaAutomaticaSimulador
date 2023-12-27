package TCP.clientTCP;

import salaAula.Sala;

public class MainClientTCP {
    
    private static final int NUMERO_SALAS = 4;

    public static void main(String[] args) {
        try {

            for(int i = 0; i < NUMERO_SALAS; i++){

                Sala sala = new Sala((i+1));
            
                sala.encherSala();

                MicrocontroladorTCP microcontroladorSocket = new MicrocontroladorTCP(i, sala);

                microcontroladorSocket.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
}
