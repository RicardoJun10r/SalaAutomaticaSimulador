package simulacao_v5.microcontrolador_v8;

import java.util.Scanner;

import simulacao_v5.Mensagem;
import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class Microcontrolador_V8 {
 
    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private Scanner scan;

    private final int PORTA;

    private final String HOST;

    public Microcontrolador_V8(String host, int porta){
        this.HOST = host;
        this.PORTA = porta;
        this.scan = new Scanner(System.in);
    }

    public void start(){
        this.socket = new SocketClientSide(this.HOST, this.PORTA);

        this.socket.conectar();

        this.socket.configurarEntradaSaida(SocketType.OBJETO);

        this.metodo_escutar = () -> {
            Mensagem line;
            while ((line = (Mensagem) this.socket.receberObjeto()) != null) {
                System.out.println(line.getEndereco() + ":" + line.getPorta() + "[" + line.getHorario().toString() + "]: " + line.getMensagem());
            }
        };

        this.metodo_enviar = () -> {
            String msg = "";
            do {
                System.out.println("Digite algo:");
                msg = this.scan.nextLine();
                this.socket.enviarObjeto(new Mensagem(this.HOST, this.PORTA, "text", msg));
            } while (!msg.equalsIgnoreCase("sair"));
        };

        this.socket.configurarMetodoEscutar(metodo_escutar);

        this.socket.configurarMetodoEnviar(metodo_enviar);

        this.socket.escutar();
        
        this.socket.enviar();
    }

}
