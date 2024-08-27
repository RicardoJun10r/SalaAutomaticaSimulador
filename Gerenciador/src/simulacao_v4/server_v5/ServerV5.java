package simulacao_v4.server_v5;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.Interface.ISocketListenFunction;

public class ServerV5 {
    
    private ISocketListenFunction metodo_escutar;

    private SocketServerSide server;

    private Queue<SocketClientSide> fila;

    private final int PORTA = 5000;

    public ServerV5(){
        this.fila = new ConcurrentLinkedQueue<>();
    }

    public void start(){
        this.server = new SocketServerSide("localhost", PORTA);
        
        this.server.iniciar();

        this.fila = this.server.Fila();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = fila.poll();
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
