package simulacao_v6.microcontrolador_v9;

import salaAula.Sala;
import util.ServerReq;
import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class Microcontrolador_V9 {

    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private final int PORTA;

    private final String HOST;

    private final Sala SALA;

    private final Integer ID;

    private final Boolean DEBUG;

    public Microcontrolador_V9(String host, int porta, int id, Sala sala, boolean debug) {
        this.HOST = host;
        this.PORTA = porta;
        this.SALA = sala;
        this.ID = id;
        this.DEBUG = debug;
    }

    public void start() {
        this.socket = new SocketClientSide(this.HOST, this.PORTA);

        this.socket.conectar();

        this.socket.configurarEntradaSaida(SocketType.OBJETO);

        this.metodo_escutar = () -> {
            ServerReq line;
            while ((line = (ServerReq) this.socket.receberObjeto()) != null) {
                if(DEBUG){
                    System.out.println(line.toString());
                }
                if (line.getHeaders().equalsIgnoreCase("fwd"))
                    line.setHeaders("mic");
                switch (line.getOpcao()) {
                    case 0, 4: {
                        this.socket.enviarObjeto(
                                new ServerReq(line.getEndereco(),
                                        line.getPorta(),
                                        line.getHeaders(),
                                        ("ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos()),
                                        line.getOpcao(),
                                        line.getMicrocontrolador_id()));
                        break;
                    }
                    case 1, 3: {
                        this.socket.enviarObjeto(
                                new ServerReq(line.getEndereco(),
                                        line.getPorta(),
                                        line.getHeaders(),
                                        ("ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos()),
                                        line.getOpcao(),
                                        line.getMicrocontrolador_id()));
                        break;
                    }
                    case 2, 5: {
                        this.socket.enviarObjeto(
                                new ServerReq(line.getEndereco(),
                                        line.getPorta(),
                                        line.getHeaders(),
                                        ("ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos()),
                                        line.getOpcao(),
                                        line.getMicrocontrolador_id()));
                        break;
                    }
                    default: {
                        this.socket.enviarObjeto(
                                new ServerReq(line.getEndereco(),
                                        line.getPorta(),
                                        line.getHeaders(),
                                        ("ID = [ " + ID + " ]: " + "ERRO: opção inválida!"),
                                        line.getOpcao(),
                                        line.getMicrocontrolador_id()));
                        break;
                    }
                }
            }
        };

        this.socket.configurarMetodoEscutar(metodo_escutar);

        this.socket.escutar();
    }

}
