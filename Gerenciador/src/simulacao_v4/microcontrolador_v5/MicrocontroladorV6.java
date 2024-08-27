package simulacao_v4.microcontrolador_v5;

import util.api.SocketClientSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;

public class MicrocontroladorV6 {
    
    private SocketClientSide socket;

    private ISocketListenFunction metodo_escutar;

    private final int PORTA;

    private final String HOST;

    public MicrocontroladorV6(String host, int porta){
        this.HOST = host;
        this.PORTA = porta;
    }

    public void start(){
        this.socket = new SocketClientSide(this.HOST, this.PORTA);

        this.socket.conectar();

        this.socket.configurarEntradaSaida(SocketType.TEXTO);

        this.metodo_escutar = () -> {
            String line;
            while ((line = this.socket.receberMensagem()) != null) {
                System.out.println(line);
                this.socket.enviarMensagem(
                    String.valueOf(Integer.parseInt(line) * 2)
                );
            }
        };

        this.socket.configurarMetodoEscutar(metodo_escutar);

        this.socket.escutar();
    }

}
