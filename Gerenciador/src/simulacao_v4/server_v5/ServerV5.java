package simulacao_v4.server_v5;

import util.api.SocketServerSide;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class ServerV5 {
    
    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_escrever;

    private SocketServerSide server;

    private final int PORTA = 5000;

    public void start(){
        this.server = new SocketServerSide("localhost", PORTA);

        this.metodo_escutar = () -> {
            
        };

        this.metodo_escrever = () -> {

        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.configurarMetodoEnviar(metodo_escrever);

        this.server.iniciar();

    }

}
