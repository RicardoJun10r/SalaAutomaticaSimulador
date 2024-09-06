package simulacao_v6.sever_v9;

import java.util.Scanner;

import util.ServerReq;
import util.MenuInterface;
import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class Server_V9 {
    
    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    private Scanner scanner;

    private final Boolean DEBUG;

    public Server_V9(String host, int porta, boolean debug) {
        this.PORTA = porta;
        this.HOST = host;
        this.scanner = new Scanner(System.in);
        this.DEBUG = debug;
    }

    public void start() {
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.OBJETO);

        this.server.iniciar();

        this.metodo_escutar = () -> {
            SocketClientSide cliente = this.server.filaClientes();
            if (cliente != null) {
                ServerReq line;
                while ((line = (ServerReq) cliente.receberObjeto()) != null) {
                    if(DEBUG){
                        System.out.println("DEBUG [" + cliente.getEndereco() + ":" + cliente.getPorta() + "]: " + line.toString());
                    }
                    if(line.getHeaders().equalsIgnoreCase("res")){
                        System.out.println(line.getMensagem());
                    } else if(line.getHeaders().equalsIgnoreCase("fwd")){
                        if(line.getMicrocontrolador_id() <= 2){
                            this.server.unicast(line.getMicrocontrolador_id(), line);
                        } else {
                            this.server.broadcast(line);
                        }
                    } else if(line.getHeaders().equalsIgnoreCase("mic")){
                        if(!this.server.verificarConexao(line.getEndereco(), line.getPorta())){
                            adicionarConexao(line.getEndereco(), line.getPorta());
                        }
                        this.server.unicast(line.getEndereco(), line.getPorta(), new ServerReq(this.HOST, line.getPorta(), "res", line.toString(), -1, -1));
                    }
                }
            }
        };

        this.metodo_enviar = () -> {
            Integer op, microcontrolador, server, id_server, id_microcontrolador, porta;
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
                            id_microcontrolador = this.scanner.nextInt();   
                            this.server.unicast(id_microcontrolador, new ServerReq(this.HOST, this.PORTA, "req", "SERVIDOR", microcontrolador, id_microcontrolador));
                        } else {
                            this.server.broadcast(new ServerReq(this.HOST, this.PORTA, "req", "SERVIDOR", microcontrolador, -1));
                        }
                        break;
                    }
                    case 1: {
                        MenuInterface.controlarServer();
                        server = this.scanner.nextInt();
                        System.out.println("ID DO SERVER:");
                        id_server = this.scanner.nextInt();
                        if(server <= 2){
                            System.out.println("ID DO MICROCONTROLADOR:");
                            id_microcontrolador = this.scanner.nextInt();
                            this.server.unicast(id_server, new ServerReq(this.HOST, this.PORTA, "fwd", "SERVIDOR", server, id_microcontrolador));
                        } else {
                            this.server.unicast(id_server, new ServerReq(this.HOST, this.PORTA, "fwd", "SERVIDOR", server, -1));
                        }
                        break;
                    }
                    case 2: {
                        this.server.listarConexoes();
                        break;
                    }
                    case 3: {
                        System.out.println("DIGITE O ENDEREÇO:");
                        endereco = scanner.next();
                        System.out.println("DIGITE A PORTA:");
                        porta = scanner.nextInt();
                        adicionarConexao(endereco, porta);
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

    private void adicionarConexao(String endereco, int porta){
        SocketClientSide nova_conexao = new SocketClientSide(endereco, porta);
        nova_conexao.conectar();
        this.server.adicionar(nova_conexao);
    }

}
