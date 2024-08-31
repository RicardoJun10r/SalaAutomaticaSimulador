package simulacao_v5.server_v8;

import simulacao_v5.Mensagem;
import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class Server_V8 {

    private ISocketListenFunction metodo_escutar;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    public Server_V8(String host, int porta){
        this.PORTA = porta;
        this.HOST = host;
    }

    public void start(){
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.OBJETO);
        
        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaRequisicoes();
                if(cliente != null){
                    Mensagem line;
                    while((line = (Mensagem) cliente.receberObjeto()) != null){
                        System.out.println(line.toString());
                        this.server.broadcast(line);
                    }
                }
            }
        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.escutar();
    }

}
