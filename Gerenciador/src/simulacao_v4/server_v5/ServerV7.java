package simulacao_v4.server_v5;

import util.MenuInterface;
import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;
import java.util.Scanner;

public class ServerV7 {

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    private Scanner scanner;

    public ServerV7(String host, int porta) {
        this.PORTA = porta;
        this.HOST = host;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.TEXTO);

        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaRequisicoes();
                if (cliente != null) {
                    String line;
                    while ((line = cliente.receberMensagem()) != null) {
                        System.out.println("RECEBIDO DE [" + cliente.getEndereco() + "]: " + line);
                    }
                }
            }
        };

        this.metodo_enviar = () -> {
            Integer op, microcontrolador, server, id, porta;
            String endereco;
            do {
                MenuInterface.mostrarOpcoes();
                op = this.scanner.nextInt();
                switch (op) {
                    case 0: {
                        MenuInterface.microcontroladorOpcoes();
                        microcontrolador = this.scanner.nextInt();
                        if(microcontrolador <= 2){
                            System.out.println("ID DO MICROCONTROLADOR:");
                            id = this.scanner.nextInt();
                            this.server.unicast(id, microcontrolador.toString());
                        } else {
                            this.server.broadcast(microcontrolador.toString());
                        }
                        break;
                    }
                    case 1: {
                        MenuInterface.controlarServer();
                        server = this.scanner.nextInt();
                        if(server <= 3){
                            System.out.println("ID DO SERVER:");
                            id = this.scanner.nextInt();
                            this.server.unicast(id, server.toString());
                        } else {
                            this.server.broadcast(server.toString());
                        }
                        break;
                    }
                    case 2: {
                        this.server.listarConexoes();
                        break;
                    }
                    case 3: {
                        System.out.println("DIGITE O ENDEREÇO:");
                        endereco = scanner.nextLine();
                        System.out.println("DIGITE A PORTA:");
                        porta = scanner.nextInt();
                        SocketClientSide nova_conexao = new SocketClientSide(endereco, porta);
                        this.server.conectar(nova_conexao);
                        break;
                    }
                    default: {
                        System.out.println("ENTRADA INVÁLIDA");
                        break;
                    }
                }

            } while (op != -1);
        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.configurarMetodoEnviar(metodo_enviar);

        this.server.enviar();

        this.server.escutar();
    }

}
