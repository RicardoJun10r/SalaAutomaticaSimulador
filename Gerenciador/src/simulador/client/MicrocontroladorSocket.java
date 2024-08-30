package simulador.client;

import salaAula.Sala;
import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class MicrocontroladorSocket {

    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private final int PORTA;

    private final String HOST;

    private final Sala SALA;

    private final Integer ID;

    private final Boolean DEBUG;

    public MicrocontroladorSocket(String host, int porta, int id, Sala sala, boolean debug) {
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
                String op, head;
                if (DEBUG) {
                    System.out.println("DEBUG: " + line);
                }
                if(req[0].equals("fwd")){
                    op = req[3];
                    head = "res;" + req[1] + ";" + req[2] + ";";
                }else {
                    op = line;
                    head = "";
                }
                switch(op) {
                    case "0", "4": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                        break;
                    }
                    case "1", "3": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                        break;
                    }
                    case "2", "5": {
                        this.socket.enviarMensagem(head + "ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
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
