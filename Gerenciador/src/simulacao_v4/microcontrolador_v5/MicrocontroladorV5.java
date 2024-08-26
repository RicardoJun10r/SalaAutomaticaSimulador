package simulacao_v4.microcontrolador_v5;

import java.util.Scanner;

import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class MicrocontroladorV5 {
    
    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private Scanner scan;

    public MicrocontroladorV5(){
        this.scan = new Scanner(System.in);
    }

    public void start(){
        this.socket = new SocketClientSide("localhost", 5000);

        this.socket.conectar();

        this.socket.configurarEntradaSaida(SocketType.TEXTO);

        this.metodo_escutar = () -> {
            String line;
            while ((line = this.socket.receberMensagem()) != null) {
                System.out.println(line);
            }
        };

        this.metodo_enviar = () -> {
            String msg = "";
            do {
                System.out.println("Digite algo:");
                msg = this.scan.nextLine();
                this.socket.enviarMensagem(msg);      
            } while (msg.equalsIgnoreCase("sair"));
        };

        this.socket.configurarMetodoEscutar(metodo_escutar);

        this.socket.configurarMetodoEnviar(metodo_enviar);

        this.socket.escutar();

        this.socket.enviar();
    }
}
