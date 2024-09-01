package simulacao_v4.server_v5;

import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class ServerV5 {
    
    private ISocketListenFunction metodo_escutar;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    public ServerV5(String host, int porta){
        this.PORTA = porta;
        this.HOST = host;
    }

    public void start(){
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.TEXTO);
        
        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaClientes();
                if(cliente != null){
                    String line;
                    while((line = cliente.receberMensagem()) != null){
                        System.out.println("RECEBIDO DE [" + cliente.getEndereco() + "]: " + line);
                        this.server.broadcast(cliente.getEndereco() + " " + line);
                        this.server.listarConexoes();
                    }
                }
            }
        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.escutar();
    }

}
