package simulacao_v4.server_v5;

import java.util.Scanner;

import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class ServerV6 {
    
    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    private Scanner scanner;

    public ServerV6(String host, int porta){
        this.PORTA = porta;
        this.HOST = host;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.TEXTO);
        
        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaRequisicoes();
                if(cliente != null){
                    String line;
                    while((line = cliente.receberMensagem()) != null){
                        System.out.println("RECEBIDO DE [" + cliente.getEndereco() + "]: " + line);
                    }
                }
            }
        };

        this.metodo_enviar = () -> {
            Integer op, num, id;
            do {

                System.out.println("DIGITE A OPÇÃO:\n[0] --> ENVIAR NÚMERO\n[1] --> LISTAR CONEXÕES\n[-1] SAIR");
                op = this.scanner.nextInt();
                switch (op) {
                    case 0:
                        System.out.println("DIGITE O NÚMERO:");
                        num = this.scanner.nextInt();
                        System.out.println("ID:");
                        id = this.scanner.nextInt();
                        this.server.unicast(id, num.toString());
                        break;
                    case 1:
                        this.server.listarConexoes();
                        break;
                    case -1:
                        break;
                    default:
                        System.out.println("ENTRADA INVÁLIDA");
                        break;
                }
                
            } while (op != -1);
        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.configurarMetodoEnviar(metodo_enviar);

        this.server.enviar();
        
        this.server.escutar();
    }

}
