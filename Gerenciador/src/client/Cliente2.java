package client;

import salaAula.Sala;

public class Cliente2 {
    public static void main(String[] args) {
        try {

            Sala sala2 = new Sala(2);

            sala2.encherSala();

            Microcontrolador microcontroladorSocket2 = new Microcontrolador(1, sala2);

            microcontroladorSocket2.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }
}
