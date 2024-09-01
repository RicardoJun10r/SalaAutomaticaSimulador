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

    private final Boolean DEBUG;

    public ServerV7(String host, int porta, boolean debug) {
        this.PORTA = porta;
        this.HOST = host;
        this.scanner = new Scanner(System.in);
        this.DEBUG = debug;
    }

    public void start() {
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.TEXTO);

        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaClientes();
                if (cliente != null) {
                    String line;
                    while ((line = cliente.receberMensagem()) != null) {
                        if(DEBUG){
                            System.out.println("DEBUG [" + cliente.getEndereco() + ":" + cliente.getPorta() + "]: " + line);
                        }
                        if(line.startsWith("fwd")){
                            String[] req = line.split(";");
                            if(req[1].equals("-1")){
                                this.server.broadcast("fwd;" + req[2] + ";" + req[3]);
                            } else if(req[1].equals("res")){
                                String endereco = req[3].split(":")[0];
                                int porta = Integer.parseInt(req[3].split(":")[1]);
                                if(!this.server.verificarConexao(endereco, porta)){
                                    System.out.println("adicionar conexao");
                                    adicionarConexao(endereco, porta);
                                }
                                this.server.unicast(endereco, porta, line);
                            } else {
                                this.server.unicast(Integer.parseInt(req[1]), "fwd;" + req[2] + ";" + req[3]);
                            }
                        } else {
                            System.out.println("RECEBIDO DE [" + cliente.getEndereco() + ":" + cliente.getPorta() + "]: " + line);
                        }
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
                            this.server.unicast(id_microcontrolador, microcontrolador.toString());
                        } else {
                            this.server.broadcast(microcontrolador.toString());
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
                            this.server.unicast(id_server, "fwd;" + id_microcontrolador + ";" + server.toString() + ";" + this.server.getEndereco() + ":" + this.server.getPorta());
                        } else {
                            this.server.unicast(id_server, "fwd;-1;" + server.toString() + ";" + this.server.getEndereco() + ":" + this.server.getPorta());
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
        nova_conexao.configurarEntradaSaida(SocketType.TEXTO);
        this.server.adicionar(nova_conexao);
    }

}
