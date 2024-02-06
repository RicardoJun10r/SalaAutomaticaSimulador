package SimulacaoV2.clientV2;

import salaAula.Sala;
import util.MessageV2;

public class MicrocontroladorV2 {
    
    private String ID;

    private MessageV2 socket;

    private final Sala sala;

    public MicrocontroladorV2(Sala sala){
        this.sala = sala;
    }

}
