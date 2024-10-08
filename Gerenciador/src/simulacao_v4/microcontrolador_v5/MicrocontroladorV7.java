package simulacao_v4.microcontrolador_v5;

import salaAula.Sala;
import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class MicrocontroladorV7 {

    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private final int PORTA;

    private final String HOST;

    private final Sala SALA;

    private final Integer ID;

    private final Boolean DEBUG;

    public MicrocontroladorV7(String host, int porta, int id, Sala sala, boolean debug) {
        this.HOST = host;
        this.PORTA = porta;
        this.SALA = sala;
        this.ID = id;
        this.DEBUG = debug;
    }

    public void start() {
        this.socket = new SocketClientSide(this.HOST, this.PORTA);

        this.socket.conectar();

        this.socket.configurarEntradaSaida(SocketType.TEXTO);

        this.metodo_escutar = () -> {
            String line;
            while ((line = this.socket.receberMensagem()) != null) {
                String[]req = line.split(";");
                String op, head, tail;
                if (DEBUG) {
                    System.out.println("DEBUG: " + line);
                }
                if(req[0].equals("fwd")){
                    op = req[1];
                    head = "fwd;res;";
                    tail = ";" + req[2];
                }else {
                    op = line;
                    head = "";
                    tail = "";
                }
                switch(op) {
                    case "0", "4": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos() + tail);
                        break;
                    }
                    case "1", "3": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos() + tail);
                        break;
                    }
                    case "2", "5": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos() + tail);
                        break;
                    }
                    default: {
                        this.socket.enviarMensagem("ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                        break;
                    }
                }
            }
        };

        this.socket.configurarMetodoEscutar(metodo_escutar);

        this.socket.escutar();
    }

}
